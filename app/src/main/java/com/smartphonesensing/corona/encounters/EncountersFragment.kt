package com.smartphonesensing.corona.encounters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.smartphonesensing.corona.util.DP3THelper
import kotlinx.coroutines.delay
import com.smartphonesensing.corona.databinding.EncountersFragmentBinding
import kotlinx.coroutines.isActive
import org.dpppt.android.sdk.internal.database.models.Handshake

class EncountersFragment : Fragment() {

//    companion object {
//        fun newInstance() = EncountersFragment()
//    }

    private lateinit var binding: EncountersFragmentBinding

    private val viewModel: EncountersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = EncountersFragmentBinding.inflate(inflater, container, false)

        val adapter = EncountersAdapter(EncounterListener { encounter ->
            viewModel.onEncounterClicked(encounter)
            findNavController().navigate(EncountersFragmentDirections
                .actionNavigationEncountersToEncounterDetailsFragment())
        })
        binding.encountersRecycler.adapter = adapter

        DP3THelper.updateContactsFromDatabase()

        loadHandshakes()

        viewModel.encountersList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        return binding.root
    }

    private fun loadHandshakes() {
        lifecycleScope.launchWhenStarted {
            while (isActive) {

                viewModel.scanInterval = DP3THelper.appConfigManager.scanInterval

                viewModel.scanDuration = DP3THelper.appConfigManager.scanDuration

                DP3THelper.database.getHandshakes { response: List<Handshake> ->
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val riskyHandshakesOnly = sharedPreferences.getBoolean("pref_dp3t_risk", false)
                    viewModel.updateEncountersList(response, riskyHandshakesOnly)
                }

                delay(5000)
            }
        }
    }

    private fun loadContacts() {
        lifecycleScope.launchWhenStarted {
            while (isActive) {

                viewModel.scanInterval = DP3THelper.appConfigManager.scanInterval

                viewModel.scanDuration = DP3THelper.appConfigManager.scanInterval

                viewModel.updateContactsList(DP3THelper.database.contacts)

                delay(5000)

            }
        }
    }



}