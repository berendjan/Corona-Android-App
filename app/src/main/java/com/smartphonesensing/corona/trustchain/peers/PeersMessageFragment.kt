package com.smartphonesensing.corona.trustchain.peers

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.smartphonesensing.corona.databinding.TrustchainPeersMessageFragmentBinding
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity
import nl.tudelft.ipv8.util.hexToBytes
import nl.tudelft.ipv8.util.toHex
import nl.tudelft.trustchain.common.util.TrustChainHelper


class PeersMessageFragment : Fragment() {

    private lateinit var binding: TrustchainPeersMessageFragmentBinding

    private val viewModel: PeersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TrustchainPeersMessageFragmentBinding.inflate(inflater, container, false)

        val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
            val inputManager = requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireView().windowToken, 0)
        }

//        binding.messageEditText.setOnEditorActionListener(OnEditorActionListener { view, actionId, event ->
//            if (event.action == KeyEvent.ACTION_DOWN && actionId == KeyEvent.KEYCODE_ENTER) {
//                val inputManager = view.context
//                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
//                return@OnEditorActionListener true
//            }
//            return@OnEditorActionListener false
//        })

        binding.messageSendButton.setOnClickListener { _ ->
            if (viewModel.getMessageString().value?.length ?: 0 > 0) {
                trustChainHelper.sendMessageToPeer(
                    viewModel.selectedPeer.value!!.peer,
                    viewModel.getMessageString().value!!)
            } else {
                Toast.makeText(context, "You cannot send an empty message", Toast.LENGTH_SHORT).show()
            }


        }

        binding.peersViewModel = viewModel

        return binding.root
    }

}
