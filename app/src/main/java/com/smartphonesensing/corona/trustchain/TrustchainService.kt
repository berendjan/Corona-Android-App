package com.smartphonesensing.corona.trustchain

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.smartphonesensing.corona.MainActivity
import com.smartphonesensing.corona.R
import nl.tudelft.ipv8.android.service.IPv8Service

class TrustChainService : IPv8Service() {

    override fun createNotification(): NotificationCompat.Builder {

        val trustChainExplorerIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(trustChainExplorerIntent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_CONNECTION)
            .setContentTitle("IPv8 Demo")
            .setContentText("Running")
            .setSmallIcon(R.drawable.ic_trustchain)
            .setContentIntent(pendingIntent)
    }
}
