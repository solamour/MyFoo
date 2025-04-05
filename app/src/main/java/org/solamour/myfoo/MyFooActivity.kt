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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.solamour.myfoo.ui.theme.MyFooTheme

class MyFooActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MyFooTheme(dynamicColor = false) {
                Home()
            }
        }

        createNotificationChannel(this)
    }
}

@Composable
private fun Home(
    viewModel: MyFooViewModel = viewModel(
        factory = MyFooViewModel.factory()
    )
) {
    MyFoo(
        logList = viewModel.logList,
        onAction = viewModel::onAction,
    )
}
