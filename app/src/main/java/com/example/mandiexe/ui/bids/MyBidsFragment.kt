package com.example.mandiexe.ui.bids

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MyRequirementsAdapter
import com.example.mandiexe.adapter.OnMyBidClickListener
import com.example.mandiexe.models.responses.bids.FamerBidsResponse
import com.example.mandiexe.ui.demands.NewDemandActivity
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.showProgress
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
    private val TAG = MyBidsFragment::class.java.simpleName

    private lateinit var pb: ProgressBar

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "In on resume")

        loadRequirements()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.e(TAG, "In on create")
        root = inflater.inflate(R.layout.my_bids_fragment, container, false)
        //Get the items from retrofit call and paged adapter
        swl = root.findViewById<SwipeRefreshLayout>(R.id.swl_bid_fragment)
        pb = root.findViewById(R.id.pb_my_bids)

        swl.isRefreshing = true
        clearObservers()
        loadRequirements()

        swl.setOnRefreshListener {
            Log.e(TAG, "In on swipe refresh")
            loadRequirements()
            swl.isRefreshing = false
        }

        root.findViewById<FloatingActionButton>(R.id.fab_add_requirement).setOnClickListener {
            val i = Intent(requireContext(), NewDemandActivity::class.java)
            startActivity(i)
        }

        val tabLayout = root.findViewById<TabLayout>(R.id.tabsBids)
        val tab = tabLayout.getTabAt(1)
        tab!!.select()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.e(TAG, "In Tab selected position ${tab?.position}")
                if (tab?.position == 0) {
                    onDestroy()
                } else {
                    //Do nothing
                    onTabReselected(tab)
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //The other tab is selected
                Log.e(TAG, "In Tab un selected")
                onDestroy()

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //Do nothing
                Log.e(TAG, "In re selected selected")
            }
        })

        return root
    }

    private fun loadRequirements() {


        viewModel.reqFunction().observe(viewLifecycleOwner, Observer { mResponse ->

            //Check with the successful of it
            if (viewModel.successful.value != null) {
                //Log the response
                showProgress(pb, requireContext())
                if (viewModel.successful.value!!) {

                    manageReqLoadedResponses(mResponse)
                } else {
                    createSnackbar(
                        viewModel.message.value,
                        requireContext(),
                        container_req
                    )
                    doThrowableState()
                }
            } else {
                Log.e(TAG, "Loading")
                showProgress(pb, requireContext())
            }
        })

        swl.isRefreshing = false
    }

    private fun clearObservers() {
        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null
    }

    private fun manageReqLoadedResponses(mResponse: FamerBidsResponse?) {
        //Create rv

        val rv = root.findViewById<RecyclerView>(R.id.rv_requirement)
        val adapter = MyRequirementsAdapter(this)

        val empty = root.findViewById<ConstraintLayout>(R.id.llEmptyMyBids)
        val tError = root.findViewById<ConstraintLayout>(R.id.llErrorThrowableBid)
        if (mResponse != null) {
            if (mResponse.bids.isEmpty()) {
                empty.visibility = View.VISIBLE
                doEmptyStates()

            } else {
                empty.visibility = View.GONE
                tError.visibility = View.GONE

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
        super.onDestroy()
        clearObservers()

        val navC = findNavController()
        navC.navigateUp()
        Log.e(TAG, "In on destroy")


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

    private fun doEmptyStates() {
        Log.e(TAG, "In empty state")
        this.apply {
            llEmptyMyBids.visibility = View.VISIBLE
            llErrorThrowableBid.visibility = View.GONE
        }
    }

    private fun doThrowableState() {
        Log.e(TAG, "In throwable state")
        this.apply {
            llEmptyMyBids.visibility = View.GONE
            llErrorThrowableBid.visibility = View.VISIBLE
        }
    }


}
