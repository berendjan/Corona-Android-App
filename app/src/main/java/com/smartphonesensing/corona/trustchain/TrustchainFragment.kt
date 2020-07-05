package com.smartphonesensing.corona.trustchain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.databinding.TrustchainFragmentBinding
import com.smartphonesensing.corona.trustchain.blocks.BlocksFragment
import com.smartphonesensing.corona.trustchain.peers.PeersFragment
import com.google.android.material.tabs.TabLayoutMediator


class TrustchainFragment : Fragment() {

//    companion object {
//        fun newInstance() = TrustchainFragment()
//    }

    private lateinit var binding: TrustchainFragmentBinding

    private val viewModel: TrustchainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TrustchainFragmentBinding.inflate(inflater, container, false)

        val adapter =  TrustchainPagerAdapter(requireActivity())

        binding.trustchainPager.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabTitles = arrayListOf(
            getString(R.string.title_trustchain_peers),
            getString(R.string.title_trustchain_blocks)
        )
        TabLayoutMediator(binding.trustchainTabLayout, binding.trustchainPager) {
            tab, position -> tab.text = tabTitles[position]
        }.attach()
    }

}

class TrustchainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PeersFragment.newInstance()
            else -> BlocksFragment.newInstance()
        }
    }


}