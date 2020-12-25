package com.example.mandiexe.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mandiexe.R

class FarmerSupplyHistory : Fragment() {

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




        return root
    }

}
