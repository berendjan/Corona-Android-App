package com.smartphonesensing.corona.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.util.DP3THelper
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.util.hexToBytes
import nl.tudelft.ipv8.util.toHex
import nl.tudelft.trustchain.common.util.TrustChainHelper


class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val seekBarPreference = findPreference<SeekBarPreference>("pref_dp3t_att")

        seekBarPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            newValue as Int
            DP3THelper.setAttenuationLevel(newValue.toFloat())
            true
        }

        val listPreference = findPreference<ListPreference>("pref_ipv8_health_official")
        val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)

        val peers = trustChainHelper.getPeers()

        val temp: MutableList<String> = ArrayList()
        val tempVals: MutableList<String> = ArrayList()

        for (peer in peers) {
            temp.add(peer.mid)
            tempVals.add(peer.publicKey.keyToBin().toHex())
        }
        temp.add("self")
        tempVals.add(trustChainHelper.getMyPublicKey().toHex())

        val entries : Array<CharSequence> = temp.toTypedArray()
        val entryValues = tempVals.toTypedArray()
        listPreference?.entries = entries
        listPreference?.entryValues = entryValues

        listPreference?.isPersistent = true

    }
}