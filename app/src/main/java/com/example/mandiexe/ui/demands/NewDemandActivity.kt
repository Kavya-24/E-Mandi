package com.example.mandiexe.ui.demands

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mandiexe.R
import com.example.mandiexe.adapter.NewReqAdapter
import com.example.mandiexe.adapter.OnClickNewRequirement
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.NewDemandSearchBody
import com.example.mandiexe.models.body.supply.CropSearchAutoCompleteBody
import com.example.mandiexe.models.responses.demand.NewDemandsResponse
import com.example.mandiexe.models.responses.supply.CropSearchAutocompleteResponse
import com.example.mandiexe.ui.bids.BidDetailActivity
import com.example.mandiexe.ui.supply.AddStock
import com.example.mandiexe.utils.DefaultListOfCrops
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.NewDemandViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_new_demand.*
import retrofit2.Call
import retrofit2.Response


class NewDemandActivity : AppCompatActivity(), OnClickNewRequirement {

    companion object {
        fun newInstance() = NewDemandActivity()
    }

    private val pref = PreferenceUtil
    private lateinit var handler: Handler
    private lateinit var tv: TextView
    private lateinit var pb: ProgressBar
    private lateinit var rvItems: RecyclerView

    private lateinit var searchView: SearchView
    private var mAdapter: SimpleCursorAdapter? = null
    private lateinit var searchManager: SearchManager

    private val VOICE_REC_CODE = 1249
    private val TAG = NewDemandActivity::class.java.simpleName

    private val viewModel: NewDemandViewModel by viewModels()
    private lateinit var t: TextView

    private lateinit var mSnackbarView: ConstraintLayout

    private lateinit var swl: SwipeRefreshLayout

    private lateinit var snack: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(pref.getLanguageFromPreference(), this)
        setContentView(R.layout.activity_new_demand)
        super.onCreate(savedInstanceState)

        //UI init
        handler = Handler()

        this.apply {
            tv = tempSearchSupplyName
            pb = pb_search_supply
            searchView = sv_crops
            t = tempSearchSupplyName
            mSnackbarView = container_search_supply
            swl = swl_supply_search

        }

        snack = Snackbar.make(
            container_search_supply,
            resources.getString(R.string.noDemandsFoundCrop, searchView.query.toString()),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(resources.getString(R.string.add_crop)) { mListener ->
            val i = Intent(this@NewDemandActivity, AddStock::class.java)
            startActivity(i)
        }


        val tb = findViewById<Toolbar>(R.id.toolbarExternalSearch)
        tb.title = resources.getString(R.string.searchCrops)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }




        searchView.queryHint = resources.getString(R.string.searchCrops)
        searchView.requestFocus()

        searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        val from = arrayOf("suggestionList")
        val to = intArrayOf(android.R.id.text1)
        val crops: Array<String> = resources.getStringArray(R.array.arr_crop_names)


        //Add a searchManager


        mAdapter = SimpleCursorAdapter(
            this,
            R.layout.text_file_suggestion,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        searchView.suggestionsAdapter = mAdapter
        searchView.isIconifiedByDefault = false
        searchView.onActionViewExpanded()


        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {

            override fun onSuggestionSelect(position: Int): Boolean {
                return false        //Was true
            }

            override fun onSuggestionClick(position: Int): Boolean {


                searchView.clearFocus()
                //Wont come here
                showProgress(pb, this@NewDemandActivity)
                val cursor: Cursor = mAdapter!!.getItem(position) as Cursor
                val txt = cursor.getString(cursor.getColumnIndex("suggestionList"))
                searchView.setQuery(txt, false)
                //Get the index, and then find its equiavlied englihs
                //Not necessary that the English Query exists
                try {

                    val mIndex = crops.indexOf(txt)
                    val englishQuery = DefaultListOfCrops.getTheDefaultCropsList().get(mIndex)
                    Log.e(
                        TAG,
                        "In item clicked nmIndex is $mIndex  and the english query is $englishQuery\n"
                    )

                    //Set the english query in the local history
                    pref.setHistorySet(englishQuery)


                    makeCall(englishQuery, txt)
                    //    hideProgress(pb, this@NewDemandActivity)
                    return true

                } catch (e: java.lang.Exception) {
                    Log.e(
                        TAG,
                        "Exception in searching ${e.cause} and ${e.message}, do the traditional way translate"
                    )
                    getTranslatedSearch(txt)

                }



                return true

            }
        })


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Returns query
                //Do nothing here
                if (query != null) {
                    searchView.clearFocus()
                    getTranslatedSearch(query.toString())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

//                showProgress(pb, this@NewDemandActivity)
                if (newText != null) {
                    getTranslatedQuery(newText.toString())
//                hideProgress(pb, this@NewDemandActivity)
                }

                return false

            }
        })


