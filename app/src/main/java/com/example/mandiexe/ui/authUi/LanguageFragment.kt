package com.example.mandiexe.ui.authUi

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.LanguagesAdapter
import com.example.mandiexe.adapter.OnMyLanguageListener
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.LocaleHelper
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

    private val TAG = LanguageFragment::class.java.simpleName

    //To be replaced by rv of languge
    //Default will be english

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.language_fragment, container, false)


        //Set the toolbar language
        //(activity as AppCompatActivity).supportActionBar?.title =
        //+  resources.getString(R.string.app_name)

        createLanguageList()




        return root
    }


    private fun createLanguageList() {

        val mLanguages: MutableList<LanguageBody> = mutableListOf()

        //0
        mLanguages.add(LanguageBody("English"))

        //1
        mLanguages.add(LanguageBody("हिंदी"))

        //2
        mLanguages.add(LanguageBody("বাংলা"))

        //3
        mLanguages.add(LanguageBody("मराठी"))

        //4 Tamil
        mLanguages.add(LanguageBody("தமிழ்"))

        //5 Telugu
        mLanguages.add(LanguageBody("తెలుగు"))


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

            else -> {
                setLocale("en")
                recreateModel("en")
            }

        }

        val navController = Navigation.findNavController(root)
        navController.navigate(R.id.action_nav_language_to_nav_login)

    }

    private fun recreateModel(s: String) {

        context?.let { LocaleHelper.onAttach(it, s) }
        activity?.recreate()

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
        val editor: SharedPreferences.Editor = context?.getSharedPreferences(
            "Settings",
            MODE_PRIVATE
        )!!.edit()
        editor.putString("My_Lang", s)
        editor.apply()


    }


}
