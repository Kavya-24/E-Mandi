package com.example.mandiexe.ui.home

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import androidx.annotation.Nullable
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
import com.example.mandiexe.R
import com.example.mandiexe.utils.Communicator
import com.example.mandiexe.utils.ExternalUtils.hideKeyboard
import com.example.mandiexe.utils.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus

class MainActivity : AppCompatActivity(), Communicator {

    private lateinit var appBarConfiguration: AppBarConfiguration

    //App Update
    private var appUpdateManager: AppUpdateManager? = null
    private var RC_APP_UPDATE = 1249

    private val pref = PreferenceUtil


    private var searchQuery = ""
    private lateinit var searchView: android.widget.SearchView
    private var mAdapter: SimpleCursorAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference().toString(), this)
        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }

    private fun fetchSuggestions(query: String) {
        //Write calls for this

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        //Get reference for the search view
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView =
            menu.findItem(R.id.action_main_search).actionView as android.widget.SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))


        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_change_language -> changeLanguage()
            R.id.action_notification -> showNotification()
            R.id.action_show_walkthrough -> showWalkthrough()
            R.id.action_main_search -> searchCrop()


        }

        return super.onOptionsItemSelected(item)
    }

    private fun searchCrop() {


        val from = arrayOf("suggestionList")
        val to = intArrayOf(android.R.id.text1)

        mAdapter = SimpleCursorAdapter(
            this,
            android.R.layout.simple_list_item_1,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        searchView.suggestionsAdapter = mAdapter
        searchView.isIconifiedByDefault = false

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {

            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {

                val cursor: Cursor = mAdapter!!.getItem(position) as Cursor
                val txt = cursor.getString(cursor.getColumnIndex("suggestionList"))

                val bundle = bundleOf(
                    "crop" to txt,
                )

                val i = Intent(this@MainActivity, SearchResultActivity::class.java)
                i.putExtra("bundle", bundle)
                i.action = Intent.ACTION_SEARCH
                startActivity(i)

                return true


            }
        })


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fetchSuggestions(newText.toString())
                return false
            }
        })


        //Close searchView
        searchView.setOnCloseListener {
            hideKeyboard(this@MainActivity, this@MainActivity)
            searchView.clearFocus()
            searchView.isIconified = true

            false
        }

    }


    override fun onSearchRequested(): Boolean {

        val appData = Bundle().apply {
            putBoolean("JARGON", true)
        }
        startSearch(null, false, appData, false)
        return true
    }

    private fun showWalkthrough() {
    }

    private fun showNotification() {
    }

    private fun changeLanguage() {
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

    override fun goToAddStocks(fragment: Fragment) {


    }


}