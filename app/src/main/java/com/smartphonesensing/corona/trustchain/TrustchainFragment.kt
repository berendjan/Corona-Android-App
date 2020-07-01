package com.smartphonesensing.corona.trustchain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TabHost.OnTabChangeListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.databinding.TrustchainFragmentBinding
import com.smartphonesensing.corona.trustchain.blocks.BlocksFragment
import com.smartphonesensing.corona.trustchain.peers.PeersFragment


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

        binding.trustchainPager.adapter = TrustchainPagerAdapter(requireActivity())

        setupTabBar()

        return binding.root
    }

    private fun setupTabBar() {
        val tabLayout = binding.trustchainTabLayout
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_trustchain_peers))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_trustchain_blocks))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    binding.trustchainPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) { }
            override fun onTabReselected(tab: TabLayout.Tab) { }
        })
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