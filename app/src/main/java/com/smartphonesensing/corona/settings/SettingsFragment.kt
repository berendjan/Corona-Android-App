package com.smartphonesensing.corona.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SeekBarPreference
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.util.DP3THelper


class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val seekBarPreference = findPreference<SeekBarPreference>("pref_dp3t_att")

        seekBarPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            newValue as Int
            DP3THelper.setAttenuationLevel(newValue.toFloat())
            true
        }

    }
}