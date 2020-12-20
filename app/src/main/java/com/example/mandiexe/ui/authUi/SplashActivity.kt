package com.example.mandiexe.ui.authUi

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.mandiexe.R
import com.example.mandiexe.utils.auth.PreferenceManager

class SplashActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var splash_display_time: Long = 500
    private val preferenceManager: PreferenceManager = PreferenceManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            R.layout.activity_splash
        )


    }
}
