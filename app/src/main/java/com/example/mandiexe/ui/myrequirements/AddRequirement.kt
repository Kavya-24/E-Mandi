package com.example.mandiexe.ui.myrequirements

import android.content.Intent
import android.database.MatrixCursor
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.NewReqAdapter
import com.example.mandiexe.adapter.OnNewReqClockListener
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.SearchCropReqBody
import com.example.mandiexe.models.body.supply.CropSearchAutoCompleteBody
import com.example.mandiexe.models.responses.SearchCropReqResponse
import com.example.mandiexe.models.responses.supply.CropSearchAutocompleteResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.DefaultListOfCrops
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.viewmodels.AddRequirementViewModel
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.SearchAdapter
import kotlinx.android.synthetic.main.add_requirement_fragment.*
import retrofit2.Call
import retrofit2.Response


class AddRequirement : AppCompatActivity(), OnNewReqClockListener {

    companion object {
        fun newInstance() = AddRequirement()
    }

    private lateinit var viewModel: AddRequirementViewModel

    private lateinit var pb: ProgressBar
    private lateinit var rv: RecyclerView

    private val sessionManager = SessionManager(ApplicationUtils.getContext())
    private val pref = PreferenceUtil
    val VOICE_REC_CODE = 1234
    private val ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION"

    private val TAG = AddRequirement::class.java.simpleName

    private lateinit var search_view_req: MaterialSearchView
    private lateinit var mListOfSuggestions: MutableList<String>

    private fun makeCall(txt: String?) {

        val service = RetrofitClient.makeCallsForBids(this)
        val body = SearchCropReqBody(txt.toString())


        service.getSearchReq(
            body,
        )
            .enqueue(object : retrofit2.Callback<SearchCropReqResponse> {
                override fun onResponse(
                    call: Call<SearchCropReqResponse>,
                    response: Response<SearchCropReqResponse>
                ) {
                    if (response.isSuccessful) {

                        response.body()?.let { loadResultInRV(it) }

                    } else {
                        createSnackbar(
                            resources.getString(R.string.NoLoadNewReq),
                            this@AddRequirement,
                            conatiner_add_req
                        )
                    }
                }

                override fun onFailure(call: Call<SearchCropReqResponse>, t: Throwable) {
                    val message = ExternalUtils.returnStateMessageForThrowable(t)
                    Log.e("Fail", "Failed to load req" + t.cause + t.message)
                }
            })


    }

    private fun fetchSuggestions(query: String) {

        val service = RetrofitClient.makeCallsForSupplies(this)

        val body = CropSearchAutoCompleteBody(query)
        service.getCropAutoComplete(
            body = body,
        ).enqueue(object : retrofit2.Callback<CropSearchAutocompleteResponse> {
            override fun onFailure(call: Call<CropSearchAutocompleteResponse>, t: Throwable) {

                val message = ExternalUtils.returnStateMessageForThrowable(t)

            }

            override fun onResponse(
                call: Call<CropSearchAutocompleteResponse>,
                response: Response<CropSearchAutocompleteResponse>
            ) {

                if (response.isSuccessful) {
                    val strAr = mutableListOf<String>()
                    for (y: CropSearchAutocompleteResponse.Suggestion in response.body()!!.suggestions) {
                        strAr.add(y.name)
                    }

                    Log.e("STr", strAr.toString())
                    Log.e("Str", strAr.size.toString())


                    val c =
                        MatrixCursor(arrayOf(BaseColumns._ID, "suggestionList"))
                    for (i in 0 until strAr.size) {
                        c.addRow(arrayOf(i, strAr[i]))
                    }


//                    mAdapter?.changeCursor(c)
                }

            }
        })


    }

