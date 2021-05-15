package com.example.mandiexe.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.OnYoutubeClickListener
import com.example.mandiexe.adapter.YoutubeAdapter
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.AdvancedSearchBody
import com.example.mandiexe.models.body.supply.SearchGlobalCropBody
import com.example.mandiexe.models.responses.AdvancedSearchResponse
import com.example.mandiexe.models.responses.supply.SearchGlobalCropResponse
import com.example.mandiexe.ui.supply.AddStock
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.Constants.defaultDaysAndDistance
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.layout_filter.*
import retrofit2.Call
import retrofit2.Response

class SearchResultActivity : AppCompatActivity(), OnYoutubeClickListener {

    private val pref = PreferenceUtil
    private lateinit var args: Bundle

    private val sessionManager = SessionManager(ApplicationUtils.getContext())

    private var crop = ""
    private var title = ""

    private val mHandler = Handler()

    private lateinit var mTv: TextView
    private lateinit var pb: ProgressBar

    private var englishFinalQuery = ""
    private lateinit var actvDays: AutoCompleteTextView
    private lateinit var actvDistance: AutoCompleteTextView
    private lateinit var mtbFilter: MaterialButton
    private var isFiltered = false

    private val TAG = SearchResultActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference(), this)
        this.setContentView(R.layout.activity_search_result)

        //Get arugmenent

        args = intent?.getBundleExtra("bundle")!!

        mTv = findViewById(R.id.tvAddMissing)
        //Add the side drawable
        mTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_black_24dp, 0, 0, 0)
        //Process the crop here
        this.apply {
            actvDays = actv_days
            actvDistance = actv_distance
            mtbFilter = mtb_filter
        }

        crop = args.getString("crop")!!
        title = args.getString("title").toString()

        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = title
        tb.setNavigationOnClickListener {
            onBackPressed()
        }

        pb = findViewById(R.id.pb_searchCrop)

        val aBar = findViewById<AppBarLayout>(R.id.appbarlayoutExternal)

        showProgress(pb, this)

        val t = findViewById<TextView>(R.id.tempTv)

        if (crop != "e-mandi-farmer-null-query") {
            //This is the case, when we have clicked on a crop suggestion and which has been translated and pulled in with crop arg
            //The locale query is in title variable
            englishFinalQuery = crop
            searchCrops(crop)
        } else {
            //This is when we have
            //This is the case when we have directly submitted
            OfflineTranslate.translateToEnglish(this, crop, t)
            if (t.text != resources.getString(R.string.noDesc)) {
                searchCrops(t.text.toString())
            } else {
                //Wait for 2 seconds
                mHandler.postDelayed({ searchCrops(t.text.toString()) }, 2000)
            }

        }


        hideProgress(pb, this)

        findViewById<ExtendedFloatingActionButton>(R.id.eFab_grow).setOnClickListener {
            addStock()
        }

        mTv.setOnClickListener {
            addStock()
        }


        /*
        For the filter layout
         */
        setUpFilterSpinners()
        mtbFilter.setOnClickListener {

            //When no filter is selected
            if (actvDays.text.isNullOrEmpty() and actvDistance.text.isNullOrEmpty()) {
                //Drop down the days spinner
                actvDays.showDropDown()

            } else {
                doAdvancedSearch()

            }
        }

        this.apply {

            ivInformation.setOnClickListener {
                if (isFiltered) {
                    //The circular map information
                    getInformationAboutFilteredData()
                } else {
                    //The information of what is happening and informations about filters
                    getInformationNormalFilters()
                }
            }
        }

    }

    private fun getInformationAboutFilteredData() {

    }

    private fun getInformationNormalFilters() {

        val d = AlertDialog.Builder(this)
        d.setTitle(resources.getString(R.string.howMuchGrown))
        d.setMessage(resources.getString(R.string.infoHowMuchGrown, title))
        d.setPositiveButton(resources.getString(R.string.ok)) { _, _ -> }
        d.create().show()
    }

    private fun setUpFilterSpinners() {

        UIUtils.getSpinnerAdapter(R.array.arr_days_limit, actvDays, this)
        UIUtils.getSpinnerAdapter(R.array.arr_distance_limit, actvDistance, this)

    }

    private fun doAdvancedSearch() {
        val numDays = actvDays.text.toString()
        val numDistance = actvDistance.text.toString()

        //Default
        var newBody =
            AdvancedSearchBody(englishFinalQuery, defaultDaysAndDistance, defaultDaysAndDistance)

        if (numDays.isEmpty()) {
            newBody =
                AdvancedSearchBody(englishFinalQuery, defaultDaysAndDistance, numDistance.toInt())
        } else if (numDistance.isEmpty()) {
            newBody =
                AdvancedSearchBody(englishFinalQuery, numDays.toInt(), defaultDaysAndDistance)
        } else {
            newBody = AdvancedSearchBody(englishFinalQuery, numDays.toInt(), numDistance.toInt())
        }

        makeAdvcall(newBody)

    }

    private fun makeAdvcall(newBody: AdvancedSearchBody) {

        Log.e(
            "SEARCH",
            "Crop" + englishFinalQuery + "Advanced search body is " + newBody.toString()
        )

        val service = RetrofitClient.makeCallsForSupplies(this)

        showProgress(pb, this)

        service.getAdvancedSearch(
            newBody
        )
            .enqueue(object : retrofit2.Callback<AdvancedSearchResponse> {
                override fun onResponse(
                    call: Call<AdvancedSearchResponse>,
                    response: Response<AdvancedSearchResponse>
                ) {
                    if (response.isSuccessful) {

                        isFiltered = true
                        if (response.body() != null) {
                            loadAdvancedSearch(response.body()!!)
                        }
                    }
                }

                override fun onFailure(call: Call<AdvancedSearchResponse>, t: Throwable) {
                    val message = ExternalUtils.returnStateMessageForThrowable(t)
                    UIUtils.logThrowables(t, TAG)
                }
            })

        hideProgress(pb, this)

    }

    private fun loadAdvancedSearch(body: AdvancedSearchResponse) {

    }

    private fun addStock() {

        val i = Intent(this, AddStock::class.java)
        startActivity(i)

    }

    private fun searchCrops(englishQueryTranslated: String) {

        Log.e("SEARCH RES", "Crop" + crop + " and from translated = " + englishQueryTranslated)

        val service = RetrofitClient.makeCallsForSupplies(this)
        val body = SearchGlobalCropBody(englishQueryTranslated)

        service.getSearchCropGlobally(
            body,
        )
            .enqueue(object : retrofit2.Callback<SearchGlobalCropResponse> {
                override fun onResponse(
                    call: Call<SearchGlobalCropResponse>,
                    response: Response<SearchGlobalCropResponse>
                ) {
                    if (response.isSuccessful) {

                        isFiltered = false
                        if (response.body() != null) {
                            loadItemsFunction(response.body()!!)
                        }
                    }
                }

                override fun onFailure(call: Call<SearchGlobalCropResponse>, t: Throwable) {
                    val message = ExternalUtils.returnStateMessageForThrowable(t)
                }
            })

        hideProgress(pb, this)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun loadItemsFunction(response: SearchGlobalCropResponse) {

        try {
            findViewById<ConstraintLayout>(R.id.clVis).visibility = View.VISIBLE
            Log.e("SEARCH RES", "response " + response.toString())
            findViewById<TextView>(R.id.tvInCountry).text = response.country.total.toString()
            findViewById<TextView>(R.id.tvInState).text = response.state.total.toString()
            findViewById<TextView>(R.id.tvInVillage).text = response.village.qty.toString()
            findViewById<TextView>(R.id.tvInDistrict).text = response.district.total.toString()

            loadYoutubeLinks(response.links)
        } catch (e: Exception) {
            findViewById<ConstraintLayout>(R.id.clVis).visibility = View.INVISIBLE
            Log.e("SEARCh", e.message + e.cause + " Error")
            createSnackbar(resources.getString(R.string.unableToGetSearch), this, container_search)

            mTv.visibility = View.VISIBLE


        }
        hideProgress(pb, this)

    }


    private fun loadYoutubeLinks(links: List<SearchGlobalCropResponse.Link>) {

        val rv = findViewById<RecyclerView>(R.id.rv_youtubeLinks)
        rv.layoutManager = LinearLayoutManager(this)

        val adapter = YoutubeAdapter(this)
        adapter.lst = links
        rv.adapter = adapter


    }
}
