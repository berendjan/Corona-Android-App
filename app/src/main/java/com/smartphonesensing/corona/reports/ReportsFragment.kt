package com.smartphonesensing.corona.reports

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.smartphonesensing.corona.databinding.ReportsFragmentBinding
import com.smartphonesensing.corona.util.DP3THelper

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

        val adapter = ReportsAdapter(ReportsListener {

        })

        binding.reportsRecycler.adapter = adapter

        DP3THelper.diagnosedContacts.observe(viewLifecycleOwner, Observer {
            viewModel.updateReports(it)
        })
        viewModel.reportItem.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        return binding.root
    }

}