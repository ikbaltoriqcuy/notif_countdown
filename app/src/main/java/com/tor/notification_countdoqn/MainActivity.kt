package com.tor.notification_countdoqn

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)

        setContent {
            showCountdownNotification(context = this, 60)
        }
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Toast.makeText(context, "diatas O", Toast.LENGTH_SHORT).show()
        val channel = NotificationChannel(
            "countdown_channel",
            "Countdown Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for countdown notifications"
            enableVibration(false) // Disable vibration
            vibrationPattern = longArrayOf(0) // Optional: Set a specific vibration pattern (empty)
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

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

    // Coroutine to update the countdown timer every second
    CoroutineScope(Dispatchers.IO).launch {
        for (second in countdownSeconds downTo 0) {
            // Send broadcast to update the notification
            val intent = Intent(context, CountdownReceiver::class.java).apply {
                putExtra("countdown_seconds", second)
            }
            context.sendBroadcast(intent)
            delay(1000L) // Wait for 1 second
        }

        // Final update when countdown completes
        val intent = Intent(context, CountdownReceiver::class.java).apply {
            putExtra("countdown_seconds", -1) // Indicate countdown completed
        }
        context.sendBroadcast(intent)
    }
}
