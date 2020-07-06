package com.smartphonesensing.corona.trustchain.blocks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.smartphonesensing.corona.databinding.TrustchainBlocksFragmentBinding
import kotlinx.coroutines.isActive
import nl.tudelft.ipv8.android.IPv8Android
import kotlinx.coroutines.delay
import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.trustchain.common.util.TrustChainHelper

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

//        val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)

        binding = TrustchainBlocksFragmentBinding.inflate(inflater, container, false)

        val adapter = BlocksAdapter(BlockListener ({clickedBlock ->
            viewModel.updateSelectedBlock(clickedBlock)
        }, {signBlock ->
            viewModel.signBlock(signBlock)
//            trustChainHelper.updateBlocks()
        } ))
        binding.trustchainBlocksRecycler.adapter = adapter


//        trustChainHelper.updateBlocks.observe(viewLifecycleOwner, Observer {
//            if (it) {
//                viewModel.updateBlocks(trustChainHelper.getMyChain())
//            }
//        })
//
//        trustChainHelper.updateBlocks()

        viewModel.blocks.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
//            trustChainHelper.doneUpdatingBlocks()
        })
        getBlocks()

        return binding.root
    }

    fun getBlocks() {
        lifecycleScope.launchWhenStarted {
            val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)
            while (isActive) {

                viewModel.updateBlocks(trustChainHelper.getMyChain())

                delay(1000)

            }
        }
    }

}