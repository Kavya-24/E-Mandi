package com.example.mandiexe.ui.authUi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mandiexe.R
import com.example.mandiexe.ui.home.MainActivity
import com.example.mandiexe.utils.auth.PreferenceManager

class SplashActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var splash_display_time: Long = 1000
    private val preferenceManager: PreferenceManager = PreferenceManager()
    private val TAG = SplashActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            R.layout.activity_splash
        )
        handler = Handler()

        /*val preferences =
            getSharedPreferences(getString(R.string.intro_prefs), Context.MODE_PRIVATE)
        val firstRun = preferences.getBoolean(getString(R.string.intro_prefs_first_run), true)

        if (firstRun) {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()




        }
        */


        val intent = if (preferenceManager.authToken!!.isEmpty()) {
            Log.e(TAG, "In no access token")
            Intent(this, LanguageFragment::class.java)
        } else {
            Log.e(TAG, "In success atoken")
            Intent(this, MainActivity::class.java)

        }


        handler = Handler()

        runnable = Runnable {
            startActivity(intent)
            finish()
        }

        handler.postDelayed(runnable, splash_display_time)





    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)

    }
}
