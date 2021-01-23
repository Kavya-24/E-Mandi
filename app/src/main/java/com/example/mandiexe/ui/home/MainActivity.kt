package com.example.mandiexe.ui.home

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.database.MatrixCursor
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
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
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.example.mandiexe.R
import com.example.mandiexe.adapter.LanguagesAdapter
import com.example.mandiexe.adapter.OnMyLanguageListener
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.models.body.supply.CropSearchAutoCompleteBody
import com.example.mandiexe.models.responses.supply.CropSearchAutocompleteResponse
import com.example.mandiexe.ui.search.AutocompleteSuggestion
import com.example.mandiexe.ui.search.MyDataHelper
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.Communicator
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import retrofit2.Call
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), Communicator, OnMyLanguageListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView

    //App Update
    private var appUpdateManager: AppUpdateManager? = null
    private var RC_APP_UPDATE = 1249

    private val pref = PreferenceUtil
    private val sessionManager = SessionManager(ApplicationUtils.getContext())
    private lateinit var drawerLayout: DrawerLayout

    private var searchQuery = ""
    private lateinit var searchView: android.widget.SearchView
    private var mAdapter: SimpleCursorAdapter? = null

    private val ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION"
    private val VOICE_REC_CODE = 1234

    //Change Language
    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog

    private lateinit var mMenu: Menu
    private lateinit var mSearchView: FloatingSearchView
    private lateinit var newSuggestions: MutableList<SearchSuggestion>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference().toString(), this)
        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        drawerLayout = findViewById(R.id.drawer_layout)
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


        //Set the app bar
        val mAppBar = findViewById<AppBarLayout>(R.id.mainAppBar)

        mSearchView = findViewById(R.id.floating_search_view)
        newSuggestions = mutableListOf()

        //Set app bar
        mAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                Log.e("MAIN", "In offset changed from main")
                mSearchView.translationY = verticalOffset.toFloat()
            }
        })

        //Add drawer
        mSearchView.attachNavigationDrawerToMenuButton(drawerLayout);

        setupSearchBar()


    }

    private fun setupSearchBar() {

        mSearchView.setOnQueryChangeListener { oldQuery, newQuery -> //get suggestions based on newQuery

            //pass them on to the search view
            if (oldQuery != "" && newQuery == "") {
                mSearchView.clearSuggestions()
            } else {

                //this shows the top left circular progress
                //you can call it where ever you want, but
                //it makes sense to do it when loading something in
                //the background.
                mSearchView.showProgress()
                mSearchView.clearSuggestions()
                fetchSuggestions(newQuery)
                mSearchView.swapSuggestions(newSuggestions)
                Log.e("MAIN", "After setting sugestions")
                mSearchView.hideProgress()

            }
        }

        mSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
                val queryClicked = searchSuggestion.body
                val eTV = findViewById<TextView>(R.id.tempTvMainToFinalSug)

                com.example.mandiexe.utils.usables.OfflineTranslate.translateToEnglish(
                    this@MainActivity,
                    queryClicked,
                    eTV
                )

                Log.e(
                    "MAIN",
                    "In suggestions clicked, query is $queryClicked and translated is ${eTV.text}"
                )
                val mHandler = Handler()

                if (eTV.text == resources.getString(R.string.noDesc)) {
                    //Not translated yet
                    //Wait for 3 secomds
                    mHandler.postDelayed({}, 2000)
                }

                val bundle = bundleOf(
                    "crop" to queryClicked,
                )

                val i = Intent(this@MainActivity, SearchResultActivity::class.java)
                i.putExtra("bundle", bundle)
                i.action = Intent.ACTION_SEARCH
                startActivity(i)

            }

            override fun onSearchAction(query: String) {

                Log.e("MAIN", "In search from key board")
                //Do nothing here
                return
            }
        })

        mSearchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)

                mSearchView.swapSuggestions(MyDataHelper.getHistory(this@MainActivity, 3))
                Log.d("MAIN", "onFocus()")
            }

            override fun onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle("")
                mSearchView.clearQuery()
                mSearchView.clearSearchFocus()
                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d("MAIN", "onFocusCleared()")
            }
        })

        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener { item ->

            Log.e("MAIN", "In menu ite click")
            if (item.itemId == R.id.action_change_language) {
                changeLanguage()
            }

        }

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener { Log.e("MAIN", "onHomeClicked()") }

        mSearchView.setOnBindSuggestionCallback { suggestionView, leftIcon, textView, item, itemPosition ->

            //Like on bind for the adapter
            leftIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_call_made_black_24dp,
                    null
                )
            )


        }


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
            mHandler.postDelayed({}, 3000)
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
                Log.e("Main", "In search response " + response.body()?.suggestions)
                if (response.isSuccessful) {

                    val strAr = mutableListOf<String>()


                    for (y: CropSearchAutocompleteResponse.Suggestion in response.body()!!.suggestions) {
                        OfflineTranslate.translateToDefault(this@MainActivity, y.name, mTV)
                        strAr.add(mTV.text.toString())
                        val acs = AutocompleteSuggestion(mTV.text.toString(), false)
                        newSuggestions.add(acs)

                    }

                    //Now strAr is a list of such options
                    Log.e("STr", strAr.toString())
                    Log.e("Str", strAr.size.toString())
                    Log.e("STR", "\nNEw Suggs " + newSuggestions.toString())
                    //We need to use the search suggestion


                    val c =
                        MatrixCursor(arrayOf(BaseColumns._ID, "suggestionList"))
                    for (i in 0 until strAr.size) {
                        c.addRow(arrayOf(i, strAr[i]))
                    }


                    mAdapter?.changeCursor(c)
                }

            }
        })

        searchView.setOnClickListener {
            searchCrop()
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        mMenu = menu


//        searchView =
//            mMenu.findItem(R.id.action_main_search).actionView as android.widget.SearchView
//
//
//        Log.e("MAIN", "In onCreateOptions")
//        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
//
//        val from = arrayOf("suggestionList")
//        val to = intArrayOf(R.id.tvSearchDefault)
//        // val toDefault = intArrayOf(R.id.tvSearchDefault)
//
//        mAdapter = SimpleCursorAdapter(
//            this,
//            R.layout.item_search,
//            null,
//            from,
//            to,
//            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
//        )
//
//
//        searchView.suggestionsAdapter = mAdapter
//        searchView.isIconifiedByDefault = true
//
//
//        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
//
//            override fun onSuggestionSelect(position: Int): Boolean {
//                return true
//            }
//
//            override fun onSuggestionClick(position: Int): Boolean {
//
//                val cursor: Cursor = mAdapter!!.getItem(position) as Cursor
//                val txt = cursor.getString(cursor.getColumnIndex("suggestionList"))
//                val eTV = findViewById<TextView>(R.id.tempTvMainToFinalSug)
//
//                com.example.mandiexe.utils.usables.OfflineTranslate.translateToEnglish(
//                    this@MainActivity,
//                    txt,
//                    eTV
//                )
//
//                val mHandler = Handler()
//
//                if (eTV.text == resources.getString(R.string.noDesc)) {
//                    //Not translated yet
//                    //Wait for 3 secomds
//                    mHandler.postDelayed({}, 2000)
//                }
//
//                val bundle = bundleOf(
//                    "crop" to txt,
//                )
//
//                val i = Intent(this@MainActivity, SearchResultActivity::class.java)
//                i.putExtra("bundle", bundle)
//                i.action = Intent.ACTION_SEARCH
//                startActivity(i)
//
//                return true
//
//
//            }
//        })
//
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                Log.e("Main", "In query change")
//                fetchSuggestions(newText.toString())
//                return false
//            }
//        })
//
//
//        //Close searchView
//        searchView.setOnCloseListener {
//            hideKeyboard(this@MainActivity, this@MainActivity)
//            searchView.clearFocus()
//            searchView.isIconified = false
//            Log.e("MAIN", "In close")
//
//            false
//        }
//
//
//        searchView.setOnSearchClickListener {
//            Log.e("MAIN", "In search click")
//
//        }
//

//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Log.e("MAIN", "In onSelected")
//        when (item.itemId) {
//
//            R.id.action_change_language -> changeLanguage()
//            //  R.id.action_notification -> showNotification()
//            // R.id.action_show_walkthrough -> showWalkthrough()
////            R.id.action_main_search -> launchSearch()
//
//
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
//

    private fun searchCrop() {

//        Log.e("MAIN", "In search crop")
//        val mic = resources.getIdentifier("android:id/search_voice_btn", null, null)
//        val micImage = searchView.findViewById<View>(mic) as ImageView
//        micImage.setOnClickListener {
//
//            Log.e("MAIN", "In voice intent")
//            val Voiceintent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//            Voiceintent.putExtra(
//                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//            )
//
//            //Put language
//            Voiceintent.putExtra(
//                RecognizerIntent.EXTRA_LANGUAGE,
//                Locale(pref.getLanguageFromPreference() + "-IN")
//            )
//            Voiceintent.putExtra(
//                RecognizerIntent.EXTRA_PROMPT,
//                resources.getString(R.string.searchHead)
//            )
//            startActivityForResult(Voiceintent, VOICE_REC_CODE)
//
//
//        }


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

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
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
                searchView.setQuery(res?.get((0)), false)
                val resultInEnglish = res?.get(0)

                //  val tx =
                //    resultInEnglish?.let { ExternalUtils.transliterateFromEnglishToDefault(it) }
                //Log.e("Main", "Res in eng " + resultInEnglish + " transf" + tx)

            }

        }
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
        super.onBackPressed()

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

    fun onActivityBackPress(): Boolean {
        //if mSearchView.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if mSearchView.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
        return mSearchView.setSearchFocused(false)
    }


}