//        //For the voice,
//        val voiceId = resources.getIdentifier("android:id/search_voice_btn", null, null)
//        val voiceImage = searchView.findViewById<View>(voiceId)
//
//        voiceImage.visibility = View.VISIBLE
//        voiceImage.setOnClickListener {
//            createVoiceIntent()
//        }
//
//
//        val searchPlateId =
//            searchView.context.resources.getIdentifier("android:id/search_plate", null, null)
//        val searchPlate = searchView.findViewById<View>(searchPlateId)
//        searchPlate.setBackgroundResource(R.drawable.searchview_selector)
//
//
//        //Hide Magnification glass
//        val magId = resources.getIdentifier("android:id/search_mag_icon", null, null)
//        val magImage = searchView.findViewById<View>(magId) as ImageView
//        magImage.layoutParams = LinearLayout.LayoutParams(0, 0)
//        magImage.visibility = View.GONE
//

        //For the voice,
        val voiceId = resources.getIdentifier("android:id/search_voice_btn", null, null)
        val voiceImage = searchView.findViewById<View>(voiceId) as ImageView
        voiceImage.visibility = View.VISIBLE
        voiceImage.setOnClickListener {
            createVoiceIntent()
        }


        //Swl
        swl.setOnRefreshListener {

            val tQuery = searchView.query
            if (!tQuery.isNullOrEmpty()) {
                getTranslatedSearch(tQuery.toString())

            }
            swl.isRefreshing = false
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

            Log.e(TAG, "In voice")
            if (data != null) {
                //Put result
                val res: java.util.ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val resultInDefault = res?.get(0)
                searchView.setQuery(resultInDefault, false)

                pb.visibility = View.VISIBLE
                getTranslatedSearch(resultInDefault.toString())
                hideProgress(pb, this)
            }

        }


    }


    private fun makeCall(txt: String?, defaultQuery: String) {

        val service = RetrofitClient.makeCallsForBids(this)
        val body = NewDemandSearchBody(txt.toString())

        Log.e(TAG, "In search of make call for txt $txt and the default query is $defaultQuery")

        pb.visibility = View.VISIBLE
        showProgress(pb, this)

        service.getSearchReq(
            body,
        )
            .enqueue(object : retrofit2.Callback<NewDemandsResponse> {
                override fun onResponse(
                    call: Call<NewDemandsResponse>,
                    response: Response<NewDemandsResponse>
                ) {
                    Log.e(TAG, "The response is " + response.body().toString())
                    //hIDE THE RV SUGGESTIONS
                    if (response.isSuccessful) {

                        response.body()?.let { loadResultInRV(it) }

                    }

                    hideProgress(pb, this@NewDemandActivity)

                }

                override fun onFailure(call: Call<NewDemandsResponse>, t: Throwable) {
                    val message = ExternalUtils.returnStateMessageForThrowable(t)
                    Log.e(TAG, "Failed to load req" + t.cause + t.message)
                    createSnackbar(
                        message,
                        this@NewDemandActivity,
                        container_search_supply
                    )

                    hideProgress(pb, this@NewDemandActivity)


                }


            })


    }

    private fun fetchSuggestions(query: String) {

        val service = RetrofitClient.makeCallsForSupplies(this)

        Log.e(TAG, "In fetch suggestion for query q $query ")

        val body = CropSearchAutoCompleteBody(query)
        showProgress(pb, this)
        service.getCropAutoComplete(
            body = body,
        ).enqueue(object : retrofit2.Callback<CropSearchAutocompleteResponse> {
            override fun onFailure(call: Call<CropSearchAutocompleteResponse>, t: Throwable) {

                val message = ExternalUtils.returnStateMessageForThrowable(t)
                hideProgress(pb, this@NewDemandActivity)
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

                    Log.e("Str", strAr.toString())
                    Log.e("Str", strAr.size.toString())

                    val tAr: MutableList<String> = getTranslatedSuggestionsList(strAr)
                    val c =
                        MatrixCursor(arrayOf(BaseColumns._ID, "suggestionList"))
                    for (i in 0 until strAr.size) {
                        c.addRow(arrayOf(i, tAr[i]))
                    }


                    mAdapter?.changeCursor(c)
                }
                hideProgress(pb, this@NewDemandActivity)
            }
        })

        //Empty the translate TV
        t.text = resources.getString(R.string.temp)


    }

    private fun getTranslatedSuggestionsList(strAr: MutableList<String>): MutableList<String> {

        val tAr = mutableListOf<String>()
        val suggestionHandler = Handler()

        for (i in strAr) {
            t.text = resources.getString(R.string.temp)
            OfflineTranslate.translateToDefault(this, i, t)
            if (getValidTranslations()) {
                tAr.add(t.text.toString())
                continue
            } else {
                //Wait for two seconds
                suggestionHandler.postDelayed({
                    tAr.add((t.text.toString()))
                }, 2000)
            }
        }


        t.text = resources.getString(R.string.temp)
        return tAr

    }

    private fun getTranslatedQuery(query: String) {

        OfflineTranslate.translateToEnglish(this, query, t)

        val autocompleteHandler = Handler()
        if (getValidTranslations()) {
            fetchSuggestions(t.text.toString())
        } else {
            //Wait for some time
            autocompleteHandler.postDelayed({
                fetchSuggestions(t.text.toString())

            }, 3000)


        }

        t.text = resources.getString(R.string.temp)

    }

    private fun getTranslatedSearch(query: String) {

        OfflineTranslate.translateToEnglish(this, query, t)

        val searchHandler = Handler()
        if (getValidTranslations()) {
            makeCall(t.text.toString(), query)
        } else {
            //Wait for some time
            searchHandler.postDelayed({
                makeCall(t.text.toString(), query)

            }, 3000)

        }

        t.text = resources.getString(R.string.temp)

    }

    private fun getValidTranslations(): Boolean {

        return ValidationObject.validateTranslations(
            findViewById<TextView>(R.id.tempSearchSupplyName),
            this
        )

    }

    private fun loadResultInRV(response: NewDemandsResponse) {
        //MAke call

        val adapter = NewReqAdapter(this)
        rvItems = findViewById(R.id.rv_searh_demand)
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = adapter

        if (response.demands.isEmpty()) {


            this.apply {

                ivNoSearchSupply.visibility = View.VISIBLE
                ivNoSearchSupply.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.nothingimg,
                        null
                    )
                )
                rvItems.visibility = View.GONE
                adapter.notifyDataSetChanged()


                snack.show()


            }


        } else {
            snack.dismiss()
            try {

                this.apply {
                    ivNoSearchSupply.visibility = View.GONE
                    rvItems.visibility = View.VISIBLE
                    adapter.lst = response.demands
                    adapter.notifyDataSetChanged()

                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception ${e.cause} ${e.message}")
            }
        }


    }

    override fun viewMyBidDetails(_listItem: NewDemandsResponse.Demand) {
        val mFrom = NewDemandActivity::class.java.simpleName
        val bundle = bundleOf(
            "BID_ID" to _listItem._id,
            "FROM" to mFrom

        )
        val i = Intent(this, BidDetailActivity::class.java)
        i.putExtra("bundle", bundle)
        startActivity(i)
    }

    override fun onResume() {
        getTranslatedSearch(searchView.query.toString())
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        searchView.clearFocus()
    }


}

