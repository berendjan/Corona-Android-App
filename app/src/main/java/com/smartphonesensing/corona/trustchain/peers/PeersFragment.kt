package com.smartphonesensing.corona.trustchain.peers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.smartphonesensing.corona.databinding.TrustchainPeersFragmentBinding
import com.smartphonesensing.corona.trustchain.CoronaCommunity
import com.smartphonesensing.corona.trustchain.TrustchainFragmentDirections
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity

class PeersFragment : Fragment() {

    companion object {
        fun newInstance() = PeersFragment()
    }

    private lateinit var binding: TrustchainPeersFragmentBinding

    private val viewModel: PeersViewModel by activityViewModels()

    private lateinit var trustChain : TrustChainCommunity

    private lateinit var coronaCommunity : CoronaCommunity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TrustchainPeersFragmentBinding.inflate(inflater, container, false)

        val adapter = PeersAdapter(PeerListener { peer ->
            viewModel.onPeerClicked(peer)
            findNavController().navigate(TrustchainFragmentDirections
                .actionNavigationTrustchainToPeersMessageFragment())
        })

        binding.trustchainPeersRecycler.adapter = adapter

        viewModel.peerList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })


        trustChain = IPv8Android.getInstance().getOverlay()!!

        coronaCommunity = IPv8Android.getInstance().getOverlay()!!

        loadPeersInfo()

        return binding.root
    }

    private fun loadPeersInfo() {
        lifecycleScope.launchWhenStarted {
            while (isActive) {
                val coronaCommunity = IPv8Android.getInstance().getOverlay<CoronaCommunity>()!!

                val peers = coronaCommunity.getPeers()
                val discoveredAddresses = coronaCommunity.network
                    .getWalkableAddresses(coronaCommunity.serviceId)
                val discoveredBluetoothAddresses = coronaCommunity.network
                    .getNewBluetoothPeerCandidates()
                    .map { it.address }

//                val peerItems = peers.map {
//                    PeerItem(
//                        it
//                    )
//                }
//
//                val addressItems = discoveredAddresses.map { address ->
//                    AddressItem(
//                        address,
//                        null,
//                        null
//                    )
//                }
//
//                val bluetoothAddressItems = discoveredBluetoothAddresses.map { address ->
//                    AddressItem(
//                        address,
//                        null,
//                        null
//                    )
//                }
//
//                val items =

                viewModel.updatePeerList(peers)
                delay(3000)
            }
        }
    }

}