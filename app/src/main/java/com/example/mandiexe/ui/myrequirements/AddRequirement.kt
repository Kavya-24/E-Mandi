package com.example.mandiexe.ui.myrequirements

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
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
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.viewmodels.AddRequirementViewModel
import retrofit2.Call
import retrofit2.Response
import java.util.*


class AddRequirement : Fragment(), OnNewReqClockListener {

    companion object {
        fun newInstance() = AddRequirement()
    }

    private lateinit var viewModel: AddRequirementViewModel
    private lateinit var root: View

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.add_requirement_fragment, container, false)

        //UI init
        pb = root.findViewById(R.id.pb_add_req)
        rv = root.findViewById(R.id.rv_search_requirements)
        searchView = root.findViewById(R.id.sv_requirements)

        searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

        searchView.setOnClickListener {
            searchRequirements()
        }



        return root
    }


    private fun searchRequirements() {

        val from = arrayOf("suggestionList")
        val to = intArrayOf(android.R.id.text1)

        //Add a searchManager


        mAdapter = SimpleCursorAdapter(
            context,
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

        val service = RetrofitClient.makeCallsForBids(requireContext())
        val body = SearchCropReqBody(txt.toString())


        service.getSearchReq(
            body,
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}",

            )
            .enqueue(object : retrofit2.Callback<SearchCropReqResponse> {
                override fun onResponse(
                    call: Call<SearchCropReqResponse>,
                    response: Response<SearchCropReqResponse>
                ) {
                    if (response.isSuccessful) {

                        response.body()?.let { loadResultInRV(it) }

                    }
                }

                override fun onFailure(call: Call<SearchCropReqResponse>, t: Throwable) {
                    val message = ExternalUtils.returnStateMessageForThrowable(t)
                }
            })


    }

    private fun fetchSuggestions(query: String) {
        //This is not yet made
        val service = RetrofitClient.makeCallsForSupplies(requireContext())

        val body = CropSearchAutoCompleteBody(query)
        service.getCropAutoComplete(
            body = body,
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}",

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
        pb.visibility = View.VISIBLE
        rv.layoutManager = LinearLayoutManager(context)
        val adapter = NewReqAdapter(this)
        adapter.lst = response.supplies
        rv.adapter = adapter

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddRequirementViewModel::class.java)
    }


    override fun viewAddReqDetails(_listItem: SearchCropReqResponse.Supply) {

        val bundle = bundleOf(
            "BID_ID" to _listItem._id

        )

        root.findNavController()
            .navigate(R.id.action_addRequirement_to_openNewRequirementFragment, bundle)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VOICE_REC_CODE) {
            if (data != null) {
                //Put result
                val res: java.util.ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                searchView.setQuery(res?.get((0)), false)

            }
        }

    }

}
