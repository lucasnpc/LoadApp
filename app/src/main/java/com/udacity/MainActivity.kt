package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var downloadID: Pair<Long, String> = Pair(0, "")
    private val downloadManager by lazy {
        getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
    }

    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        with(binding.contentMain) {
            customButton.buttonState.observe(this@MainActivity) { state ->
                when (state) {
                    ButtonState.Completed -> {
                        mainLayout.progress = 0F
                        customButton.buttonDelegateState = state
                    }
                    ButtonState.Loading -> {}
                    ButtonState.Clicked -> {
                        download(url)
                        customButton.buttonDelegateState = state
                    }
                }
            }
        }
        createChannel(getString(R.string.channel_id), getString(R.string.channel_name))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val status = downloadID.first == id
            notificationManager.cancelAll()
            when (downloadID.second) {
                GLIDE_URL -> {
                    notificationManager.sendNotification(
                        context = this@MainActivity,
                        downloadStatus = status,
                        urlDownloaded = getString(R.string.glide_description)
                    )
                }
                RETROFIT_URL -> {
                    notificationManager.sendNotification(
                        this@MainActivity,
                        downloadStatus = status,
                        urlDownloaded = getString(R.string.retrofit_description)
                    )
                }
                APP_URL -> {
                    notificationManager.sendNotification(
                        this@MainActivity,
                        downloadStatus = status,
                        urlDownloaded = getString(R.string.loadapp_description)
                    )
                }
            }

        }
    }

    private fun download(url: String) {
        if (url.isEmpty()) {
            Toast.makeText(this, getString(R.string.unselected_message), Toast.LENGTH_SHORT).show()
            return
        }
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID = Pair(downloadManager.enqueue(request), url)
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.radio_glide ->
                    if (checked) {
                        url = GLIDE_URL
                    }
                R.id.radio_app ->
                    if (checked) {
                        url = APP_URL
                    }
                R.id.radio_retrofit -> {
                    if (checked) {
                        url = RETROFIT_URL
                    }
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "Download Finished"
                setShowBadge(false)
            }

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
        const val DOWNLOAD_COMPLETED = "download_completed"
        const val URL_DOWNLOADED = "url_downloaded"
    }

}
