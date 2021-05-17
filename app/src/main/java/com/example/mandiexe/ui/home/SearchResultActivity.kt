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
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.layout_filter.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SearchResultActivity : AppCompatActivity(), OnYoutubeClickListener {

    private val pref = PreferenceUtil
    private lateinit var args: Bundle

    private val sessionManager = SessionManager(ApplicationUtils.getContext())

    private var crop = ""
    private var title = ""

    private val mHandler = Handler()

    private lateinit var pb: ProgressBar

    private var englishFinalQuery = ""
    private lateinit var actvDays: AutoCompleteTextView
    private lateinit var actvDistance: AutoCompleteTextView
    private lateinit var mtbFilter: MaterialButton


    private val TAG = SearchResultActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference(), this)
        this.setContentView(R.layout.activity_search_result)

        //Get arugmenent

        args = intent?.getBundleExtra("bundle")!!

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
        this.apply {
            tvTitleToolbar.text = title
        }
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
                getInformationNormalFilters()
            }
        }

    }

    private fun getInformationNormalFilters() {

        val kgLocale = resources.getString(R.string.kg)
        val d = AlertDialog.Builder(this)
        d.setTitle(resources.getString(R.string.howMuchGrown))
        d.setMessage(resources.getString(R.string.infoHowMuchGrown, title, kgLocale))
        d.setPositiveButton(resources.getString(R.string.ok)) { _, _ -> }
        d.create().show()
    }

    private fun setUpFilterSpinners() {

        //Null at first
        actvDays.setText(
            resources.getString(R.string.num50).toString(),
            TextView.BufferType.EDITABLE
        )
        actvDistance.setText(resources.getString(R.string.num50), TextView.BufferType.EDITABLE)


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

                        if (response.body() != null) {
                            loadAdvancedSearch(response.body()!!)
                        }
                    } else {
                        doEmptyStates()
                    }
                }

                override fun onFailure(call: Call<AdvancedSearchResponse>, t: Throwable) {
                    val message = ExternalUtils.returnStateMessageForThrowable(t)
                    doThrowableState()
                    UIUtils.logThrowables(t,TAG)
                    createSnackbar(message,this@SearchResultActivity,container_search)

                }
            })

        hideProgress(pb, this)

    }

    private fun loadAdvancedSearch(body: AdvancedSearchResponse) {

        Log.e(TAG, "In adv load")
        showProgress(pb, this)

        try {
            this.apply {
                cslAdvancedSearch.visibility = View.VISIBLE

                val kgLocale = resources.getString(R.string.kg)
                val kmLocale = resources.getString(R.string.kilometeres)

                val mCalendar = Calendar.getInstance();
                val myFormat = "dd/MMM/yyyy" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                var mDays = actvDays.text.toString()
                if (mDays.isEmpty()) {
                    mDays = defaultDaysAndDistance.toString()
                }

                var mDistance = actvDistance.text.toString()
                if (mDistance.isEmpty()) {
                    mDistance = defaultDaysAndDistance.toString()
                }

                val currentDayMark = mDays.toString()
                mCalendar.add(Calendar.DATE, currentDayMark.toInt())
                val afterDate = sdf.format(mCalendar.time)
                mCalendar.add(Calendar.DATE, -(2 * currentDayMark.toInt()))
                val beforeDate = sdf.format(mCalendar.time)


                //Load
                tvAdvData.text =
                    resources.getString(
                        R.string.quantity_grown_is,
                        body.info.qty.toString(),
                        kgLocale.toString(),
                        beforeDate.toString(),
                        afterDate.toString()
                    )

                tvAdvDistance.text =
                    resources.getString(R.string.within, mDistance, kmLocale)
            }
        } catch (e: Exception) {
            UIUtils.logExceptions(e, TAG)
            doEmptyStates()
        }

        Log.e(TAG, "Ending with search result adv daya as well")
        hideProgress(pb, this)

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

                        if (response.body() != null) {
                            loadItemsFunction(response.body()!!)
                        }
                    } else {
                        doEmptyStates()
                    }
                }

                override fun onFailure(call: Call<SearchGlobalCropResponse>, t: Throwable) {
                    val message = ExternalUtils.returnStateMessageForThrowable(t)
                    UIUtils.logThrowables(t,TAG)
                    createSnackbar(message,this@SearchResultActivity,container_search)
                    doThrowableState()


                }
            })

        hideProgress(pb, this)
    }

    private fun doEmptyStates() {
        this.apply {
            clVis.visibility = View.GONE
            llErrorSearch.visibility = View.VISIBLE
            llErrorThrowable.visibility = View.GONE
        }
    }

    private fun doThrowableState() {
        this.apply {
            clVis.visibility = View.GONE
            llErrorSearch.visibility = View.GONE
            llErrorThrowable.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {

        finish()
        super.onBackPressed()
    }

    private fun loadItemsFunction(response: SearchGlobalCropResponse) {

        showProgress(pb, this)
        try {
            this.apply {
                Log.e("SEARCH RES", "response " + response.toString())

                clVis.visibility = View.VISIBLE
                cslAdvancedSearch.visibility = View.GONE
                cslNormalSearchData.visibility = View.VISIBLE

                val kgLocale = resources.getString(R.string.kg)

                tvInCountry.text = resources.getString(
                    R.string.kgQuantityValueString,
                    response.country.total.toString(),
                    kgLocale
                )
                tvInState.text = resources.getString(
                    R.string.kgQuantityValueString,
                    response.state.total.toString(),
                    kgLocale
                )
                tvInVillage.text = resources.getString(
                    R.string.kgQuantityValueString,
                    response.village.qty.toString(),
                    kgLocale
                )
                tvInDistrict.text = resources.getString(
                    R.string.kgQuantityValueString,
                    response.district.total.toString(),
                    kgLocale
                )



                loadYoutubeLinks(response.links)
                //Load the advanced data
                val newBody = AdvancedSearchBody(
                    englishFinalQuery,
                    defaultDaysAndDistance.toInt(),
                    defaultDaysAndDistance.toInt()
                )
                makeAdvcall(newBody)

            }

        } catch (e: Exception) {

            Log.e("SEARCh", e.message + e.cause + " Error")
            doEmptyStates()
            //When we dont find anything
            //Show the error field


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
