package com.example.mandiexe.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.PriceViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyCropBidsFragment : Fragment() {

    companion object {
        fun newInstance() = MyCropBidsFragment()
    }

    private lateinit var viewModel: PriceViewModel
    private lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.my_crop_bids_fragment, container, false)


        root.findViewById<FloatingActionButton>(R.id.fab_add_my_stock).setOnClickListener {


            root.findNavController().navigate(R.id.action_nav_home_to_addStock)
            onDestroy()
        }


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PriceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
