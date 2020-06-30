package com.smartphonesensing.corona

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smartphonesensing.corona.onboarding.OnboardingActivity
import com.smartphonesensing.corona.storage.SecureStorage

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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.navigation_home,
//            R.id.navigation_encounters,
//            R.id.navigation_trustchain,
//            R.id.navigation_settings,
//            R.id.navigation_reports))
        navView.setupWithNavController(navController)


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

    }

}

