package com.example.mandiexe.ui.home

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mandiexe.R
import com.example.mandiexe.adapter.BidHistoryAdapter
import com.example.mandiexe.adapter.OnMyBidClickListenerGlobal
import com.example.mandiexe.models.responses.bids.BidHistoryResponse
import com.example.mandiexe.ui.bids.BidDetailActivity
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.viewmodels.FarmerBidHistoryViewModel
import kotlinx.android.synthetic.main.farmer_bid_history_fragment.*

class FarmerBidHistory : Fragment(), OnMyBidClickListenerGlobal {

    companion object {
        fun newInstance() = FarmerBidHistory()
    }

    private val viewModel: FarmerBidHistoryViewModel by viewModels()
    private lateinit var root: View
    private lateinit var pb: ProgressBar
    private lateinit var swl: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.farmer_bid_history_fragment, container, false)
        pb = root.findViewById(R.id.pb_history_bid)
        swl = root.findViewById(R.id.swl_bid_history)



        loadHistory()

        swl.setOnRefreshListener {
            loadHistory()
            swl.isRefreshing = false
        }


        return root
    }

    private fun loadHistory() {

        swl.isRefreshing = true

        viewModel.BidFunction().observe(viewLifecycleOwner, Observer { mResponse ->
            val success = viewModel.successful.value

            if (success != null) {
                if (success) {
                    loadItemsInRV(mResponse)
                } else {
                    UIUtils.createSnackbar(
                        viewModel.message.value,
                        ApplicationUtils.getContext(),
                        container_bid_history
                    )
                }
            }


        })


        swl.isRefreshing = false
    }

    private fun loadItemsInRV(mResponse: BidHistoryResponse) {

        val rv = root.findViewById<RecyclerView>(R.id.rv_history_bid)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BidHistoryAdapter(this)
        rv.adapter = adapter

        val empty = root.findViewById<ConstraintLayout>(R.id.llEmptyBidHistory)

        if (mResponse.bids.isEmpty()) {
            empty.visibility = View.VISIBLE


        } else {
            empty.visibility = View.GONE
            adapter.lst = mResponse.bids
            adapter.notifyDataSetChanged()
        }

    }


    override fun onDestroy() {

        Log.e("Bid History", "In on destroy")


        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null

        super.onDestroy()


    }

    override fun viewMyBidDetails(_listItem: BidHistoryResponse.Bid) {

        val bundle = bundleOf(
            "BID_ID" to _listItem._id,
            "FROM" to FarmerBidHistory::class.java.simpleName
        )

        val i = Intent(requireContext(), BidDetailActivity::class.java)
        i.putExtra("bundle", bundle)
        startActivity(i)
    }


}
