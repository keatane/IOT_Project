package com.island.iot

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class NotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FIREBASE", "Message received")
        Log.d("FIREBASE", message.toString())
        Log.d("FIREBASE", message.data.toString())
        sendNotification(message.data["jugId"]!!.toInt())
    }

    private fun sendNotification(jugId: Int) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }.setAction(jugId.toString())


        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, FLAG_IMMUTABLE
        )

        val channelId = FILTER_ALERT_CHANNEL

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.filter_alert))
            .setContentText(getString(R.string.your_filter_is_about_to_expire))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notificationBuilder.build())
    }
}