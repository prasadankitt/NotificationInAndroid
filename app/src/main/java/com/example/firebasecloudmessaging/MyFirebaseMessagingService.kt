package com.example.firebasecloudmessaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.notification?.title
            ?.let {
                message.notification!!.body?.let {
                        it1 -> NotificationBuild().generateAndDisplayNotification(applicationContext,it, it1) } }
    }
}