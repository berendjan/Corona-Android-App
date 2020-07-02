package com.smartphonesensing.corona.util

import android.content.Context
import org.dpppt.android.sdk.DP3T
import org.dpppt.android.sdk.backend.ResponseCallback
import org.dpppt.android.sdk.backend.models.ExposeeAuthMethod
import org.dpppt.android.sdk.internal.AppConfigManager
import org.dpppt.android.sdk.internal.crypto.CryptoModule
import org.dpppt.android.sdk.internal.crypto.EphId
import org.dpppt.android.sdk.internal.logger.Logger
import org.dpppt.android.sdk.internal.util.DayDate
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DP3THelper {

    private val BROADCAST_KEY = "broadcast key".toByteArray()
    private const val NUMBER_OF_EPOCHS_PER_DAY = 24 * 4

    private lateinit var DP3T: DP3T
    private lateinit var appConfigManager: AppConfigManager
    private lateinit var cryptoModule: CryptoModule

    fun setDP3T(dp3t: DP3T) {
        DP3T = dp3t
    }

    fun setAppConfigManager(manager: AppConfigManager) {
        appConfigManager = manager
    }

    fun setCryptoModule(module: CryptoModule) {
        cryptoModule = module
    }

    fun createEphIds(SK: ByteArray?, shuffle: Boolean): List<EphId?>? {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(SK, "HmacSHA256"))
            mac.update(BROADCAST_KEY)
            val prf = mac.doFinal()

            //generate EphIDs
            val keySpec =
                SecretKeySpec(prf, "AES")
            val cipher =
                Cipher.getInstance("AES/CTR/NoPadding")
            val counter = ByteArray(16)
            cipher.init(
                Cipher.ENCRYPT_MODE,
                keySpec,
                IvParameterSpec(counter)
            )
            val ephIds = ArrayList<EphId?>()
            val emptyArray = ByteArray(CryptoModule.EPHID_LENGTH)
            for (i in 0 until NUMBER_OF_EPOCHS_PER_DAY) {
                ephIds.add(EphId(cipher.update(emptyArray)))
            }
            if (shuffle) {
                ephIds.shuffle(SecureRandom())
            }
            ephIds
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException("HmacSHA256 and AES algorithms must be present!", e)
        } catch (e: NoSuchPaddingException) {
            throw IllegalStateException("HmacSHA256 and AES algorithms must be present!", e)
        } catch (e: InvalidKeyException) {
            throw IllegalStateException("HmacSHA256 and AES algorithms must be present!", e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw IllegalStateException("HmacSHA256 and AES algorithms must be present!", e)
        }
    }


    fun sendIAmInfected(
        context: Context?,
        onset: Date,
        exposeeAuthMethod: ExposeeAuthMethod?,
        callback: ResponseCallback<Void?>
    ) {
//        DP3T.checkInit()
        val onsetDate = DayDate(onset.time)
        val exposeeRequest = CryptoModule.getInstance(context)
            .getSecretKeyForPublishing(onsetDate, exposeeAuthMethod)
        val appConfigManager = AppConfigManager.getInstance(context)
        try {
            appConfigManager.getBackendReportRepository(context)
                .addExposee(exposeeRequest, exposeeAuthMethod,
                    object : ResponseCallback<Void?> {
                        override fun onSuccess(response: Void?) {
                            appConfigManager.iAmInfected = true
                            CryptoModule.getInstance(context).reset()
//                            DP3T.stop(context)
                            callback.onSuccess(response)
                        }

                        override fun onError(throwable: Throwable) {
                            callback.onError(throwable)
                        }
                    })
        } catch (e: java.lang.IllegalStateException) {
            callback.onError(e)
//            Logger.e(DP3T.TAG, e)
        }
    }
}