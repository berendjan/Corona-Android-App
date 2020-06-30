package com.smartphonesensing.corona.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.smartphonesensing.corona.R

class OnboardingFinishedFragment :
    Fragment(R.layout.fragment_onboarding_finished) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        view.findViewById<View>(R.id.onboarding_continue_button)
            .setOnClickListener { (activity as OnboardingActivity?)!!.continueToNextPage() }
    }

    companion object {
        @JvmStatic
		fun newInstance(): OnboardingFinishedFragment {
            return OnboardingFinishedFragment()
        }
    }
}