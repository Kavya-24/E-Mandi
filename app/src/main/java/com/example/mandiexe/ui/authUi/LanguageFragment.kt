package com.example.mandiexe.ui.authUi

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.LanguagesAdapter
import com.example.mandiexe.adapter.OnMyLanguageListener
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.viewmodels.LanguageViewModel
import java.util.*


class LanguageFragment : Fragment(), OnMyLanguageListener {

    companion object {
        fun newInstance() = LanguageFragment()
    }

    private lateinit var viewModel: LanguageViewModel
    private lateinit var root: View
    private var pref = PreferenceUtil

    //To be replaced by rv of languge
    //Default will be english

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.language_fragment, container, false)

        loadLocales()

        //Set the toolbar language
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.app_name)
        createLanguageList()



        return root
    }


    private fun createLanguageList() {

        val mLanguages: MutableList<LanguageBody> = mutableListOf()

        mLanguages.add(LanguageBody("English"))
        mLanguages.add(LanguageBody("हिंदी"))
        mLanguages.add(LanguageBody("বাংলা"))


        val rv = root.findViewById<RecyclerView>(R.id.rv_language_main)
        val mAdapter = LanguagesAdapter(this)
        mAdapter.lst = mLanguages
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = mAdapter


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LanguageViewModel::class.java)

    }

    override fun selectLanguage(_listItem: LanguageBody, position: Int) {
        //Use keys
        when (position) {

            0 -> {
                setLocale("en")
                //Recreate
                recreate(context as Activity)
            }
            1 -> {
                setLocale("hi")
                recreate(context as Activity)
            }
            2 -> {
                setLocale("bn")
                recreate(context as Activity)
            }

            else -> {
                setLocale("en")
                recreate(context as Activity)
            }

        }

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

        //Now for the system
        val editor: SharedPreferences.Editor = context?.getSharedPreferences(
            "Settings",
            MODE_PRIVATE
        )!!.edit()
        editor.putString("My_Lang", s)
        editor.apply()


    }

    private fun loadLocales() {

        val lang = pref.getLanguageFromPreference()
        val sharedPref = context?.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val langSystem = sharedPref?.getString("My_Lang", "")

        setLocale(langSystem ?: "en")


    }


}
