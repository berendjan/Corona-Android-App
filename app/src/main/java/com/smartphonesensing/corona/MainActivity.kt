package com.smartphonesensing.corona

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smartphonesensing.corona.onboarding.OnboardingActivity
import com.smartphonesensing.corona.storage.SecureStorage
import com.smartphonesensing.corona.trustchain.CoronaPayload
import com.smartphonesensing.corona.trustchain.MyMessage
import com.smartphonesensing.corona.util.DP3THelper
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity
import nl.tudelft.ipv8.util.toHex
import nl.tudelft.trustchain.common.util.TrustChainHelper

class MainActivity : AppCompatActivity() {

    private val REQ_ONBOARDING = 123
    private val STATE_CONSUMED_EXPOSED_INTENT = "STATE_CONSUMED_EXPOSED_INTENT"

    private lateinit var secureStorage: SecureStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.main_navigation_bar)

        secureStorage = SecureStorage.getInstance(this)!!

        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

        val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)


        if (savedInstanceState == null) {
            if (secureStorage.onboardingCompleted) {
//                supportFragmentManager
//                    .beginTransaction()
//                    .add(R.id.nav_host_fragment, HomeFragment.newInstance())
//                    .commit()
            } else {
                startActivityForResult(
                    Intent(this, OnboardingActivity::class.java),
                    REQ_ONBOARDING
                )
            }
        } else {
            val consumedExposedIntent =
                savedInstanceState.getBoolean(STATE_CONSUMED_EXPOSED_INTENT)
        }


        /**
         * Add custom listener for message
         */
        trustChainHelper.addMessageListener(object : TrustChainCommunity.GeneralPacketListener(
            MyMessage.Deserializer) {
            override fun onGeneralPacketReceived(contents: Any?, peer: Peer) {
                contents as MyMessage
                Toast.makeText(applicationContext, "Received message '${contents.message}' from peer ${peer.mid}", Toast.LENGTH_LONG).show()
            }
        })


        /**
         * Add listener for SKList packet denoted in TrustChainPayload
         */
        trustChainHelper.addSKListPacketListener(object : TrustChainCommunity.GeneralPacketListener(
            CoronaPayload.Deserializer) {
            override fun onGeneralPacketReceived(contents: Any?, peer: Peer) {
                contents as CoronaPayload
                for (pair in contents.SKList) {
                    Log.i("SKLIST", "Pair date " + pair.first.formatAsString() + " Key " + pair.second.toHex())
                }
            }
        })

    }

}


