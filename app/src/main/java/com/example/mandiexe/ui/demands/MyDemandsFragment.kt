package com.example.mandiexe.ui.demands

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MyRequirementsAdapter
import com.example.mandiexe.adapter.OnMyBidClickListener
import com.example.mandiexe.models.responses.bids.FamerBidsResponse
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.viewmodels.RequirementsViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.my_demands_fragment.*

class MyDemandsFragment : Fragment(), OnMyBidClickListener {

    companion object {
        fun newInstance() = MyDemandsFragment()
    }


    private val viewModel: RequirementsViewmodel by viewModels()
    private lateinit var root: View
    private lateinit var tabLayout: TabLayout

    private lateinit var pb : ProgressBar

    override fun onResume() {
        super.onResume()
        Log.e("Req", "In resume")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.my_demands_fragment, container, false)
        pb = root.findViewById(R.id.pb_requirement)
        //Get the items from retrofit call and paged adapter



        loadRequirements()

        val swl = root.findViewById<SwipeRefreshLayout>(R.id.swl_stock_fragment)
        swl.setOnRefreshListener {
            loadRequirements()
            swl.isRefreshing = false
        }

        root.findViewById<FloatingActionButton>(R.id.fab_add_requirement).setOnClickListener {
            val i = Intent(requireContext(), AddRequirement::class.java)
            startActivity(i)
        }

        return root
    }

    private fun loadRequirements() {

        showProgress(pb, requireContext())

        viewModel.reqFunction().observe(viewLifecycleOwner, Observer { mResponse ->

            //Check with the successful of it
            if (viewModel.successful.value != null) {
                if (viewModel.successful.value == false) {
                    UIUtils.createSnackbar(
                        viewModel.message.value,
                        requireContext(),
                        container_req
                    )
                } else {
                    manageReqLoadedResponses(mResponse)
                }
            }
        })

       hideProgress(pb, requireContext())

    }

    private fun manageReqLoadedResponses(mResponse: FamerBidsResponse?) {
        //Create rv

        val rv = root.findViewById<RecyclerView>(R.id.rv_requirement)
        val adapter = MyRequirementsAdapter(this)

        val mTV = root.findViewById<AppCompatTextView>(R.id.tvEmptyList)
        Log.e("REQ", "Framer Bid Response $mResponse" )
        if (mResponse != null) {
            if (mResponse.bids.isEmpty()) {
                mTV.visibility = View.VISIBLE
                mTV.text =
                    context?.resources?.getString(R.string.noDemand)

            } else {

                val mutableDemands = mutableListOf<FamerBidsResponse.Bid>()
                for(i in mResponse.bids){
                        mutableDemands.add(i)

                }
                adapter.lst = mutableDemands
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = adapter
            }
        }

    }

    override fun onDestroy() {
        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null
        Log.e("In requirements", "In destroy")
        super.onDestroy()

    }

    override fun viewMyBidDetails(_listItem: FamerBidsResponse.Bid) {
        val mFrom = MyDemandsFragment::class.java.simpleName
        val bundle = bundleOf(
            "BID_ID" to _listItem._id,
            "FROM" to mFrom
        )

        val i = Intent(requireContext(), DemandDetailActivity::class.java)
        i.putExtra("bundle", bundle)
        startActivity(i)
    }


}
