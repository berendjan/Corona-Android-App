package com.smartphonesensing.corona.trustchain.peers

import android.content.Context
import android.os.Bundle
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
import com.smartphonesensing.corona.trustchain.CoronaCommunity
import nl.tudelft.ipv8.android.IPv8Android


class PeersMessageFragment : Fragment() {

    private lateinit var binding: TrustchainPeersMessageFragmentBinding

    private val viewModel: PeersViewModel by activityViewModels()

    private lateinit var coronaCommunity: CoronaCommunity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TrustchainPeersMessageFragmentBinding.inflate(inflater, container, false)

        coronaCommunity = IPv8Android.getInstance().getOverlay<CoronaCommunity>()!!

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
            val inputManager = requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireView().windowToken, 0)
        }

//        binding.messageEditText.setOnEditorActionListener(OnEditorActionListener { view, actionId, event ->
//            if (event.action == KeyEvent.ACTION_UP && actionId == KeyEvent.KEYCODE_ENTER) {
//                val inputManager = view.context
//                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
//                return@OnEditorActionListener true
//            }
//            return@OnEditorActionListener false
//        })
//
//        binding.messageEditText.setOnFocusChangeListener { view, hasFocus ->
//            if (!hasFocus) {
//                val inputManager = view.context
//                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
//            }
//        }

        binding.messageSendButton.setOnClickListener { _ ->
            if (viewModel.getMessageString().value?.length ?: 0 > 0) {
                coronaCommunity.broadcastHelloMessage(
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
