package com.smartphonesensing.corona.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.onboarding.OnboardingContentFragment.Companion.newInstance

class OnboardingSlidePageAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return newInstance(
                R.string.onboarding_prinzip_title,
                R.string.onboarding_prinzip_heading,
                R.drawable.ill_prinzip,
                R.string.onboarding_prinzip_text1,
                R.drawable.ic_begegnungen,
                R.string.onboarding_prinzip_text2,
                R.drawable.ic_message_alert,
                false
            )
            1 -> return newInstance(
                R.string.onboarding_privacy_title,
                R.string.onboarding_privacy_heading,
                R.drawable.ill_privacy,
                R.string.onboarding_privacy_text1,
                R.drawable.ic_key,
                R.string.onboarding_privacy_text2,
                R.drawable.ic_lock,
                true
            )
            2 -> return newInstance(
                R.string.onboarding_begegnungen_title,
                R.string.onboarding_begegnungen_heading,
                R.drawable.ill_bluetooth,
                R.string.onboarding_begegnungen_text1,
                R.drawable.ic_begegnungen,
                R.string.onboarding_begegnungen_text2,
                R.drawable.ic_bluetooth,
                false
            )
            3 -> return OnboardingLocationPermissionFragment.newInstance()
            4 -> return OnboardingBatteryPermissionFragment.newInstance()
            5 -> return newInstance(
                R.string.onboarding_meldung_title,
                R.string.onboarding_meldung_heading,
                R.drawable.ill_meldung,
                R.string.onboarding_meldung_text1,
                R.drawable.ic_message_alert,
                R.string.onboarding_meldung_text2,
                R.drawable.ic_home,
                false
            )
            6 -> return OnboardingFinishedFragment.newInstance()
        }
        throw IllegalArgumentException("There is no fragment for view pager position $position")
    }
}