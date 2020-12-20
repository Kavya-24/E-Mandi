package com.example.mandiexe.ui.authUi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.mandiexe.R
import com.example.mandiexe.utils.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.auth.PreferenceUtil


class LoginActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val pref = PreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference(), this)
        setContentView(R.layout.activity_login)

        val navControllerLogin = findNavController(R.id.nav_host_fragment_login)



    }


    override fun onSupportNavigateUp(): Boolean {
        val navControllerLoginActivity = findNavController(R.id.nav_host_fragment_login)
        return navControllerLoginActivity.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
