package com.example.firebasecloudmessaging

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationBuild{

    fun getRemoteView(title: String,message: String) : RemoteViews
    {
        val remoteView = RemoteViews("com.example.firebasecloudmessaging",R.layout.notification)
        remoteView.setTextViewText(R.id.title,title)
        remoteView.setTextViewText(R.id.message,message)
        remoteView.setImageViewResource(R.id.logo,R.drawable.ic_stat_name)

        return remoteView
    }

    fun generateAndDisplayNotification(context: Context,title:String,message: String) {
        val pi: PendingIntent = PendingIntent.getActivity(context,0,
            Intent(context,MainActivity::class.java), PendingIntent.FLAG_MUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context,MainActivity().channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setContentText(message)

        notificationBuilder.setContent(getRemoteView(title,message))

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notificationBuilder.build());
    }
}