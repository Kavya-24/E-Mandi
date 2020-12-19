package com.example.mandiexe.ui.mybids

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.BidHistoryViewModel

class BidHistory : Fragment() {

    companion object {
        fun newInstance() = BidHistory()
    }

    private lateinit var viewModel: BidHistoryViewModel
    private lateinit var root: View
    private lateinit var args: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.bid_history_fragment, container, false)
        args = requireArguments()
        //When this comes
        initViews()

        return root
    }

    private fun initViews() {
        //For the headings
        //Populate the table row
        //Instantiate the rv and adapter

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BidHistoryViewModel::class.java)

    }


}
