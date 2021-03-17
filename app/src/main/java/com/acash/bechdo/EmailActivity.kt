package com.acash.bechdo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.acash.bechdo.fragments.emailactivity.SignInFragment

class EmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SignInFragment())
            .commit()
    }
}