package com.example.mandiexe.ui.bids

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.example.mandiexe.ui.demands.NewDemandActivity
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.viewmodels.MyBidsViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.my_bids_fragment.*

class MyBidsFragment : Fragment(), OnMyBidClickListener {

    companion object {
        fun newInstance() = MyBidsFragment()
    }


    private val viewModel: MyBidsViewmodel by viewModels()
    private lateinit var root: View
    private lateinit var tabLayout: TabLayout

    private lateinit var swl: SwipeRefreshLayout

    override fun onResume() {
        super.onResume()
        Log.e("Req", "In resume")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.my_bids_fragment, container, false)
        //Get the items from retrofit call and paged adapter
        swl = root.findViewById<SwipeRefreshLayout>(R.id.swl_bid_fragment)



        loadRequirements()

        swl.setOnRefreshListener {
            loadRequirements()
            swl.isRefreshing = false
        }

        root.findViewById<FloatingActionButton>(R.id.fab_add_requirement).setOnClickListener {
            val i = Intent(requireContext(), NewDemandActivity::class.java)
            startActivity(i)
        }

        return root
    }

    private fun loadRequirements() {

        swl.isRefreshing = true

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

        swl.isRefreshing = false
    }

    private fun manageReqLoadedResponses(mResponse: FamerBidsResponse?) {
        //Create rv

        val rv = root.findViewById<RecyclerView>(R.id.rv_requirement)
        val adapter = MyRequirementsAdapter(this)

        val empty = root.findViewById<ConstraintLayout>(R.id.llEmptyMyBids)

        if (mResponse != null) {
            if (mResponse.bids.isEmpty()) {
                empty.visibility = View.VISIBLE

            } else {
                empty.visibility = View.GONE

                val mutableDemands = mutableListOf<FamerBidsResponse.Bid>()
                for (i in mResponse.bids) {
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
        val mFrom = MyBidsFragment::class.java.simpleName
        val bundle = bundleOf(
            "BID_ID" to _listItem._id,
            "FROM" to mFrom
        )

        val i = Intent(requireContext(), BidDetailActivity::class.java)
        i.putExtra("bundle", bundle)
        startActivity(i)
    }


}
