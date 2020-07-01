package com.smartphonesensing.corona.trustchain.blocks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.databinding.EncountersFragmentBinding
import com.smartphonesensing.corona.databinding.TrustchainBlocksFragmentBinding
import com.smartphonesensing.corona.encounters.EncountersViewModel

class BlocksFragment : Fragment() {

    companion object {
        fun newInstance() = BlocksFragment()
    }

    private lateinit var binding: TrustchainBlocksFragmentBinding

    private val viewModel: BlocksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TrustchainBlocksFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

}