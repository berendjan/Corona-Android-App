package com.smartphonesensing.corona.trustchain.blocks

//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import com.smartphonesensing.corona.databinding.EncounterDetailsFragmentBinding
//import com.smartphonesensing.corona.encounters.EncountersViewModel
//
//class BlockDetailsFragment : Fragment() {
//
//    private lateinit var binding: EncounterDetailsFragmentBinding
//
//    private val viewModel: EncountersViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        binding = EncounterDetailsFragmentBinding.inflate(inflater, container, false)
//
//        binding.backButton.setOnClickListener {
//            findNavController().navigateUp()
//        }
//
////        val encounter = EncounterDetailsFragmentArgs.fromBundle(requireArguments()).encounter
//
//        binding.encounter = viewModel.encounterSelected.value
////
////        Log.i("Handshakes", encounter.identifier)
////        Log.i("Handshakes", encounter.dateTimeStart)
////        Log.i("Handshakes", encounter.dateTimeEnd)
////        Log.i("Handshakes", encounter.count.toString())
////        Log.i("Handshakes", encounter.expectedCount.toString())
////        Log.i("Handshakes", encounter.endtime.toString())
////        Log.i("Handshakes", encounter.starttime.toString())
////        Log.i("Handshakes", encounter.maxRSSI.toString())
////        Log.i("Handshakes", encounter.maxTxPowerLevel.toString())
////        Log.i("Handshakes", encounter.handshakes.toString())
//
//        return binding.root
//    }
//}
