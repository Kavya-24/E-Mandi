package com.example.mandiexe.ui.home

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
import com.example.mandiexe.adapter.OnMySupplyHistoryClickListener
import com.example.mandiexe.adapter.SupplyHistoryAdapter
import com.example.mandiexe.models.responses.supply.SupplyHistoryResponse
import com.example.mandiexe.ui.supply.MyCropBidDetails
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import kotlinx.android.synthetic.main.farmer_supply_history_fragment.*

class FarmerSupplyHistory : Fragment(), OnMySupplyHistoryClickListener {

    companion object {
        fun newInstance() = FarmerSupplyHistory()
    }

    private val viewModel: FarmerSupplyHistoryViewModel by viewModels()
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.farmer_supply_history_fragment, container, false)

        root.findViewById<ProgressBar>(R.id.pb_history_supply).visibility = View.VISIBLE

        loadHistory()
        val swl = root.findViewById<SwipeRefreshLayout>(R.id.swl_supply_history)
        root.findViewById<ProgressBar>(R.id.pb_history_supply).visibility = View.GONE

        swl.setOnRefreshListener {
            loadHistory()
            swl.isRefreshing = false

        }



        return root
    }

    private fun loadHistory() {

        viewModel.supplyFunction().observe(viewLifecycleOwner, Observer { mResponse ->
            val success = viewModel.successful.value

            if (success != null) {
                if (success) {
                    loadItemsInRV(mResponse)
                } else {
                    createSnackbar(
                        viewModel.message.value,
                        requireContext(),
                        container_supply_history
                    )
                }
            }


        })

        root.findViewById<ProgressBar>(R.id.pb_history_supply).visibility = View.GONE


    }

    private fun loadItemsInRV(mResponse: SupplyHistoryResponse) {
        val rv = root.findViewById<RecyclerView>(R.id.rv_history_supply)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SupplyHistoryAdapter(this)
        val mTv = root.findViewById<AppCompatTextView>(R.id.tvEmptyListSupply)
        if (mResponse.supplies.size == 0) {
            mTv.visibility = View.VISIBLE
            mTv.text =
                context?.resources?.getString(R.string.noSupply)


        } else {
            adapter.lst = mResponse.supplies
            mTv.visibility = View.GONE
        }

        rv.adapter = adapter

    }

    override fun viewMyStockDetails(_listItem: SupplyHistoryResponse.Supply) {

        val from = FarmerSupplyHistory::class.java.simpleName
        val bundle = bundleOf(
            "SUPPLY_ID" to _listItem._id,
            "FROM" to from
        )

        //   val supply = _listItem._id
        val i = Intent(requireContext(), MyCropBidDetails::class.java)
        i.putExtra("bundle", bundle)
        startActivity(i)


    }


    override fun onDestroy() {

        Log.e("Supply History", "In on destroy")


        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null

        super.onDestroy()


    }


}
