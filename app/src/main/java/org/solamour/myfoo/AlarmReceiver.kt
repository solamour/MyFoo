package org.solamour.myfoo

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.solamour.myfoo.MyFooActivity.Companion.NOTIFICATION_CHANNEL_ID
import org.solamour.myfoo.MyFooActivity.Companion.NOTIFICATION_ID

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Content Title")
            .setContentText("contentText")
            .setVibrate(longArrayOf(0, 1_000, 1_000, 1_000))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify("VisualVoicemail_", NOTIFICATION_ID, notification)
    }
}
