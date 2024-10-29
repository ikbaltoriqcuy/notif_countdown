package com.tor.notification_countdoqn

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        setContent {
           showCountdownNotification(context = this, 60)
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel",
                "Download Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java) as NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }


    @SuppressLint("RestrictedApi")
    fun showCountdownNotification(context: Context, countdownSeconds: Int) {
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_countdown)
        notificationLayout.setTextViewText(R.id.notification_title, "Spike Event Countdown")
        notificationLayout.setTextViewText(R.id.notification_description, "Don't miss out!")
        notificationLayout.setImageViewResource(R.id.notification_image, R.mipmap.ic_launcher)

        // Create the notification builder
        val builder = NotificationCompat.Builder(context, "countdown_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setContentTitle("Download in Progress")
            .setContentText("Downloading file...")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, builder.build())

        builder.setChannelId("download_channel")
        CoroutineScope(Dispatchers.IO).launch {
            for (second in countdownSeconds downTo 0) {
                    builder.contentView.setTextViewText(R.id.notification_countdown, "Starts in $second seconds")
                    notificationManager.notify(1, builder.build())
                    delay(1000L) // Wait for 1 second
            }

        }
    }

}
