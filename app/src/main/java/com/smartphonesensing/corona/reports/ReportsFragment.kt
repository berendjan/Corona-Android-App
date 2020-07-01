package com.smartphonesensing.corona.reports

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.smartphonesensing.corona.databinding.ReportsFragmentBinding

class ReportsFragment : Fragment() {

//    companion object {
//        fun newInstance() = ReportsFragment()
//    }

    private lateinit var binding: ReportsFragmentBinding

    private val viewModel: ReportsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ReportsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

}