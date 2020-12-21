package com.example.mandiexe.ui.supply

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.utils.Communicator
import com.example.mandiexe.viewmodels.PriceViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MyCropBidsFragment : Fragment() {

    companion object {
        fun newInstance() = MyCropBidsFragment()
    }

    private lateinit var viewModel: PriceViewModel
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PriceViewModel::class.java)
    }

}
