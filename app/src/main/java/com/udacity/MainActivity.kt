package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var downloadID: Pair<Long, String> = Pair(0, "")
    private val downloadManager by lazy {
        getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }


    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        with(binding.contentMain) {
            customButton.buttonState.observe(this@MainActivity) { state ->
                when (state) {
                    ButtonState.Completed -> {}
                    ButtonState.Loading -> {}
                    ButtonState.Clicked -> {
                        download(url)
                        customButton.buttonDelegateState = state
                    }
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID.first == id)
                when (downloadID.second) {
                    GLIDE_URL -> {
                        println("Glide")
                    }
                    RETROFIT_URL -> {
                        println("Retrofit")
                    }
                    APP_URL -> {
                        println("App")
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
        // enqueue puts the download request in the queue.
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

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }

}
