package com.smartphonesensing.corona.util

import org.dpppt.android.sdk.DP3T
import org.dpppt.android.sdk.internal.AppConfigManager
import org.dpppt.android.sdk.internal.crypto.CryptoModule

object DP3THelper {
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

    fun getEphIds() {

    }


}