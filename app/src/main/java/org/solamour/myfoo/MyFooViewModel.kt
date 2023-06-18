package org.solamour.myfoo

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thedeanda.lorem.LoremIpsum
import org.solamour.myfoo.MyFooActivity.Companion.NOTIFICATION_CHANNEL_ID
import org.solamour.myfoo.MyFooActivity.Companion.NOTIFICATION_ID
import org.solamour.myfoo.MyFooActivity.Companion.REQUEST_CODE
import org.solamour.myfoo.MyFooActivity.Companion.TIMER_EXPIRATION
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyFooViewModel(
    private val application: Application,
) : ViewModel() {
    var logList = mutableStateListOf<String>()
        private set

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                MyFooViewModel(application)
            }
        }

        private val TAG = MyFooViewModel::class.qualifiedName
    }

    init {
    }

    fun log(string: String = "") {
        val timestamp = DateTimeFormatter.ofPattern("mm:ss.SSS").format(LocalDateTime.now())
        val log = if (string.isEmpty()) string else "[$timestamp] $string"
        logList.add(log)
        Log.d(TAG, log)
    }

    fun onClearLog() {
        logList.clear()
    }

    fun onPlay() {
        log(LoremIpsum.getInstance().firstNameFemale)
    }

    private fun showNotification() {
        val context = application.applicationContext
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("content_title")
            .setContentText("content_text")
            .setVibrate(longArrayOf(0, 1000, 1000, 1000))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        /*
        "reminds_link" and "reminds_check_in" work, but they don't light up external display.
        "LegacyVoicemail_" works same as "VisualVoicemail_".
        */
        notificationManager.notify("VisualVoicemail_", NOTIFICATION_ID, notification)
    }

    private fun setAlarm() {
        val context = application.applicationContext
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
}
