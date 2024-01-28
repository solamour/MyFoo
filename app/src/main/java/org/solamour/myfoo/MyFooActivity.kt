package org.solamour.myfoo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home()
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

@Composable
private fun Home(
    viewModel: MyFooViewModel = viewModel(
        factory = MyFooViewModel.factory()
    )
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    DisposableEffect(navController) {
        val listener = Consumer<Intent> {
            println("jchun listener")
            if (it.action == Intent.ACTION_SEND && it.type == "text/plain") {
                it.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                    println("jchun $text")
                }
            }
        }
        context.requireActivity().addOnNewIntentListener(listener)
        onDispose { context.requireActivity().removeOnNewIntentListener(listener) }
    }

    NavHost(
        navController = navController,
        startDestination = "myfoo",
    ) {
        composable(
            route = "myfoo",
        ) { _ ->    // navBackStackEntry ->
            MyFoo(
                logList = viewModel.logList,
                onPlay = viewModel::onPlay,
                onClearLog = viewModel::onClearLog,
            )
        }
    }
}

fun Context.requireActivity(): ComponentActivity = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.requireActivity()
    else -> throw IllegalStateException("Compose $this not attached to Activity")
}
