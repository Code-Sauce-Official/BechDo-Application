package com.acash.bechdo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.acash.bechdo.activities.IntroActivity
import com.acash.bechdo.activities.MainActivity
import com.acash.bechdo.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

const val CURRENT_USER = "Current User"

class SplashActivity : AppCompatActivity() {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        object : CountDownTimer(1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                    database.collection("users").document(auth.uid.toString()).get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                val currentUser = it.toObject(User::class.java)!!
                                startActivity(
                                    Intent(this@SplashActivity, MainActivity::class.java)
                                        .putExtra(CURRENT_USER, arrayOf(currentUser.name,currentUser.downloadUrlDp))
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            } else {
                                startActivity(
                                    Intent(this@SplashActivity, ProfileActivity::class.java)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@SplashActivity,it.message,Toast.LENGTH_SHORT).show()
                            finish()
                        }
                }else{
                    startActivity(
                        Intent(this@SplashActivity, IntroActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        }.start()
    }
}