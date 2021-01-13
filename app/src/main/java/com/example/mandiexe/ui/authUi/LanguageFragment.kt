package com.example.mandiexe.ui.authUi

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.LanguagesAdapter
import com.example.mandiexe.adapter.OnMyLanguageListener
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.viewmodels.LanguageViewModel
import java.util.*


class LanguageFragment : AppCompatActivity(), OnMyLanguageListener {

    companion object {
        fun newInstance() = LanguageFragment()
    }

    private lateinit var viewModel: LanguageViewModel
    private var pref = PreferenceUtil

    private val TAG = LanguageFragment::class.java.simpleName

    //To be replaced by rv of languge
    //Default will be english


    private fun createLanguageList() {

        val mLanguages: MutableList<LanguageBody> = mutableListOf()

        //0
        mLanguages.add(LanguageBody("English", "en"))

        //1
        mLanguages.add(LanguageBody("हिंदी", "hi"))

        //2
        mLanguages.add(LanguageBody("বাংলা", "bn"))

        //3
        mLanguages.add(LanguageBody("मराठी", "mr"))

        //4 Tamil
        mLanguages.add(LanguageBody("தமிழ்", "ta"))

        //5 Telugu
        mLanguages.add(LanguageBody("తెలుగు", "te"))


        val rv = findViewById<RecyclerView>(R.id.rv_language_main)!!
        Log.e(TAG, "Rv is " + rv.toString())

        rv.layoutManager = LinearLayoutManager(this)

        val mAdapter = LanguagesAdapter(this)
        mAdapter.lst = mLanguages
        rv.adapter = mAdapter


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExternalUtils.setAppLocale(pref.getLanguageFromPreference().toString(), this)
        setContentView(R.layout.language_fragment)

        //Change the language of the toolbar
        setTitle(R.string.choose_language)

        createLanguageList()
    }

    override fun selectLanguage(_listItem: LanguageBody, position: Int) {
        //Use keys

        Log.e(TAG, "In selectLanguage")

        when (position) {

            0 -> {
                setLocale("en")
                //Recreate
                recreateModel("en")
            }
            1 -> {
                setLocale("hi")
                recreateModel("hi")
            }
            2 -> {
                setLocale("bn")
                recreateModel("bn")
            }

            3 -> {
                setLocale("mr")
                recreateModel("mr")
            }
            4 -> {
                setLocale("ta")
                recreateModel("ta")
            }
            5 -> {
                setLocale("te")
                recreateModel("te")
            }


            else -> {
                setLocale("en")
                recreateModel("en")
            }

        }

        //Naviagte to the new ACTRIVTY
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()

    }

    private fun recreateModel(s: String) {
        //Do nothing here

    }


    private fun setLocale(s: String) {
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


        Log.e(TAG, "In set local" + pref.getLanguageFromPreference().toString())
        //Now for the system
        val editor: SharedPreferences.Editor = getSharedPreferences(
            "Settings",
            MODE_PRIVATE
        )!!.edit()
        editor.putString("My_Lang", s)
        editor.apply()


    }


}
