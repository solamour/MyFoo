package org.solamour.myfoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.solamour.myfoo.ui.theme.MyFooTheme

class MyFooActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MyFooTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize().windowInsetsPadding(
                        WindowInsets.safeDrawing.exclude(WindowInsets.statusBars)
                    ),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MyFoo()
                }
            }
        }

        createNotificationChannel(this)
    }
}
