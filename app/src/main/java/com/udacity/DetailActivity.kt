package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.MainActivity.Companion.DOWNLOAD_COMPLETED
import com.udacity.MainActivity.Companion.URL_DOWNLOADED
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        with(binding.detailContent) {
            intent.extras?.let {
                repository = it.getString(URL_DOWNLOADED)
                isDownloaded = it.getBoolean(DOWNLOAD_COMPLETED)
            }
            backButton.setOnClickListener { onBackPressed() }
        }
    }
}
