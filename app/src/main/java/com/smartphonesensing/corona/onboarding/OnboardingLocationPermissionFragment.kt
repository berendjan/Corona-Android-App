package com.smartphonesensing.corona.onboarding

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.onboarding.util.PermissionButtonUtil
import com.smartphonesensing.corona.util.DeviceFeatureHelper

class OnboardingLocationPermissionFragment :
    Fragment(R.layout.fragment_onboarding_permission_location) {
    private lateinit var locationButton: Button
    private lateinit var continueButton: Button

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        locationButton =
            view.findViewById(R.id.onboarding_location_permission_button)
        locationButton.setOnClickListener {
            val permissions =
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(
                permissions,
                REQUEST_CODE_ASK_PERMISSION_FINE_LOCATION
            )
        }
        continueButton =
            view.findViewById(R.id.onboarding_location_permission_continue_button)
        continueButton.setOnClickListener { (activity as OnboardingActivity?)!!.continueToNextPage() }
    }

    override fun onResume() {
        super.onResume()
        updateFragmentState()
    }

    private fun updateFragmentState() {
        val locationPermissionGranted: Boolean =
            DeviceFeatureHelper.isLocationPermissionGranted(requireContext())
        if (locationPermissionGranted) {
            PermissionButtonUtil.setButtonOk(
                locationButton,
                R.string.android_onboarding_bt_permission_button_allowed
            )
        } else {
            PermissionButtonUtil.setButtonDefault(
                locationButton,
                R.string.android_onboarding_bt_permission_button
            )
        }
        continueButton.visibility = if (locationPermissionGranted) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSION_FINE_LOCATION) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat
                        .shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                ) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.android_button_permission_location)
                        .setMessage(R.string.android_foreground_service_notification_error_location_permission)
                        .setPositiveButton(getString(R.string.android_button_ok)
                        ) { dialogInterface: DialogInterface, _: Int ->
                            DeviceFeatureHelper.openApplicationSettings(requireActivity())
                            dialogInterface.dismiss()
                        }
                        .create()
                        .show()
                }
            }
            (activity as OnboardingActivity?)!!.continueToNextPage()
        }
    }

    companion object {
        const val REQUEST_CODE_ASK_PERMISSION_FINE_LOCATION = 123
        @JvmStatic
        fun newInstance(): OnboardingLocationPermissionFragment {
            return OnboardingLocationPermissionFragment()
        }
    }
}