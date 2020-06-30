package com.smartphonesensing.corona.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.onboarding.util.PermissionButtonUtil
import com.smartphonesensing.corona.util.DeviceFeatureHelper

class OnboardingBatteryPermissionFragment :
    Fragment(R.layout.fragment_onboarding_permission_battery) {
    private lateinit var batteryButton: Button
    private lateinit var continueButton: Button
    private var wasUserActive = false
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        batteryButton =
            view.findViewById(R.id.onboarding_battery_permission_button)
        batteryButton.setOnClickListener {
            startActivity(
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:" + requireContext().packageName)
                )
            )
            wasUserActive = true
        }
        continueButton =
            view.findViewById(R.id.onboarding_battery_permission_continue_button)
        continueButton.setOnClickListener { (activity as OnboardingActivity?)!!.continueToNextPage() }
    }

    override fun onResume() {
        super.onResume()
        updateFragmentState()
    }

    private fun updateFragmentState() {
        val batteryOptDeactivated: Boolean =
            DeviceFeatureHelper.isBatteryOptimizationDeactivated(requireContext())
        if (batteryOptDeactivated) {
            PermissionButtonUtil.setButtonOk(
                batteryButton,
                R.string.android_onboarding_battery_permission_button_deactivated
            )
        } else {
            PermissionButtonUtil.setButtonDefault(
                batteryButton,
                R.string.android_onboarding_battery_permission_button
            )
        }
        continueButton.visibility =
            if (batteryOptDeactivated || wasUserActive) View.VISIBLE else View.GONE
        if (batteryOptDeactivated && wasUserActive) {
            (activity as OnboardingActivity?)!!.continueToNextPage()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): OnboardingBatteryPermissionFragment {
            return OnboardingBatteryPermissionFragment()
        }
    }
}