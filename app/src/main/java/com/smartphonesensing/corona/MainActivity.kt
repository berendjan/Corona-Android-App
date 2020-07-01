package com.smartphonesensing.corona

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smartphonesensing.corona.onboarding.OnboardingActivity
import com.smartphonesensing.corona.storage.SecureStorage
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity
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
         * Incoming message listener
         *
         * Add custom message listeners
         */
        trustChainHelper.addMessageListener(object: TrustChainCommunity.StringMessageListener {
            override fun onStringMessageReceived(message: String, peer: Peer?) {
                Toast.makeText(applicationContext, "Received message '${message}' from peer ${peer?.mid}", Toast.LENGTH_LONG).show()
            }
        })

    }

}


