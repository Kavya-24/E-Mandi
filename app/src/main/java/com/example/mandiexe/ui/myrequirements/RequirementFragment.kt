package com.example.mandiexe.ui.myrequirements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.StockViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RequirementFragment : Fragment() {

    companion object {
        fun newInstance() = RequirementFragment()
    }

    private lateinit var viewModel: StockViewModel
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.requirement_fragment, container, false)

        //Get the items from retrofit call and paged adapter


        root.findViewById<FloatingActionButton>(R.id.fab_add_requirement).setOnClickListener {
            root.findNavController().navigate(R.id.action_nav_home_to_addRequirement)
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StockViewModel::class.java)

    }

}
