package org.solamour.myfoo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.solamour.myfoo.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
        const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE = 1
        val TIMER_EXPIRATION = TimeUnit.SECONDS.toMillis(45)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener {
            foo()
        }

        foo()
    }

    private fun foo() {
        /*
        Snackbar.make(
            binding.root,
            "Hello World",
            Snackbar.LENGTH_LONG
        ).show()
        */

        Toast.makeText(this, "Hello World", Toast.LENGTH_LONG).show()
    }

    private fun createNotificationChannel() {
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
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(notificationChannel)
    }

    private fun showNotification() {
        val notificationManager = ContextCompat.getSystemService(
            this@MainActivity,
            NotificationManager::class.java
        ) as NotificationManager

        val notification = NotificationCompat.Builder(this@MainActivity, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Content Title")
            .setContentText("contentText")
            .setVibrate(longArrayOf(0, 1000, 1000, 1000))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify("VisualVoicemail_", NOTIFICATION_ID, notification)
    }
}

/*--------------------------------------------------------------------------------------------------
val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
val alarmIntent = Intent(this, AlarmReceiver::class.java)
val alarmPendingIntent = PendingIntent.getBroadcast(
    this,
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

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val notification = NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Content Title")
            .setContentText("contentText")
            .setVibrate(longArrayOf(0, 1000, 1000, 1000))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify("VisualVoicemail_", MainActivity.NOTIFICATION_ID, notification)
    }
}
*/
