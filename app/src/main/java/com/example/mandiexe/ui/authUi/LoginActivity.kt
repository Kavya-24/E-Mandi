package com.example.mandiexe.ui.authUi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.mandiexe.R
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale


class LoginActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val pref = PreferenceUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference(), this)
        setContentView(R.layout.activity_login)


        val navControllerLogin = findNavController(R.id.nav_host_fragment_login)


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_login, R.id.nav_otp, R.id.nav_signup
            )
        )


    }

    override fun onSupportNavigateUp(): Boolean {
        val navControllerLoginActivity = findNavController(R.id.nav_host_fragment_login)
        return navControllerLoginActivity.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
