package com.example.mandiexe.ui.home

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.LanguagesAdapter
import com.example.mandiexe.adapter.OnMyLanguageListener
import com.example.mandiexe.adapter.mSearchViewOnSuggestionClick
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.models.body.supply.CropSearchAutoCompleteBody
import com.example.mandiexe.models.responses.supply.CropSearchAutocompleteResponse
import com.example.mandiexe.ui.authUi.LoginActivity
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.Communicator
import com.example.mandiexe.utils.auth.PreferenceManager
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.OfflineTranslate.transliterateToDefault
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.SearchAdapter
import retrofit2.Call
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), Communicator, OnMyLanguageListener,
    mSearchViewOnSuggestionClick {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView

    //App Update
    private var appUpdateManager: AppUpdateManager? = null
    private var RC_APP_UPDATE = 1249

    private val pref = PreferenceUtil
    private val sessionManager = SessionManager(ApplicationUtils.getContext())


    private var searchQuery = ""

    //private lateinit var searchView: android.widget.SearchView
    private var mAdapter: SimpleCursorAdapter? = null

    private val ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION"
    private val VOICE_REC_CODE = 1234

    //Change Language
    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog

    private lateinit var mMenu: Menu


    //SearchUtils
    private lateinit var search_view: com.miguelcatalan.materialsearchview.MaterialSearchView
    private lateinit var mListOfSuggestions: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference().toString(), this)
        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_my_supplies, R.id.nav_supply, R.id.nav_bid, R.id.nav_my_requirements
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Update the drawer
        //Get the search view
        updateDrawerDetails()

        search_view = findViewById(R.id.search_view)

        mListOfSuggestions = mutableListOf()

        // search_view.setCursorDrawable(R.drawable.ic_call_made_black_24dp)

        val crops: Array<String> = resources.getStringArray(R.array.arr_crop_names)
        search_view.setAdapter(SearchAdapter(this@MainActivity, crops))

        //My SearchView
        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Do some magic
                if (!query.isNullOrEmpty()) {
                    Log.e("MAIN", "In on query submit")
                    searchCrop(query ?: resources.getString(R.string.rice))

                }
                return false

            }


            override fun onQueryTextChange(newText: String?): Boolean {

                //Do some magic
                // fetchSuggestions(newText ?: resources.getString(R.string.rice))
                //search_view.setSuggestions(mListOfSuggestions.toTypedArray())


                return false
            }
        })

        search_view.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                Log.e("MAIN", "In onSearchViewShown()")
                //Do some magic
            }

            override fun onSearchViewClosed() {
                Log.e("MAIN", "In onSearchViewClosed")
                search_view.dismissSuggestions()
                search_view.clearFocus()
                return
                //Do some magic
            }
        })

        search_view.setVoiceSearch(true)
        search_view.showVoice(true)


        val v = search_view.findViewById<ImageView>(R.id.action_voice_btn)
        v.setOnClickListener {
            createVoiceIntent()
        }

        val et = search_view.findViewById<EditText>(R.id.searchTextView)
        et.setHint(resources.getString(R.string.searchHint))
        val mView = layoutInflater.inflate(R.layout.suggest_item, null)
        val sugIcon = mView.findViewById<ImageView>(R.id.suggestion_icon)
        sugIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_call_made_black_24dp, null))


        search_view.setOnItemClickListener { parent, view, position, id ->

            Log.e("MAIN", "In item clicked" + parent.getItemAtPosition(position).toString())
            val q = parent.getItemAtPosition(position).toString()
            search_view.dismissSuggestions()
            search_view.clearFocus()
            search_view.setQuery("", false)

            searchCrop(q)
            search_view.visibility = View.GONE
        }


    }

    private fun createVoiceIntent() {

        val Voiceintent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        //Put language
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale(pref.getLanguageFromPreference() + "-IN")
        )
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            resources.getString(R.string.searchHead)
        )
        startActivityForResult(Voiceintent, VOICE_REC_CODE)

    }

    private fun updateDrawerDetails() {


        val NAME = pref.name
        val PHONE = pref.getNumberFromPreference().toString()

        val v: View = navView.getHeaderView(0)

        //Check the presence of the View
        (v.findViewById<View>(R.id.tv_header_name) as TextView).text = NAME
        (v.findViewById<View>(R.id.tv_header_number) as TextView).text = PHONE


    }

    private fun fetchSuggestions(query: String) {

        //Clear adapter
        if (query == "") {
            return
        }

        val service = RetrofitClient.makeCallsForSupplies(this)

        //Translate to English to get the English version

        //Gte the temp tv
        val mTV = findViewById<TextView>(R.id.tempTvMain)
        val eTV = findViewById<TextView>(R.id.tempTvMainToEng)

        //Use Translate ViewModel
        OfflineTranslate.translateToEnglish(this, query, eTV)


        //Pause
        val mHandler = Handler()

        if (eTV.text == resources.getString(R.string.noDesc)) {
            mHandler.postDelayed({}, 2000)
        }

        val body = CropSearchAutoCompleteBody(eTV.text.toString())


        service.getCropAutoComplete(
            body = body,
        ).enqueue(object : retrofit2.Callback<CropSearchAutocompleteResponse> {
            override fun onFailure(call: Call<CropSearchAutocompleteResponse>, t: Throwable) {

                val message = ExternalUtils.returnStateMessageForThrowable(t)
                Log.e("Main", "In failure " + message)
            }

            override fun onResponse(
                call: Call<CropSearchAutocompleteResponse>,
                response: Response<CropSearchAutocompleteResponse>
            ) {
                Log.e("MAIN", "In search response " + response.body()?.suggestions)
                if (response.isSuccessful) {

                    mListOfSuggestions.clear()
                    for (y: CropSearchAutocompleteResponse.Suggestion in response.body()!!.suggestions) {
                        OfflineTranslate.translateToDefault(this@MainActivity, y.name, mTV)
                        mListOfSuggestions.add(mTV.text.toString())
                    }

                    Log.e("MAIN", "Now MlistSugg:" + mListOfSuggestions.toString())


                }

            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        mMenu = menu

//        val item = menu.findItem(R.id.action_main_search)
//        search_view.setMenuItem(item)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.e("MAIN", "In onSelected")
        when (item.itemId) {

            R.id.action_change_language -> changeLanguage()
            R.id.action_logout -> logout()
            R.id.action_main_search -> {
                search_view.showSearch()
                search_view.visibility = View.VISIBLE
            }
            //  R.id.action_notification -> showNotification()
            // R.id.action_show_walkthrough -> showWalkthrough()


        }

        return super.onOptionsItemSelected(item)
    }

    private fun logout() {


        PreferenceManager().clear()
        PreferenceUtil.clearPrefData()
        val i = Intent(this, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        finishAffinity()
        startActivity(i)
        finish()
    }

    private fun searchCrop(query: String) {

        val eTV = findViewById<TextView>(R.id.tempTvMainToFinalSug)

        com.example.mandiexe.utils.usables.OfflineTranslate.translateToEnglish(
            this@MainActivity,
            query,
            eTV
        )

        val mHandler = Handler()

        if (eTV.text == resources.getString(R.string.noDesc)) {
            //Not translated yet
            //Wait for 3 secomds
            mHandler.postDelayed({}, 2000)
        }

        val bundle = bundleOf(
            "crop" to query,
        )

        val i = Intent(this@MainActivity, SearchResultActivity::class.java)
        i.putExtra("bundle", bundle)
        i.action = Intent.ACTION_SEARCH
        startActivity(i)


    }

    private fun showWalkthrough() {
    }

    private fun showNotification() {
    }

    private fun changeLanguage() {
        //Create dialog
        d = androidx.appcompat.app.AlertDialog.Builder(this)

        val v = layoutInflater.inflate(R.layout.layout_language_change, null)
        d.setView(v)

        val rv = v.findViewById<RecyclerView>(R.id.rv_language_options) as RecyclerView
        fillLanguages(rv)


        d.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            //Do nothing
        }
        d.create()

        tempRef = d.create()
        d.show()


    }

    private fun fillLanguages(rv: RecyclerView) {
        val mLanguages = ExternalUtils.getSupportedLanguageList()
        val adapter = LanguagesAdapter(this)
        adapter.lst = mLanguages
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //App Update
    private fun updateApp() {

        appUpdateManager = AppUpdateManagerFactory.create(this)
        installStateUpdatedListener?.let { appUpdateManager!!.registerListener(it) }

    }

    //Install State Listener
    var installStateUpdatedListener: InstallStateUpdatedListener? =
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(state: InstallState) {
                when {
                    state.installStatus() === InstallStatus.DOWNLOADED -> {
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        popupSnackbarForCompleteUpdate()
                    }
                    state.installStatus() === InstallStatus.INSTALLED -> {
                        appUpdateManager?.unregisterListener(this)
                    }
                    else -> {
                        Log.i(
                            "Activity",
                            "InstallStateUpdatedListener: state: " + state.installStatus()
                        )
                    }
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {


        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != Activity.RESULT_OK) {
                Log.e("Android", "onActivityResult: app download failed")
            }
        } else if (requestCode == VOICE_REC_CODE) {

            Log.e("IN ARC", "")
            if (data != null) {
                //Put result
                val res: java.util.ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val resultInEnglish = res?.get(0)

                //1. Transliterate
                val transliteratedValue = transliterateToDefault(resultInEnglish)
                //2. Set this over
                search_view.setQuery(transliteratedValue, false)
            }

        }

        super.onActivityResult(requestCode, resultCode, data)

    }

    //PopUpView
    private fun popupSnackbarForCompleteUpdate(): Unit {
        val snackbar: Snackbar = Snackbar.make(
            findViewById(R.id.drawer_layout),
            "App has been updated",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Install") { view ->
            appUpdateManager?.completeUpdate()
        }
        snackbar.setActionTextColor(resources.getColor(R.color.wildColor))
        snackbar.show()
    }

    //States
    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ updateApp() }, 1000)
    }

    //Comunicator function
    override fun goToAddStocks(fragment: Fragment) {

    }

    override fun onBackPressed() {

        if (search_view.isSearchOpen()) {
            search_view.dismissSuggestions();
            search_view.clearFocus()
        } else {
            super.onBackPressed();
        }
    }

    override fun selectLanguage(_listItem: LanguageBody, position: Int) {
        val newLocale = ExternalUtils.getLocaleFromAdapterIndex(position)
        setLocale(newLocale)
        //Now we need to recreate this activty
        recreate()

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
        val editor: SharedPreferences.Editor = getSharedPreferences(
            "Settings",
            MODE_PRIVATE
        )!!.edit()
        editor.putString("My_Lang", s)
        editor.apply()


    }


}
