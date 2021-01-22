package com.example.mandiexe.ui.myrequirements

import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.*
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
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.viewmodels.AddRequirementViewModel
import kotlinx.android.synthetic.main.add_requirement_fragment.*
import retrofit2.Call
import retrofit2.Response
import java.util.*


class AddRequirement : AppCompatActivity(), OnNewReqClockListener {

    companion object {
        fun newInstance() = AddRequirement()
    }

    private lateinit var viewModel: AddRequirementViewModel
    //private lateinit var root: View

    private lateinit var searchView: SearchView
    private var mAdapter: SimpleCursorAdapter? = null
    private lateinit var searchManager: SearchManager

    private lateinit var pb: ProgressBar
    private lateinit var rv: RecyclerView

    private val sessionManager = SessionManager(ApplicationUtils.getContext())
    private val pref = PreferenceUtil
    val VOICE_REC_CODE = 1234
    private val ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION"

    private val TAG = AddRequirement::class.java.simpleName


    private fun searchRequirements() {

        val from = arrayOf("suggestionList")
        val to = intArrayOf(android.R.id.text1)

        //Add a searchManager


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
        searchView.onActionViewExpanded()
        searchView.clearFocus()

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false        //Was true
            }

            override fun onSuggestionClick(position: Int): Boolean {

                val cursor: Cursor = mAdapter!!.getItem(position) as Cursor
                val txt = cursor.getString(cursor.getColumnIndex("suggestionList"))

                makeCall(txt)

                return true


            }
        })


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Returns query
                //Do nothing here

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fetchSuggestions(newText.toString())
                return false
            }
        })


        val Voiceintent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        //Put language
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale(pref.getLanguageFromPreference() ?: "en")
        )
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            resources.getString(R.string.searchHead)
        )
        startActivityForResult(Voiceintent, VOICE_REC_CODE)


    }


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


                    mAdapter?.changeCursor(c)
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

        pb.visibility = View.VISIBLE

        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = resources.getString(R.string.searchBuyers)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }

        searchView = findViewById(R.id.sv_requirements)

        searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnClickListener {
            searchRequirements()
        }


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
