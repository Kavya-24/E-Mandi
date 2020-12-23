package com.example.mandiexe.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mandiexe.R
import com.example.mandiexe.utils.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.auth.PreferenceUtil

class SearchResultActivity : AppCompatActivity() {

    private val pref = PreferenceUtil
    private lateinit var args: Bundle

    private var crop = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference(), this)
        setContentView(R.layout.activity_search_result)

        //Get arugmenent
        args = intent?.getBundleExtra("bundle")!!
        crop = args.getString("crop").toString()


    }
}
