package com.example.mandiexe.ui.myrequirements

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.OnItemClickListenerAddReq
import com.example.mandiexe.models.requirements.AddRequirementResponse
import com.example.mandiexe.viewmodels.AddRequirementViewModel


class AddRequirement : Fragment(), OnItemClickListenerAddReq {

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

                loadResultInRV(txt)


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

    }

    private fun fetchSuggestions(query: String) {

    }

    private fun loadResultInRV(txt: String?) {
        //MAke call
        pb.visibility = View.VISIBLE

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddRequirementViewModel::class.java)
    }

    override fun onItemClicked(_listItem: AddRequirementResponse) {
        //Open Card of the requirement
        val bundle = bundleOf(

        )


    }

}
