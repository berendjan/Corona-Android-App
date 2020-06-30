package com.smartphonesensing.corona.encounters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smartphonesensing.corona.databinding.EncountersFragmentBinding
import org.dpppt.android.sdk.internal.AppConfigManager
import org.dpppt.android.sdk.internal.database.Database
import org.dpppt.android.sdk.internal.database.models.Handshake

class EncountersFragment : Fragment() {

//    companion object {
//        fun newInstance() = EncountersFragment()
//    }

    private lateinit var binding: EncountersFragmentBinding

    private lateinit var viewModel: EncountersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = EncountersFragmentBinding.inflate(inflater, container, false)

        val adapter = EncountersAdapter(EncounterListener { encounter ->
            viewModel.onEncounterClicked(encounter)
            findNavController().navigate(EncountersFragmentDirections
                .actionNavigationEncountersToEncounterDetailsFragment(encounter))
        })
        binding.encountersRecycler.adapter = adapter

        viewModel = ViewModelProvider(this).get(EncountersViewModel::class.java)

        loadHandshakes()

        viewModel.encountersList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        return binding.root
    }

    private fun loadHandshakes() {

        viewModel.scanInterval = AppConfigManager.getInstance(context).scanInterval

        viewModel.scanDuration = AppConfigManager.getInstance(context).scanDuration

        Database((context)!!).getHandshakes { response: List<Handshake> ->
            viewModel.updateEncountersList(response)
        }
    }



}