    private fun loadResultInRV(response: SearchCropReqResponse) {
        //MAke call

        if (response.supplies.isEmpty()) {
            val tv = findViewById<TextView>(R.id.tvNoNewReq)
            tv.visibility = View.VISIBLE

        } else {

            pb.visibility = View.VISIBLE
            rv.layoutManager = LinearLayoutManager(this)
            val adapter = NewReqAdapter(this)
            adapter.lst = response.supplies
            rv.adapter = adapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(pref.getLanguageFromPreference(), this)
        setContentView(R.layout.add_requirement_fragment)
        super.onCreate(savedInstanceState)

        //UI init
        pb = findViewById(R.id.pb_add_req)
        rv = findViewById(R.id.rv_search_requirements)


        val tb = findViewById<Toolbar>(R.id.toolbarAddReq)
        tb.title = resources.getString(R.string.searchBuyers)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }

        //Inflate menu
        tb.inflateMenu(R.menu.search_requirement_menu)


        search_view_req = findViewById(R.id.search_view_req)

        mListOfSuggestions = mutableListOf()

        // search_view_req.setCursorDrawable(R.drawable.ic_call_made_black_24dp)

        val crops: Array<String> = resources.getStringArray(R.array.arr_crop_names)
        search_view_req.setAdapter(SearchAdapter(this@AddRequirement, crops))


        //Get tge liust view
        val mListView =
            search_view_req.findViewById<ListView>(com.miguelcatalan.materialsearchview.R.id.suggestion_list)!!

        //My SearchView
        search_view_req.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Do some magic
                if (!query.isNullOrEmpty()) {
                    Log.e("MAIN", "In on query submit")

                    searchCrop(
                        query ?: resources.getString(R.string.rice),
                        "e-mandi-farmer-null-query"
                    )

                }
                return false

            }


            override fun onQueryTextChange(newText: String?): Boolean {

                //Do some magic
                // fetchSuggestions(newText ?: resources.getString(R.string.rice))
                //search_view_req.setSuggestions(mListOfSuggestions.toTypedArray())
                //The adapter is fixed
                Log.e("MAIN", "On query change")

                return false
            }
        })

        search_view_req.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onSearchViewShown() {
                Log.e("MAIN", "In onSearchViewShown()")
                //Set suggestions
                search_view_req.showSuggestions()

                mListView.visibility = View.VISIBLE


                //Do some magic
            }

            override fun onSearchViewClosed() {
                Log.e("MAIN", "In onSearchViewClosed")
                search_view_req.dismissSuggestions()
                search_view_req.clearFocus()
                return
                //Do some magic
            }
        })

        search_view_req.setVoiceSearch(true)
        search_view_req.showVoice(true)


        val v =
            search_view_req.findViewById<ImageView>(com.miguelcatalan.materialsearchview.R.id.action_voice_btn)!!
        v.setOnClickListener {
            createVoiceIntent()
        }


        search_view_req.setOnItemClickListener { parent, view, position, id ->


            val q = parent.getItemAtPosition(position).toString()

            //Get the index, and then find its equiavlied englihs
            val mIndex = crops.indexOf(q)


            val englishQuery = DefaultListOfCrops.getTheDefaultCropsList().get(mIndex)
            Log.e(
                "MAIN",
                "In item clicked nmIndex is $mIndex  and the real valur is $englishQuery\n" + parent.getItemAtPosition(
                    position
                ).toString()
            )

            //Set the english query in the local history
            pref.setHistorySet(englishQuery)

            search_view_req.dismissSuggestions()
            search_view_req.clearFocus()
            search_view_req.setQuery("", false)

            searchCrop(q, englishQuery)
            search_view_req.visibility = View.GONE
        }


    }

    private fun createVoiceIntent() {
        val mLanguage = pref.getLanguageFromPreference() ?: "en"
        val Voiceintent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        //Put language
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            mLanguage
        )

        Voiceintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, mLanguage)
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
            mLanguage
        )

        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            resources.getString(R.string.searchHead)
        )
        startActivityForResult(Voiceintent, VOICE_REC_CODE)


    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VOICE_REC_CODE) {

            Log.e("IN ARC", "")
            if (data != null) {
                //Put result
                val res: java.util.ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val resultInDefault = res?.get(0)
                search_view_req.setQuery(resultInDefault, false)
            }

        }


    }

    private fun searchCrop(defaultQuery: String, englishQuery: String) {
        makeCall(englishQuery)

    }

    //Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search_requirement_menu, menu)
        Log.e("ADD", "In on create menu")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.e("MAIN", "In onSelected")
        when (item.itemId) {

            R.id.action_search_requirements -> {
                search_view_req.showSearch()
                search_view_req.visibility = View.VISIBLE
            }


        }

        return super.onOptionsItemSelected(item)
    }

    override fun viewAddReqDetails(_listItem: SearchCropReqResponse.Supply) {

        val mFrom = AddRequirement::class.java.simpleName
        val bundle = bundleOf(
            "BID_ID" to _listItem._id,
            "FROM" to mFrom

        )

        val i = Intent(this, OpenNewRequirementFragment::class.java)
        i.putExtra("bundle", bundle)
        startActivity(i)

    }

}

