package org.solamour.myfoo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.solamour.myfoo.ui.theme.MyFooTheme
import java.util.concurrent.TimeUnit

class MyFooActivity : ComponentActivity() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
        const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE = 1
        val TIMER_EXPIRATION = TimeUnit.SECONDS.toMillis(45)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyFooTheme(dynamicColor = false) {
                val viewModel: MyFooViewModel = viewModel(
                    factory = MyFooViewModel.factory()
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyFoo(
                        logList = viewModel.logList,
                        onPlay = viewModel::onPlay,
                        onClearLog = viewModel::onClearLog,
                    )
                }
            }
        }

        createNotificationChannel()
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
}
