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
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.messaging.FirebaseMessaging

//For different  devices we have different tokens so we need authentication of each devices
// so we authenticate using email and passwords

class MainActivity : AppCompatActivity() {

    private val channelId = "MyNotifications"
    private val channelName = "MyNotifications"
    private val channelDesc = "This is notification"

    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var signUp : Button
    private lateinit var pb : ProgressBar
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        confirmVersion()

        mAuth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        signUp = findViewById(R.id.signUp)
        pb = findViewById(R.id.pb)
        pb.visibility = View.INVISIBLE



        // When the device installs this app an unique registration token generated for every unique device
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("token", "Refreshed Token : $token")
            }
            else {
                Log.w("token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
        })



        signUp.setOnClickListener{
            createUser()
        }

    }

//Authentication using firebase Authentication
    private fun createUser() {
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()

        if(emailText.isEmpty()){
            email.error = "Email Required"
            email.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.error = "Proper Email Required"
            email.requestFocus()
            return
        }
        if(passwordText.isEmpty() || passwordText.length<=6){
            password.error = "Password Required with more than 6 characters"
            password.requestFocus()
            return
        }

        pb.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                startProfileActivity()
            }
            else{
                if(task.exception is FirebaseAuthUserCollisionException){
                    userLogin(emailText,passwordText)
                }
                else{
                    pb.visibility = View.INVISIBLE
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun userLogin(emailText: String, passwordText: String) {
        mAuth.signInWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                startProfileActivity()
            }
            else{
                pb.visibility = View.INVISIBLE
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startProfileActivity()
    {
        val intent = Intent(this,ProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
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