package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(context: Context) {
    val contentIntent = Intent(context, DetailActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
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
            null
        )
        priority = NotificationCompat.PRIORITY_DEFAULT
    }

    notify(NOTIFICATION_ID, builder.build())
}