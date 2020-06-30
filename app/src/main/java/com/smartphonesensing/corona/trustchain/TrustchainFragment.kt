package com.smartphonesensing.corona.trustchain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.smartphonesensing.corona.databinding.TrustchainFragmentBinding

class TrustchainFragment : Fragment() {

//    companion object {
//        fun newInstance() = TrustchainFragment()
//    }

    private lateinit var binding: TrustchainFragmentBinding

    private lateinit var viewModel: TrustchainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TrustchainFragmentBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(TrustchainViewModel::class.java)

        return binding.root
    }

}