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
import com.example.mandiexe.adapter.OnMySupplyHistoryClickListener
import com.example.mandiexe.adapter.SupplyHistoryAdapter
import com.example.mandiexe.models.responses.supply.SupplyHistoryResponse
import com.example.mandiexe.ui.supply.SupplyDetailActivity
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.viewmodels.FarmerSupplyHistoryViewModel
import kotlinx.android.synthetic.main.farmer_supply_history_fragment.*

class FarmerSupplyHistory : Fragment(), OnMySupplyHistoryClickListener {

    companion object {
        fun newInstance() = FarmerSupplyHistory()
    }

    private val viewModel: FarmerSupplyHistoryViewModel by viewModels()
    private lateinit var root: View
    private lateinit var pb: ProgressBar


    private lateinit var swl: SwipeRefreshLayout

    override fun onResume() {
        loadHistory()
        super.onResume()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.farmer_supply_history_fragment, container, false)
        pb = root.findViewById(R.id.pb_history_supply)
        swl = root.findViewById<SwipeRefreshLayout>(R.id.swl_supply_history)


        loadHistory()

        swl.setOnRefreshListener {
            loadHistory()
            swl.isRefreshing = false

        }



        return root
    }

    private fun loadHistory() {

        swl.isRefreshing = true

        val mSnapbarView = root.findViewById<ConstraintLayout>(R.id.container_supply_history)
        viewModel.supplyFunction(mSnapbarView, pb)
            .observe(viewLifecycleOwner, Observer { mResponse ->
                val success = viewModel.successful.value

                if (success != null) {
                    hideProgress(pb, requireContext())

                    if (success) {
                        loadItemsInRV(mResponse)
                    } else {
                        createSnackbar(
                            viewModel.message.value,
                            requireContext(),
                            container_supply_history
                        )
                    }
                } else {
                    showProgress(pb, requireContext())
                }


            })

        swl.isRefreshing = false

    }

    private fun loadItemsInRV(mResponse: SupplyHistoryResponse) {
        val rv = root.findViewById<RecyclerView>(R.id.rv_history_supply)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SupplyHistoryAdapter(this)
        val empty = root.findViewById<ConstraintLayout>(R.id.llEmptySupplyHistory)

        if (mResponse.supplies.size == 0) {
            empty.visibility = View.VISIBLE


        } else {
            adapter.lst = mResponse.supplies
            empty.visibility = View.GONE
            //Notify adapter
            adapter.notifyDataSetChanged()
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
        val i = Intent(requireContext(), SupplyDetailActivity::class.java)
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
