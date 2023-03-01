package org.solamour.myfoo

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.solamour.myfoo.MyViewModel.Companion.url
import org.solamour.myfoo.databinding.ActivityMainBinding
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MyViewModel by viewModels {
        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()
        val webService = retrofit.create(WebService::class.java)

        MyViewModel.factory(webService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener {
            foo()
        }
        binding.textView.movementMethod = ScrollingMovementMethod()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.log.collect { log ->
                    binding.textView.text = log.joinToString(separator = "\n\n")
                }
            }
        }
    }

    private fun foo() {
        viewModel.log.update { mutableListOf() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.myfoo, menu)
        menu?.let {
            MenuCompat.setGroupDividerEnabled(menu, true)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.get_area_list -> {
                viewModel.getAreaList()
                true
            }
            R.id.get_product_list -> {
                viewModel.getProductList()
                true
            }

            R.id.create_csr -> {
                viewModel.createCsr()
                true
            }
            R.id.create_certificates -> {
                lifecycleScope.launch {
                    viewModel.createCertificates()
                }
                true
            }
            R.id.query_status -> {
                lifecycleScope.launch {
                    viewModel.queryStatus()
                }
                true
            }

            R.id.sign_with_device_key -> {
                viewModel.signWithDeviceKey()
                true
            }

            R.id.read_device_certificate -> {
                viewModel.readDeviceCertificate()
                true
            }
            R.id.read_certificate_chain -> {
                viewModel.readCertificateChain()
                true
            }

            R.id.write_certificates -> {
                viewModel.writeCertificates()
                true
            }

            R.id.get_crl -> {
                viewModel.getCrl()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

/*--------------------------------------------------------------------------------------------------
companion object {
    const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
    const val NOTIFICATION_ID = 1
    const val REQUEST_CODE = 1
    val TIMER_EXPIRATION = TimeUnit.SECONDS.toMillis(45)
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
