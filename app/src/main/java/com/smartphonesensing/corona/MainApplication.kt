package com.smartphonesensing.corona

import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import androidx.work.*
import com.smartphonesensing.corona.trustchain.TrustchainService
import com.smartphonesensing.corona.util.ContactsCreationWorker
import com.smartphonesensing.corona.util.DP3THelper
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.tudelft.ipv8.IPv8Configuration
import nl.tudelft.ipv8.OverlayConfiguration
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.android.keyvault.AndroidCryptoProvider
import nl.tudelft.ipv8.android.messaging.bluetooth.BluetoothLeDiscovery
import nl.tudelft.ipv8.android.peerdiscovery.NetworkServiceDiscovery
import nl.tudelft.ipv8.attestation.trustchain.*
import nl.tudelft.ipv8.attestation.trustchain.store.TrustChainSQLiteStore
import nl.tudelft.ipv8.attestation.trustchain.store.TrustChainStore
import nl.tudelft.ipv8.attestation.trustchain.validation.TransactionValidator
import nl.tudelft.ipv8.keyvault.PrivateKey
import nl.tudelft.ipv8.keyvault.defaultCryptoProvider
import nl.tudelft.ipv8.peerdiscovery.DiscoveryCommunity
import nl.tudelft.ipv8.peerdiscovery.strategy.PeriodicSimilarity
import nl.tudelft.ipv8.peerdiscovery.strategy.RandomChurn
import nl.tudelft.ipv8.peerdiscovery.strategy.RandomWalk
import nl.tudelft.ipv8.sqldelight.Database
import nl.tudelft.ipv8.util.hexToBytes
import nl.tudelft.ipv8.util.toHex
import nl.tudelft.trustchain.common.util.TrustChainHelper
import okhttp3.CertificatePinner
import org.dpppt.android.sdk.DP3T
import org.dpppt.android.sdk.InfectionStatus
import org.dpppt.android.sdk.internal.BluetoothAdvertiseMode
import org.dpppt.android.sdk.internal.BluetoothTxPowerLevel
import org.dpppt.android.sdk.internal.crypto.CryptoModule
import org.dpppt.android.sdk.internal.crypto.SKList
import org.dpppt.android.sdk.internal.database.models.ExposureDay
import org.dpppt.android.sdk.internal.util.ProcessUtil
import org.dpppt.android.sdk.util.SignatureUtil
import java.util.concurrent.TimeUnit

class MainApplication : Application() {

    val BUCKET_PUBLIC_KEY =
        "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUZrd0V3WUhLb1pJemowQ0FRWUlLb1pJemowREFRY0RRZ0" +
        "FFdkxXZHVFWThqcnA4aWNSNEpVSlJaU0JkOFh2UgphR2FLeUg2VlFnTXV2Zk1JcmxrNk92QmtKeHdhbUdN" +
        "RnFWYW9zOW11di9rWGhZdjF1a1p1R2RjREJBPT0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg=="

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        initDP3T()
        initIPv8()

        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
//        val createContactsWorkRequest =
//            PeriodicWorkRequestBuilder<ContactsCreationWorker>(
//                CryptoModule.MILLISECONDS_PER_EPOCH.toLong(), TimeUnit.MILLISECONDS,
//                5, TimeUnit.MINUTES)
//                .addTag(ContactsCreationWorker.WORK_NAME)
//                .build()
        val createContactsWorkRequest =
            PeriodicWorkRequestBuilder<ContactsCreationWorker>(
                1, TimeUnit.MINUTES)
                .addTag(ContactsCreationWorker.WORK_NAME)
                .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                ContactsCreationWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                createContactsWorkRequest
            )
    }

    private fun initDP3T() {
        if (ProcessUtil.isMainProcess(this)) {
            registerReceiver(contactUpdateReceiver, DP3T.getUpdateIntentFilter())
            val publicKey = SignatureUtil.getPublicKeyFromBase64OrThrow(
                BUCKET_PUBLIC_KEY
            )
            DP3T.init(this, "demo.dpppt.org", true, publicKey)
            val certificatePinner: CertificatePinner = CertificatePinner.Builder()
                .add("demo.dpppt.org", "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=")
                .build()
            DP3T.setCertificatePinner(certificatePinner)
        }
        DP3THelper.setContext(this)
        DP3THelper.setupAppConfiguration(
            attenuationThreshold = 100.0F,
            bluetoothTxPowerLevel = BluetoothTxPowerLevel.ADVERTISE_TX_POWER_LOW,
            bluetoothAdvertiseMode = BluetoothAdvertiseMode.ADVERTISE_MODE_BALANCED
        )
    }

    private val contactUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
