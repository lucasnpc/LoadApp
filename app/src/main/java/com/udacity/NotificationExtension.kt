package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.udacity.MainActivity.Companion.DOWNLOAD_COMPLETED
import com.udacity.MainActivity.Companion.URL_DOWNLOADED

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    context: Context,
    downloadStatus: Boolean,
    urlDownloaded: String
) {
    val contentIntent =
        Intent(context, DetailActivity::class.java)
            .putExtras(
                bundleOf(
                    DOWNLOAD_COMPLETED to downloadStatus,
                    URL_DOWNLOADED to urlDownloaded
                )
            )

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    )
    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.channel_id)
    ).apply {
        setSmallIcon(R.drawable.ic_assistant_black_24dp)
        setContentTitle(
            context
                .getString(R.string.notification_title)
        )
        setContentText(context.getString(R.string.notification_description))
        setContentIntent(contentPendingIntent)
        setAutoCancel(true)
        addAction(
            R.drawable.ic_assistant_black_24dp,
            context.getString(R.string.notification_button),
            contentPendingIntent
        )
        priority = NotificationCompat.PRIORITY_DEFAULT
    }

    notify(NOTIFICATION_ID, builder.build())
}