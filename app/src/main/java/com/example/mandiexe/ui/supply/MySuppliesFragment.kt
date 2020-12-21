package com.example.mandiexe.ui.supply

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MySuppliesAdapter
import com.example.mandiexe.adapter.OnMyStockClickListener
import com.example.mandiexe.models.responses.supply.FarmerSuppliesResponse
import com.example.mandiexe.utils.Communicator
import com.example.mandiexe.utils.ExternalUtils.createSnackbar
import com.example.mandiexe.viewmodels.MySuppliesViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.my_crop_bids_fragment.*


class MySuppliesFragment : Fragment(), OnMyStockClickListener {

    companion object {
        fun newInstance() = MySuppliesFragment()
    }

    private lateinit var viewModel: MySuppliesViewmodel
    private lateinit var root: View
    private lateinit var comm: Communicator

    override fun onResume() {
        super.onResume()
        Log.e("IN my crop", "In on resume")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.my_crop_bids_fragment, container, false)

        loadItems()
        comm = activity as Communicator

        //Get the items from normal adapter

        root.findViewById<FloatingActionButton>(R.id.fab_add_my_stock).setOnClickListener {

            //Use communicators
            //Replace containers

            val navController = root.findNavController()
            navController.navigateUp()
            navController.navigate(R.id.action_nav_home_to_nav_add_stock_2)

        }


        return root
    }

    private fun loadItems() {

        viewModel.supplyFunction().observe(viewLifecycleOwner, Observer { mResponse ->

            //Check with the sucessful of it
            if (viewModel.successful.value == false) {
                createSnackbar(viewModel.message.value, requireContext(), container_my_crops)
            } else {
                manageStockLoadedResponses(mResponse)
            }
        })


    }

    @SuppressLint("CutPasteId")
    private fun manageStockLoadedResponses(mResponse: FarmerSuppliesResponse?) {
        //Create rv
        val rv = root.findViewById<RecyclerView>(R.id.rv_my_stocks)
        val adapter = MySuppliesAdapter(this)

        if (mResponse != null) {
            if (mResponse.supplies.isEmpty()) {
                root.findViewById<AppCompatTextView>(R.id.tvEmptyListCrop).visibility = View.VISIBLE
                root.findViewById<AppCompatTextView>(R.id.tvEmptyListCrop).text =
                    context?.resources?.getString(R.string.noSupply)

            } else {
                adapter.lst = mResponse.supplies
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = adapter
            }
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MySuppliesViewmodel::class.java)
    }

    override fun viewMyStockDetails(_listItem: FarmerSuppliesResponse.Supply) {

        //##MIght give error
        val navController = root.findNavController()
        navController.navigateUp()

        val bundle = bundleOf(
            "SUPPLY_ID" to _listItem._id
        )

        val supply = _listItem._id
        navController.navigate(R.id.action_nav_home_to_myBidDetails, bundle)



    }

}
