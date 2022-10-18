package com.example.firebasecloudmessaging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class ProfileActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()

        FirebaseMessaging.getInstance().subscribeToTopic("weather")
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                saveToken(token)
            }
            else {
                return@OnCompleteListener
            }
        })
    }

    override fun onStart() {
        super.onStart()
        //User is LoggedOut send user to mainActivity to Login
        if(mAuth.currentUser == null)
        {
            val intent = Intent(this,MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }

    private fun saveToken(token: String) {
        val email = mAuth.currentUser?.email
        val user = email?.let { User(it,token) }

        val dbUsers = FirebaseDatabase.getInstance().getReference("users")
        dbUsers.child(mAuth.currentUser!!.uid)
            .setValue(user).addOnCompleteListener(OnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Token Saved",Toast.LENGTH_SHORT).show()
                }
            })
    }
}