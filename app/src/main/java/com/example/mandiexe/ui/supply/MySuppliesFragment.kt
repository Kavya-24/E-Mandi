package com.example.mandiexe.ui.supply

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
import com.example.mandiexe.adapter.MySuppliesAdapter
import com.example.mandiexe.adapter.OnMyStockClickListener
import com.example.mandiexe.models.responses.supply.FarmerSuppliesResponse
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.viewmodels.MySuppliesViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.my_supplies_fragment.*


class MySuppliesFragment : Fragment(), OnMyStockClickListener {

    companion object {
        fun newInstance() = MySuppliesFragment()
    }

    private val viewModel: MySuppliesViewmodel by viewModels()
    private lateinit var root: View
    private val TAG = MySuppliesFragment::class.java.simpleName
    private lateinit var swl: SwipeRefreshLayout
    private lateinit var mSwipeable: ConstraintLayout
    private lateinit var pb: ProgressBar


    override fun onResume() {
        super.onResume()
        loadItems()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.my_supplies_fragment, container, false)
        swl = root.findViewById<SwipeRefreshLayout>(R.id.swl_supplies_fragment)
        mSwipeable = root.findViewById<ConstraintLayout>(R.id.swipeablecslsuuplies)
        pb = root.findViewById(R.id.pb_my_crops)


        swl.isRefreshing = true
        clearObservers()

        loadItems()


        //Get the items from normal adapter

        root.findViewById<FloatingActionButton>(R.id.fab_add_my_stock).setOnClickListener {

            val i = Intent(requireContext(), AddStock::class.java)
            startActivity(i)

        }

        swl.setOnRefreshListener {

            loadItems()
            swl.isRefreshing = false
        }



        return root
    }


    private fun loadItems() {

        val mSnackbarView = root.findViewById<ConstraintLayout>(R.id.container_my_crops)

        viewModel.supplyFunction(mSnackbarView, pb).observe(
            viewLifecycleOwner,
            Observer { mResponse ->
                if (viewModel.successful.value != null) {
                    hideProgress(pb, requireContext())
                    if (viewModel.successful.value!!) {

                        manageStockLoadedResponses(mResponse)

                    } else {
                        doThrowableState()
                        createSnackbar(
                            viewModel.message.value,
                            requireContext(),
                            container_my_crops
                        )

                    }
                } else {
                    //While state
                    showProgress(pb, requireContext())
                }

            })

        swl.isRefreshing = false
    }


    private fun manageStockLoadedResponses(mResponse: FarmerSuppliesResponse?) {
        //Create rv

        Log.e("MY Supply", "In manage stock")


        swl.isRefreshing = true
        val rv = root.findViewById<RecyclerView>(R.id.rv_my_stocks)
        val adapter = MySuppliesAdapter(this)
        val empty = root.findViewById<ConstraintLayout>(R.id.llEmptyMySupplies)
        val tError = root.findViewById<ConstraintLayout>(R.id.llErrorThrowableSupply)

        if (mResponse != null) {
            if (mResponse.supplies.isEmpty()) {
                empty.visibility = View.VISIBLE
                rv.visibility = View.GONE
                doEmptyStates()

            } else {

                doResponseStates()
                rv.visibility = View.VISIBLE
                empty.visibility = View.GONE
                tError.visibility = View.GONE

                //Sort by timestamp
                mResponse.supplies.sortedByDescending { it.lastModified }

                adapter.lst = mResponse.supplies
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = adapter
            }
        }

        swl.isRefreshing = false

    }


    override fun viewMyStockDetails(_listItem: FarmerSuppliesResponse.Supply) {

        val from = MySuppliesFragment::class.java.simpleName
        val bundle = bundleOf(
            "SUPPLY_ID" to _listItem._id,
            "FROM" to from
        )

        //   val supply = _listItem._id
        val i = Intent(requireContext(), SupplyDetailActivity::class.java)
        i.putExtra("bundle", bundle)
        startActivity(i)
        //     navController.navigate(R.id.action_nav_home_to_myBidDetails, bundle)


    }

    override fun onDestroy() {

        clearObservers()
        Log.e(TAG, "In on destroy")
        super.onDestroy()

    }

    private fun clearObservers() {

        //Hide throwable states

        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null

    }

    private fun doEmptyStates() {
        Log.e(TAG, "In empty state")
        this.apply {
            llEmptyMySupplies.visibility = View.VISIBLE
            llErrorThrowableSupply.visibility = View.GONE

        }
    }

    private fun doThrowableState() {
        Log.e(TAG, "In throwable state")
        this.apply {
            llEmptyMySupplies.visibility = View.GONE
            llErrorThrowableSupply.visibility = View.VISIBLE

        }
    }


    private fun doResponseStates() {
        Log.e(TAG, "In response state")
        this.apply {
            llEmptyMySupplies.visibility = View.GONE
            llErrorThrowableSupply.visibility = View.GONE

        }
    }
}
