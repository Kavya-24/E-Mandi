package com.example.mandiexe.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.BidHistoryAdapter
import com.example.mandiexe.adapter.OnMyBidHistoryGlobalClickListener
import com.example.mandiexe.models.responses.bids.BidHistoryResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import kotlinx.android.synthetic.main.farmer_bid_history_fragment.*

class FarmerBidHistory : Fragment(), OnMyBidHistoryGlobalClickListener {

    companion object {
        fun newInstance() = FarmerBidHistory()
    }

    private val viewModel: FarmerBidHistoryViewModel by viewModels()
    private lateinit var root: View
    private lateinit var pb: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.farmer_bid_history_fragment, container, false)
        pb = root.findViewById(R.id.pb_history_bid)

        showProgress(pb, requireContext())
        loadHistory()

        hideProgress(pb, requireContext())



        return root
    }

    private fun loadHistory() {

        showProgress(pb, requireContext())

        viewModel.BidStockFunction().observe(viewLifecycleOwner, Observer { mResponse ->
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

        hideProgress(pb, requireContext())

    }

    private fun loadItemsInRV(mResponse: BidHistoryResponse) {

        val rv = root.findViewById<RecyclerView>(R.id.rv_history_bid)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BidHistoryAdapter(this)

        if (mResponse.bids.size == 0) {
            root.findViewById<AppCompatTextView>(R.id.tvEmptyListBid).visibility = View.VISIBLE
            root.findViewById<AppCompatTextView>(R.id.tvEmptyListBid).text =
                context?.resources?.getString(R.string.noDemand)


        } else {
            adapter.lst = mResponse.bids
        }

        rv.adapter = adapter

    }

    override fun viewMyStockDetails(_listItem: BidHistoryResponse.Bid) {

    }


    override fun onDestroy() {

        Log.e("Bid History", "In on destroy")


        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null

        super.onDestroy()


    }


}
