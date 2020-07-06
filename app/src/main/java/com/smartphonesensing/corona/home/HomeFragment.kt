package com.smartphonesensing.corona.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.databinding.HomeFragmentBinding
import com.smartphonesensing.corona.util.DP3THelper
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.trustchain.TrustChainCommunity
import nl.tudelft.ipv8.util.hexToBytes
import nl.tudelft.ipv8.util.toHex
import nl.tudelft.trustchain.common.util.TrustChainHelper
import org.dpppt.android.sdk.internal.database.models.Handshake


class HomeFragment : Fragment() {

//    companion object {
//        fun newInstance() = HomeFragment()
//    }

    private lateinit var binding: HomeFragmentBinding

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentBinding.inflate(inflater, container, false)

        setHeaderOpacityWithScroll()

        val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)

        /** set LifeCycleOwner of binding to this fragment
         * can directly home_fragment.xml to viewModel LiveData object
         */
        binding.lifecycleOwner = this

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        binding.diagnosedRequest.setOnClickListener {

            val healthOfficial = sharedPreferences.getString("pref_ipv8_health_official", null)

            when (healthOfficial) {
                null -> Toast.makeText(context, "Please provide the health official validation peer in settings!", Toast.LENGTH_LONG).show()
                trustChainHelper.getMyPublicKey().toHex() -> run {
                    Toast.makeText(context, "You are the health official and can only validate others", Toast.LENGTH_LONG).show()
                }
                else -> run {
                    val secretKeyList = DP3THelper.getSKList()
                    trustChainHelper.createCoronaProposalBlock(secretKeyList,
                        healthOfficial.hexToBytes())
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupStatusIcons()
        DP3THelper.updateContactsFromDatabase()
    }

    private fun setupStatusIcons() {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val dp3t = sharedPreferences.getBoolean("pref_dp3t_tracing", true)
        if (dp3t) {
            view?.findViewById<ImageView>(R.id.encounters_card_status_icon)?.setImageResource(R.drawable.ic_check)
            DP3THelper.database.getHandshakes { response: List<Handshake> ->
                view?.findViewById<TextView>(R.id.encounters_card_status_text)?.text =
                    getString(R.string.home_encounters_subtext_check, response.size , DP3THelper.contacts.value?.size ?: 0)
            }
        } else {
            view?.findViewById<ImageView>(R.id.encounters_card_status_icon)?.setImageResource(R.drawable.ic_warning)
            view?.findViewById<TextView>(R.id.encounters_card_status_text)?.text =
                getString(R.string.home_encounters_subtext_warning)
        }

        val ipv8 = sharedPreferences.getBoolean("pref_ipv8_trustchain", true)
        if (ipv8) {
            val trustChainHelper = TrustChainHelper(IPv8Android.getInstance().getOverlay()!!)
            view?.findViewById<ImageView>(R.id.trustchain_card_status_icon)?.setImageResource(R.drawable.ic_check)
            view?.findViewById<TextView>(R.id.trustchain_card_status_text)?.text =
                getString(R.string.home_trustchain_subtext_check,
                    trustChainHelper.getPeers().size,
                    trustChainHelper.getChainByUser(trustChainHelper.getMyPublicKey()).size)
        } else {
            view?.findViewById<ImageView>(R.id.trustchain_card_status_icon)?.setImageResource(R.drawable.ic_warning)
            view?.findViewById<TextView>(R.id.trustchain_card_status_text)?.text =
                getString(R.string.home_trustchain_subtext_warning)
        }

        val reports = DP3THelper.diagnosedContacts.value
        if (reports?.isNotEmpty() == true) {
            view?.findViewById<ImageView>(R.id.reports_card_status_icon)?.setImageResource(R.drawable.ic_warning)
            view?.findViewById<TextView>(R.id.reports_card_status_text)?.text =
                getString(R.string.home_reports_subtext_warning, reports.size, DP3THelper.getNumberOfDaysExposed())
        } else {
            view?.findViewById<ImageView>(R.id.reports_card_status_icon)?.setImageResource(R.drawable.ic_check)
            view?.findViewById<TextView>(R.id.reports_card_status_text)?.text =
                getString(R.string.home_reports_subtext_check)
        }

    }

    private fun setHeaderOpacityWithScroll() {
        val headerImage = binding.headerImage
        val scrollView = binding.homeScrollView
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY < 500) {
                headerImage.alpha = (0.5 - (scrollY.toFloat() + 1) / 1000).toFloat()
            } else {
                headerImage.alpha = 0.0F
            }
        }
    }

}