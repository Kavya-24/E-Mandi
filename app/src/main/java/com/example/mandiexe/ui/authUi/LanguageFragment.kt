package com.example.mandiexe.ui.authUi

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.LanguagesAdapter
import com.example.mandiexe.adapter.OnMyLanguageListener
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.ExternalUtils
import java.util.*


class LanguageFragment : AppCompatActivity(), OnMyLanguageListener {

    companion object {
        fun newInstance() = LanguageFragment()
    }

    private var pref = PreferenceUtil

    private val TAG = LanguageFragment::class.java.simpleName

    private fun createLanguageList() {

        val mLanguages = ExternalUtils.getSupportedLanguageList()

        val rv = findViewById<RecyclerView>(R.id.rv_language_main)!!

        rv.layoutManager = GridLayoutManager(this, 2)

        val mAdapter = LanguagesAdapter(this)
        mAdapter.lst = mLanguages
        rv.adapter = mAdapter

        mAdapter.notifyDataSetChanged()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExternalUtils.setAppLocale(pref.getLanguageFromPreference().toString(), this)
        setContentView(R.layout.language_fragment)


        createLanguageList()
    }

    override fun selectLanguage(_listItem: LanguageBody, position: Int) {
        //Use keys
        val newLocale = ExternalUtils.getLocaleFromAdapterIndex(position)
        setLocale(newLocale)

        //Navigate to the new ACTIVITY
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()

    }

    private fun setLocale(s: String) {

        //Get the Locale from string
        val locale = Locale(s)
        Locale.setDefault(locale)


        val config = Configuration()
        config.locale = locale
        ApplicationUtils.getContext().resources.updateConfiguration(
            config,
            ApplicationUtils.getContext().resources.displayMetrics
        )

        //Save data to the shared preference
        pref.setLanguageFromPreference(s)


        //Save in Shared Preference
        val editor: SharedPreferences.Editor = getSharedPreferences(
            "Settings",
            MODE_PRIVATE
        )!!.edit()
        editor.putString("My_Lang", s)
        editor.apply()


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        finishAffinity()
    }


}
