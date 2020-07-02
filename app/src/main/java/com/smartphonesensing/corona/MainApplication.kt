package com.smartphonesensing.corona

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.smartphonesensing.corona.storage.SecureStorage
//import com.smartphonesensing.corona.trustchain.CoronaCommunity
import com.smartphonesensing.corona.trustchain.CoronaPayload
import com.smartphonesensing.corona.trustchain.MyMessage
import com.smartphonesensing.corona.trustchain.TrustchainService
import com.smartphonesensing.corona.util.DP3THelper
import com.smartphonesensing.corona.util.NotificationUtil
import com.squareup.sqldelight.android.AndroidSqliteDriver
import nl.tudelft.ipv8.IPv8Configuration
import nl.tudelft.ipv8.Overlay
import nl.tudelft.ipv8.OverlayConfiguration
import nl.tudelft.ipv8.Peer
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
import nl.tudelft.ipv8.messaging.Packet
import nl.tudelft.ipv8.peerdiscovery.DiscoveryCommunity
import nl.tudelft.ipv8.peerdiscovery.strategy.PeriodicSimilarity
import nl.tudelft.ipv8.peerdiscovery.strategy.RandomChurn
import nl.tudelft.ipv8.peerdiscovery.strategy.RandomWalk
import nl.tudelft.ipv8.sqldelight.Database
import nl.tudelft.ipv8.util.hexToBytes
import nl.tudelft.ipv8.util.toHex
import okhttp3.CertificatePinner
import okhttp3.internal.wait
import org.dpppt.android.sdk.DP3T
import org.dpppt.android.sdk.InfectionStatus
import org.dpppt.android.sdk.internal.AppConfigManager
import org.dpppt.android.sdk.internal.crypto.CryptoModule
import org.dpppt.android.sdk.internal.database.models.ExposureDay
import org.dpppt.android.sdk.internal.util.ProcessUtil
import org.dpppt.android.sdk.util.SignatureUtil

class MainApplication : Application() {

    val BUCKET_PUBLIC_KEY =
        "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUZrd0V3WUhLb1pJemowQ0FRWUlLb1pJemowREFRY0RRZ0FFdkxXZHVFWThqcnA4aWNSNEpVSlJaU0JkOFh2UgphR2FLeUg2VlFnTXV2Zk1JcmxrNk92QmtKeHdhbUdNRnFWYW9zOW11di9rWGhZdjF1a1p1R2RjREJBPT0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg=="
    val ACTION_GOTO_REPORTS = "ACTION_GOTO_REPORTS"

    override fun onCreate() {
        super.onCreate()

        initDP3T()
        initIPv8()

    }

    private fun initDP3T() {
        if (ProcessUtil.isMainProcess(this)) {
            registerReceiver(contactUpdateReceiver, DP3T.getUpdateIntentFilter())
            val publicKey = SignatureUtil.getPublicKeyFromBase64OrThrow(
                BUCKET_PUBLIC_KEY
            )
            DP3T.init(this, "com.smartphonesensing.corona", true, publicKey)
            val certificatePinner: CertificatePinner = CertificatePinner.Builder()
                .add("com.smartphonesensing.corona", "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=")
                .build()
            DP3T.setCertificatePinner(certificatePinner)
            DP3THelper.setCryptoModule(CryptoModule.getInstance(this))
            DP3THelper.setAppConfigManager(AppConfigManager.getInstance(this))
        }
        //demo.dpppt.org
    }

    private val contactUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val secureStorage: SecureStorage = SecureStorage.getInstance(context)!!
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
                if (exposureDay != null && secureStorage.lastShownContactId != exposureDay.id) {
                    createNewContactNotifaction(context, exposureDay.id)
                }
            }
        }
    }

    private fun createNewContactNotifaction(
        context: Context,
        contactId: Int
    ) {
        val secureStorage: SecureStorage = SecureStorage.getInstance(context)!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotificationChannel(context)
        }
        val resultIntent = Intent(context, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        resultIntent.action = ACTION_GOTO_REPORTS
        val pendingIntent =
            PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(
            context,
            NotificationUtil.NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle(context.getString(R.string.push_exposed_title))
            .setContentText(context.getString(R.string.push_exposed_text))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSmallIcon(R.drawable.ic_begegnungen)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NotificationUtil.NOTIFICATION_ID_CONTACT, notification)
        secureStorage.isHotlineCallPending = true
        secureStorage.isReportsHeaderAnimationPending = true
        secureStorage.lastShownContactId = contactId
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
        trustchain.registerTransactionValidator(DEMO_BLOCK_TYPE, object : TransactionValidator {
            override fun validate(
                block: TrustChainBlock,
                database: TrustChainStore
            ): Boolean {
                Log.d("TrustChainDemo", "validator called for block: ${block.blockId} \n previousblock: ${block.previousHash}\n is proposal ${block.isProposal} \n isAgreement: ${block.isAgreement} \n is Genesis: ${block.isGenesis}")
                return block.transaction["messageKey"] != null || block.isAgreement
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
        trustchain.registerBlockSigner(DEMO_BLOCK_TYPE, object : BlockSigner {
            override fun onSignatureRequest(block: TrustChainBlock) {
                Log.d("TrustChainDemo", "Create agreement block for incoming block: ${block.blockId} \n previousblock: ${block.previousHash}\n is proposal ${block.isProposal} \n isAgreement: ${block.isAgreement} \n is Genesis: ${block.isGenesis}")
//                val tx = mapOf<Any?, Any?>("messageKey2" to "messageValue2")
                trustchain.createAgreementBlock(block, block.transaction)
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
        trustchain.addListener(DEMO_BLOCK_TYPE, object : BlockListener {
            override fun onBlockReceived(block: TrustChainBlock) {
                Log.d("CoronaChain", "listener called for action on block: ${block.blockId} \n previousblock: ${block.previousHash}\n is proposal ${block.isProposal} \n isAgreement: ${block.isAgreement} \n is Genesis: ${block.isGenesis}")
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