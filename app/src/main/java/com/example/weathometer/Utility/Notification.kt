package com.example.weathometer.Utility

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.weathometer.R

const val notificationId=1
const val channelId="dailynotification"
const val titleExtra ="titleExtra"
const val messageExtra ="messageExtra"

class Notification : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val notification = NotificationCompat.Builder(p0!!, channelId)
            .setSmallIcon(R.drawable.ic_cloud)
            .setContentTitle(p1?.getStringExtra(titleExtra))
            .setContentText(p1?.getStringExtra(messageExtra))
            .build()

        val manager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)

    }
}