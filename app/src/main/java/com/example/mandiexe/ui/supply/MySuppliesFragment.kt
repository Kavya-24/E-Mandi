package com.example.mandiexe.ui.supply

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MySuppliesAdapter
import com.example.mandiexe.adapter.OnMyStockClickListener
import com.example.mandiexe.models.responses.supply.FarmerSuppliesResponse
import com.example.mandiexe.utils.Communicator
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.viewmodels.MySuppliesViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.my_crop_bids_fragment.*


class MySuppliesFragment : Fragment(), OnMyStockClickListener, Observable {

    companion object {
        fun newInstance() = MySuppliesFragment()
    }

    private val viewModel: MySuppliesViewmodel by viewModels()
    private lateinit var root: View
    private lateinit var comm: Communicator
    private lateinit var tabLayout: TabLayout


    override fun onResume() {
        super.onResume()
        Log.e("In supplies", "In on resume")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        root = inflater.inflate(R.layout.my_crop_bids_fragment, container, false)

        loadItems()

        comm = activity as Communicator

        tabLayout = root.findViewById(R.id.tabsSupplies) as TabLayout
        tabLayout.selectTab(tabLayout.getTabAt(0), true)

//        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE


        tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 1) {
                    //Navigae to my requirements fragment
                    root.findNavController()
                        .navigate(R.id.action_nav_my_supplies_to_nav_my_requirements)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        //Get the items from normal adapter

        root.findViewById<FloatingActionButton>(R.id.fab_add_my_stock).setOnClickListener {

            root.findNavController().navigate(R.id.action_nav_my_supplies_to_addStock)

        }

        val swl = root.findViewById<SwipeRefreshLayout>(R.id.swl_supplies_fragment)
        swl.setOnRefreshListener {
            loadItems()
            swl.isRefreshing = false
        }

        return root
    }

    private fun loadItems() {

        root.findViewById<ProgressBar>(R.id.pb_my_crops).visibility = View.VISIBLE


        viewModel.supplyFunction().observe(viewLifecycleOwner, Observer { mResponse ->
            if (viewModel.successful.value != null) {
                if (viewModel.successful.value!!) {
                    manageStockLoadedResponses(mResponse)

                } else {
                    createSnackbar(viewModel.message.value, requireContext(), container_my_crops)

                }
            }

        })


        root.findViewById<ProgressBar>(R.id.pb_my_crops).visibility = View.GONE


    }

    @SuppressLint("CutPasteId")
    private fun manageStockLoadedResponses(mResponse: FarmerSuppliesResponse?) {
        //Create rv

        Log.e("MY Supply", "In manage stock")

        root.findViewById<ProgressBar>(R.id.pb_my_crops).visibility = View.GONE
        val rv = root.findViewById<RecyclerView>(R.id.rv_my_stocks)
        val adapter = MySuppliesAdapter(this)

        if (mResponse != null) {
            if (mResponse.supplies.isEmpty()) {
                root.findViewById<AppCompatTextView>(R.id.tvEmptyListCrop).visibility = View.VISIBLE
                root.findViewById<AppCompatTextView>(R.id.tvEmptyListCrop).text =
                    context?.resources?.getString(R.string.noSupply)

            } else {
                root.findViewById<AppCompatTextView>(R.id.tvEmptyListCrop).visibility = View.GONE
                adapter.lst = mResponse.supplies
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = adapter
            }
        }

        root.findViewById<ProgressBar>(R.id.pb_my_crops).visibility = View.GONE


    }

    override fun viewMyStockDetails(_listItem: FarmerSuppliesResponse.Supply) {

        val from = MySuppliesFragment::class.java.simpleName
        val bundle = bundleOf(
            "SUPPLY_ID" to _listItem._id,
            "FROM" to from
        )

        //   val supply = _listItem._id
        val i = Intent(requireContext(), MyCropBidDetails::class.java)
        i.putExtra("bundle", bundle)
        startActivity(i)
        //     navController.navigate(R.id.action_nav_home_to_myBidDetails, bundle)


    }

    override fun onDestroy() {

        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null

        Log.e("In supplies", "In on destroy")
        super.onDestroy()

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }


}
