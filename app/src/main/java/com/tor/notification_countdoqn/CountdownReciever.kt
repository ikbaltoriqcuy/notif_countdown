package com.tor.notification_countdoqn

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class CountdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val countdownSeconds = intent.getIntExtra("countdown_seconds", 0)
        updateNotification(context, countdownSeconds)
    }

    private fun updateNotification(context: Context, seconds: Int) {
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_countdown)
        notificationLayout.setTextViewText(R.id.notification_countdown, "Starts in $seconds seconds")

        val builder = NotificationCompat.Builder(context, "countdown_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        notificationManager.notify(1001, builder.build())
    }
}
