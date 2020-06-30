
package com.smartphonesensing.corona.onboarding.util

import android.graphics.Color
import android.widget.Button
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.smartphonesensing.corona.R

object PermissionButtonUtil {
    fun setButtonDefault(button: Button, @StringRes buttonLabel: Int) {
        val context = button.context
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        button.setTextColor(Color.WHITE)
        button.setText(buttonLabel)
        button.isClickable = true
        button.elevation =
            context.resources.getDimensionPixelSize(R.dimen.button_elevation).toFloat()
        button.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue_main)
    }

    fun setButtonOk(button: Button, @StringRes grantedLabel: Int) {
        val context = button.context
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0)
        button.setCompoundDrawableTintList(
            ContextCompat.getColorStateList(
                context,
                R.color.blue_main
            )
        )
        button.setTextColor(ContextCompat.getColor(context, R.color.blue_main))
        button.setText(grantedLabel)
        button.isClickable = false
        button.elevation = 0f
        button.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
    }
}