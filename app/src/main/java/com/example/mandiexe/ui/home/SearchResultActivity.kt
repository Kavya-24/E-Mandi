package com.example.mandiexe.ui.home

import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.supply.SearchGlobalCropBody
import com.example.mandiexe.models.responses.supply.SearchGlobalCropResponse
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import retrofit2.Call
import retrofit2.Response

class SearchResultActivity : AppCompatActivity() {

    private val pref = PreferenceUtil
    private lateinit var args: Bundle

    private val sessionManager = SessionManager(this)

    private var crop = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference(), this)
        setContentView(R.layout.activity_search_result)

        //Get arugmenent

        args = intent?.getBundleExtra("bundle")!!
        crop = args.getString("crop").toString()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = crop

        searchCrops()

    }

    private fun searchCrops() {

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


    }

    private fun loadItemsFunction(response: SearchGlobalCropResponse) {

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
