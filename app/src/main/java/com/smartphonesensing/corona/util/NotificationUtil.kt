package com.smartphonesensing.corona.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.smartphonesensing.corona.R

object NotificationUtil {
    const val NOTIFICATION_CHANNEL_ID = "contact-channel"
    const val NOTIFICATION_ID_CONTACT = 42
    const val NOTIFICATION_ID_UPDATE = 43

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelName = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
    }
}