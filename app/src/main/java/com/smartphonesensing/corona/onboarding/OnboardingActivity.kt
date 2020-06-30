package com.smartphonesensing.corona.onboarding

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.storage.SecureStorage
import org.dpppt.android.sdk.DP3T

class OnboardingActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: FragmentStateAdapter
    private lateinit var secureStorage: SecureStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        viewPager = findViewById(R.id.pager)
        viewPager.isUserInputEnabled = false
        pagerAdapter = OnboardingSlidePageAdapter(this)
        viewPager.adapter = pagerAdapter
        secureStorage = SecureStorage.getInstance(this)!!
    }

    // TODO: Choose country fragment
    // TODO: Activate Trustchain fragment

    fun continueToNextPage() {
        val currentItem: Int = viewPager.currentItem
        if (currentItem < pagerAdapter.itemCount - 1) {
            viewPager.setCurrentItem(currentItem + 1, true)
        } else {
            DP3T.start(this)
            setResult(Activity.RESULT_OK)
            secureStorage.onboardingCompleted = true
            finish()
        }
    }
}