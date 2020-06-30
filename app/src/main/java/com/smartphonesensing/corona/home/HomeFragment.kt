package com.smartphonesensing.corona.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smartphonesensing.corona.databinding.HomeFragmentBinding


class HomeFragment : Fragment() {

//    companion object {
//        fun newInstance() = HomeFragment()
//    }

    private lateinit var binding: HomeFragmentBinding

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentBinding.inflate(inflater, container, false)

        setHeaderOpacityWithScroll()

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

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