package com.example.firebasecloudmessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RemoteViews
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

// To make a notification we need -> Notification channel |  Notification Builder |  Notification manager

class MainActivity : AppCompatActivity() {

    private val channelId = "MyNotifications"
    private val channelName = "MyNotifications"
    private val channelDesc = "This is notification"
    private lateinit var myToken : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        confirmVersion()

        myToken = findViewById(R.id.tokenText)

        // When the device installs this app an unique registration token generated for that device
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("token", "Refreshed Token : $token")
                myToken.text = token
            }
            else {
                Log.w("token", "Fetching FCM registration token failed", task.exception)
                myToken.text = "Token not generated"
                return@OnCompleteListener
            }
        })

    }

    private fun confirmVersion()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = channelDesc
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getRemoteView(title: String,message: String) : RemoteViews
    {
        val remoteView = RemoteViews("com.example.firebasecloudmessaging",R.layout.notification)
        remoteView.setTextViewText(R.id.title,title)
        remoteView.setTextViewText(R.id.message,message)
        remoteView.setImageViewResource(R.id.logo,R.drawable.ic_stat_name)

        return remoteView
    }

    private fun generateAndDisplayNotification(title:String,message: String) {
        val pi: PendingIntent = PendingIntent.getActivity(this,0,
            Intent(this,MainActivity::class.java), PendingIntent.FLAG_MUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setContentText(message)

        notificationBuilder.setContent(getRemoteView(title,message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build());
    }
}