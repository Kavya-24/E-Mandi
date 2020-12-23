package com.example.mandiexe.ui.myrequirements

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.OpenNewRequirementViewModel
import com.google.android.material.button.MaterialButton

class OpenNewRequirementFragment : Fragment() {

    companion object {
        fun newInstance() = OpenNewRequirementFragment()
    }

    private lateinit var viewModel: OpenNewRequirementViewModel
    private lateinit var root: View
    private lateinit var args: Bundle

    //UI element
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.open_new_requirement_fragment, container, false)
        args = requireArguments()

        //populateArgumentData


        root.findViewById<ImageView>(R.id.iv_new_requirement_call_buyer).setOnClickListener {
            //Call person
            val i = Intent(Intent.ACTION_CALL, Uri.parse("number"))
            startActivity(i)

        }

        root.findViewById<MaterialButton>(R.id.mtb_bid_new).setOnClickListener {
            //Bid
        }

        root.findViewById<TextView>(R.id.tv_view_bid_history_requirement_new).setOnClickListener {
            viewBidHistory()
        }

        return root
    }

    private fun viewBidHistory() {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OpenNewRequirementViewModel::class.java)

    }

}
