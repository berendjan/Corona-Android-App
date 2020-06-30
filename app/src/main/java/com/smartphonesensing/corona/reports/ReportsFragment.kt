package com.smartphonesensing.corona.reports

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.smartphonesensing.corona.databinding.ReportsFragmentBinding

class ReportsFragment : Fragment() {

//    companion object {
//        fun newInstance() = ReportsFragment()
//    }

    private lateinit var binding: ReportsFragmentBinding

    private lateinit var viewModel: ReportsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ReportsFragmentBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)

        return binding.root
    }

}