//            val secureStorage: SecureStorage = SecureStorage.getInstance(context)!!
            val status = DP3T.getStatus(context)
            if (status.infectionStatus == InfectionStatus.EXPOSED) {
                var exposureDay: ExposureDay? = null
                var dateNewest: Long = 0
                for (day in status.exposureDays) {
                    if (day.exposedDate.startOfDayTimestamp > dateNewest) {
                        exposureDay = day
                        dateNewest = day.exposedDate.startOfDayTimestamp
                    }
                }
//                if (exposureDay != null && secureStorage.lastShownContactId != exposureDay.id) {
//                    createNewContactNotifaction(context, exposureDay.id)
//                }
            }
        }
    }


    private fun initIPv8() {

        defaultCryptoProvider = AndroidCryptoProvider

        val config = IPv8Configuration(overlays = listOf(
            createDiscoveryCommunity(bluetooth = false),
            createTrustChainCommunity()
        ), walkerInterval = 5.0)

        IPv8Android.Factory(this)
            .setConfiguration(config)
            .setPrivateKey(getPrivateKey())
            .setServiceClass(TrustchainService::class.java)
            .init()

        initTrustChain()
    }

    private fun initTrustChain() {

        val ipv8 = IPv8Android.getInstance()
        val trustchain = ipv8.getOverlay<TrustChainCommunity>()!!
        val trustChainHelper: TrustChainHelper = TrustChainHelper(trustchain)

        /**
         * Step 2: Proposal block validator
         * Step 1 is in TrustChainCommunity.kt
         *
         * Optionally, we can register TransactionValidator for our block type to enforce integrity requirements.
         * In the validate method, we also get access to TrustChainStore if there we need to define any
         * context-sensitive rules depending on the previous blocks in the chain. Invalid blocks are
         * discarded and not stored in the database.
         *
         * In our case, we require proposal blocks to include a message field. We accept any transaction
         * for agreement blocks as we are only concerned with the signature.
         */
        trustchain.registerTransactionValidator(CORONA_BLOCK_TYPE, object : TransactionValidator {
            override fun validate(
                block: TrustChainBlock,
                database: TrustChainStore
            ): Boolean {
                Log.d("CoronaChain", "validator called for block: ${block.blockId} \n previousblock: ${block.previousHash}\n is proposal ${block.isProposal} \n isAgreement: ${block.isAgreement} \n is Genesis: ${block.isGenesis}")
                return block.transaction["SKList"] != null || block.isAgreement || block.transaction["message"] != null
            }
        })


        /**
         * Step 3: Agreement block creator
         *
         * Listener for incoming proposal blocks that we should create agreement blocks for.
         * If user should sign, manually call TrustChainCommunity.createAgreementBlock() on user action.
         *
         * At the point when the onSignatureRequest method is called,
         * the last few blocks (the exact count can be configured in TrustChainSettings.validationRange)
         * have already been validated.
         *
         * Right now it signs all valid blocks with simple transaction
         */
        trustchain.registerBlockSigner(CORONA_BLOCK_TYPE, object : BlockSigner {
            override fun onSignatureRequest(block: TrustChainBlock) {
                Log.d("CoronaChain", "Create agreement block for incoming block: ${block.blockId} \n previousblock: ${block.previousHash}\n is proposal ${block.isProposal} \n isAgreement: ${block.isAgreement} \n is Genesis: ${block.isGenesis}")
//                trustchain.createAgreementBlock(block, block.transaction)
            }
        })

        /**
         * Step 4: Incoming block listener, activated also when block proposal block is created
         *
         * We can register a block listener to be notified about all incoming blocks.
         * When the listener methods get called,
         * the block has already been validated using the registered TransactionValidator,
         * so we can just assume all received blocks are valid.
         *
         * There can be multiple listeners registered for a single block type and
         * a listener can be removed with TrustChainCommunity.removeListener method.
         *
         * param 1 : block type to listen to
         * param 2 : object implementing BlockListener interface
         */
        trustchain.addListener(CORONA_BLOCK_TYPE, object : BlockListener {
            override fun onBlockReceived(block: TrustChainBlock) {
                Log.d("CoronaChain", "listener called for action on block: ${block.blockId} \n previousblock: ${block.previousHash}\n is proposal ${block.isProposal} \n isAgreement: ${block.isAgreement} \n is Genesis: ${block.isGenesis}")

                trustChainHelper.updateBlocks()

                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val healthOfficial = sharedPreferences.getString("pref_ipv8_health_official", null)
                if (healthOfficial != null) {
                    if (block.isAgreement && healthOfficial == block.publicKey.toHex()) {
                        val stringSKList: ArrayList<Map<String, String>> =
                            block.transaction["SKList"] as ArrayList<Map<String, String>>
                        val SKList: SKList = trustChainHelper.stringSKListToSKList(stringSKList)
                        DP3THelper.checkContactsForSKList(SKList)
                    }
                }
            }
        })

    }



    private fun createDiscoveryCommunity(bluetooth: Boolean): OverlayConfiguration<DiscoveryCommunity> {
        val randomWalk = RandomWalk.Factory()
        val randomChurn = RandomChurn.Factory()
        val periodicSimilarity = PeriodicSimilarity.Factory()

        val nsd = NetworkServiceDiscovery.Factory(getSystemService()!!)
        val bluetoothManager = getSystemService<BluetoothManager>()
            ?: throw IllegalStateException("BluetoothManager not available")
        val strategies = mutableListOf(
            randomWalk, randomChurn, periodicSimilarity, nsd
        )
        if (bluetoothManager.adapter != null && Build.VERSION.SDK_INT >= 24 && bluetooth) {
            val ble = BluetoothLeDiscovery.Factory()
            strategies += ble
        }

        return OverlayConfiguration(
            DiscoveryCommunity.Factory(),
            strategies
        )
    }

    private fun createTrustChainCommunity(): OverlayConfiguration<TrustChainCommunity> {
        val settings = TrustChainSettings()
        val driver = AndroidSqliteDriver(Database.Schema, this, "trustchain.db")
        val store = TrustChainSQLiteStore(Database(driver))
        val randomWalk = RandomWalk.Factory()
        return OverlayConfiguration(
            TrustChainCommunity.Factory(settings, store),
            listOf(randomWalk)
        )
    }

    private fun getPrivateKey(): PrivateKey {
        // Load a key from the shared preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val privateKey = prefs.getString(PREF_PRIVATE_KEY, null)
        return if (privateKey == null) {
            // Generate a new key on the first launch
            val newKey = AndroidCryptoProvider.generateKey()
            prefs.edit()
                .putString(PREF_PRIVATE_KEY, newKey.keyToBin().toHex())
                .apply()
            newKey
        } else {
            AndroidCryptoProvider.keyFromPrivateBin(privateKey.hexToBytes())
        }
    }

    companion object {
        private const val PREF_PRIVATE_KEY = "private_key"
        private const val DEMO_BLOCK_TYPE = "demo_block"
        private const val CORONA_BLOCK_TYPE = "corona_block"
    }

}