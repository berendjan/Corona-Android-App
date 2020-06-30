package com.smartphonesensing.corona.onboarding

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.smartphonesensing.corona.R

class OnboardingContentFragment :
    Fragment(R.layout.fragment_onboarding_content) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val args = requireArguments()
        (view.findViewById<View>(R.id.onboarding_title) as TextView).setText(
            args.getInt(
                ARG_RES_TITLE
            )
        )
        val subtitle = view.findViewById<TextView>(R.id.onboarding_subtitle)
        subtitle.setText(args.getInt(ARG_RES_SUBTITLE))
        if (args.getBoolean(ARG_STYLE_GREEN)) subtitle.setTextColor(
            resources.getColor(R.color.green_main, null)
        )
        (view.findViewById<View>(R.id.onboarding_illustration) as ImageView).setImageResource(
            args.getInt(ARG_RES_ILLUSTRATION)
        )
        val icon1 =
            view.findViewById<ImageView>(R.id.onboarding_description_1_icon)
        icon1.setImageResource(args.getInt(ARG_RES_DESCR_ICON_1))
        if (args.getBoolean(ARG_STYLE_GREEN)) icon1.imageTintList =
            ColorStateList.valueOf(resources.getColor(R.color.green_main, null))
        (view.findViewById<View>(R.id.onboarding_description_1) as TextView).setText(
            args.getInt(ARG_RES_DESCRIPTION_1)
        )
        val icon2 =
            view.findViewById<ImageView>(R.id.onboarding_description_2_icon)
        icon2.setImageResource(args.getInt(ARG_RES_DESCR_ICON_2))
        if (args.getBoolean(ARG_STYLE_GREEN)) icon2.imageTintList =
            ColorStateList.valueOf(resources.getColor(R.color.green_main, null))
        (view.findViewById<View>(R.id.onboarding_description_2) as TextView).setText(
            args.getInt(ARG_RES_DESCRIPTION_2)
        )
        val continueButton =
            view.findViewById<Button>(R.id.onboarding_continue_button)
        continueButton.setOnClickListener { (activity as OnboardingActivity?)!!.continueToNextPage() }
    }

    companion object {
        private const val ARG_RES_TITLE = "RES_TITLE"
        private const val ARG_RES_SUBTITLE = "RES_SUBTITLE"
        private const val ARG_RES_DESCRIPTION_1 = "RES_DESCRIPTION_1"
        private const val ARG_RES_DESCRIPTION_2 = "RES_DESCRIPTION_2"
        private const val ARG_RES_DESCR_ICON_1 = "ARG_RES_DESCR_ICON_1"
        private const val ARG_RES_DESCR_ICON_2 = "ARG_RES_DESCR_ICON_2"
        private const val ARG_RES_ILLUSTRATION = "RES_ILLUSTRATION"
        private const val ARG_STYLE_GREEN = "ARG_STYLE_GREEN"
        @JvmStatic
        fun newInstance(
            @StringRes title: Int,
            @StringRes subtitle: Int,
            @DrawableRes illustration: Int,
            @StringRes description1: Int,
            @DrawableRes iconDescription1: Int,
            @StringRes description2: Int,
            @DrawableRes iconDescription2: Int,
            greenStyle: Boolean
        ): OnboardingContentFragment {
            val args = Bundle()
            args.putInt(ARG_RES_TITLE, title)
            args.putInt(ARG_RES_SUBTITLE, subtitle)
            args.putInt(ARG_RES_ILLUSTRATION, illustration)
            args.putInt(ARG_RES_DESCR_ICON_1, iconDescription1)
            args.putInt(ARG_RES_DESCRIPTION_1, description1)
            args.putInt(ARG_RES_DESCR_ICON_2, iconDescription2)
            args.putInt(ARG_RES_DESCRIPTION_2, description2)
            args.putBoolean(ARG_STYLE_GREEN, greenStyle)
            val fragment = OnboardingContentFragment()
            fragment.arguments = args
            return fragment
        }
    }
}