package com.example.mandiexe.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.supply.SearchGlobalCropBody
import com.example.mandiexe.models.responses.supply.SearchGlobalCropResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import retrofit2.Call
import retrofit2.Response

class SearchResultActivity : AppCompatActivity() {

    private val pref = PreferenceUtil
    private lateinit var args: Bundle

    private val sessionManager = SessionManager(ApplicationUtils.getContext())

    private var crop = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference(), this)
        setContentView(R.layout.activity_search_result)

        //Get arugmenent

        args = intent?.getBundleExtra("bundle")!!
        crop = args.getString("crop").toString()

        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = crop
        tb.setNavigationOnClickListener {
            onBackPressed()
        }

        val aBar = findViewById<AppBarLayout>(R.id.appbarlayoutExternal)




        searchCrops()

        findViewById<ExtendedFloatingActionButton>(R.id.eFab_grow).setOnClickListener {
            addStock()
        }
    }

    private fun addStock() {
        val i = Intent(this, GrowActivity::class.java)
        startActivity(i)


    }

    private fun searchCrops() {

        Log.e("SEARCH RES", "Crop" + crop)

        findViewById<ProgressBar>(R.id.pb_searchCrop).visibility = View.VISIBLE
        val service = RetrofitClient.makeCallsForSupplies(this)
        val body = SearchGlobalCropBody(crop)

        service.getSearchCropGlobally(
            body,
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}",

            )
            .enqueue(object : retrofit2.Callback<SearchGlobalCropResponse> {
                override fun onResponse(
                    call: Call<SearchGlobalCropResponse>,
                    response: Response<SearchGlobalCropResponse>
                ) {
                    if (response.isSuccessful) {

                        response.body()?.let { loadItemsFunction(it) }

                    }
                }

                override fun onFailure(call: Call<SearchGlobalCropResponse>, t: Throwable) {
                    val message = ExternalUtils.returnStateMessageForThrowable(t)
                }
            })

        findViewById<ProgressBar>(R.id.pb_searchCrop).visibility = View.GONE
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun loadItemsFunction(response: SearchGlobalCropResponse) {

        findViewById<ConstraintLayout>(R.id.clVis).visibility = View.VISIBLE
        Log.e("SEARCH RES", "response " + response.toString())

        findViewById<TextView>(R.id.tvInCountry).text = response.country.total.toString()
        findViewById<TextView>(R.id.tvInState).text = response.state.total.toString()
        findViewById<TextView>(R.id.tvInVillage).text = response.village.qty.toString()
        findViewById<TextView>(R.id.tvInDistrict).text = response.district.total.toString()

        loadYoutubeLinks(response.links)

    }

    private fun loadYoutubeLinks(links: List<SearchGlobalCropResponse.Link>) {

        val rv = findViewById<RecyclerView>(R.id.rv_youtubeLinks)
        rv.layoutManager = LinearLayoutManager(this)

    }
}
