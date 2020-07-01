package com.smartphonesensing.corona.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.smartphonesensing.corona.R
import com.smartphonesensing.corona.databinding.HomeFragmentBinding
import kotlin.random.Random


class HomeFragment : Fragment() {

//    companion object {
//        fun newInstance() = HomeFragment()
//    }

    private lateinit var binding: HomeFragmentBinding

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentBinding.inflate(inflater, container, false)

        setHeaderOpacityWithScroll()

        val images = listOf(
            R.drawable.header_basel,
            R.drawable.header_bern,
            R.drawable.header_chur,
            R.drawable.header_geneva,
            R.drawable.header_lausanne,
            R.drawable.header_locarno,
            R.drawable.header_lugano,
            R.drawable.header_luzern,
            R.drawable.header_stgallen,
            R.drawable.header_zurich
        )

        binding.headerImage.setImageResource(images[Random.nextInt(images.size)])

        /** set LifeCycleOwner of binding to this fragment
         * can directly home_fragment.xml to viewModel LiveData object
         */
        binding.lifecycleOwner = this

        return binding.root
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