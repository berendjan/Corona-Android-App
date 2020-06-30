package com.smartphonesensing.corona.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.smartphonesensing.corona.R

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}