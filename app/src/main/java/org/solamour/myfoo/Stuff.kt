package org.solamour.myfoo

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
const val NOTIFICATION_ID = 1
const val REQUEST_CODE = 1
val TIMER_EXPIRATION = TimeUnit.SECONDS.toMillis(45)

//--------------------------------------------------------------------------------------------------
fun createNotificationChannel(context: Context) {
    val notificationChannel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        enableLights(true)
        lightColor = Color.RED
        enableVibration(true)
        description = "channel_description"
    }
    val notificationManager = ContextCompat.getSystemService(
        context,
        NotificationManager::class.java
    ) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)
}

//--------------------------------------------------------------------------------------------------
private fun showNotification(context: Context) {
    val notificationManager = ContextCompat.getSystemService(
        context,
        NotificationManager::class.java
    ) as NotificationManager

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setContentTitle("content_title")
        .setContentText("content_text")
        .setVibrate(longArrayOf(0, 1_000, 1_000, 1_000))
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
    /*
    "reminds_link" and "reminds_check_in" work, but they don't light up external display.
    "LegacyVoicemail_" works same as "VisualVoicemail_".
    */
    notificationManager.notify("VisualVoicemail_", NOTIFICATION_ID, notification)
}

//--------------------------------------------------------------------------------------------------
private fun setAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent = Intent(context, AlarmReceiver::class.java)
    val alarmPendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        alarmIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    AlarmManagerCompat.setExactAndAllowWhileIdle(
        alarmManager,
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + TIMER_EXPIRATION,
        alarmPendingIntent
    )
}

//--------------------------------------------------------------------------------------------------
