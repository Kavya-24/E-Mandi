package com.example.mandiexe.ui.myrequirements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.BidViewModel

class BidFragment : Fragment() {

    companion object {
        fun newInstance() = BidFragment()
    }

    private lateinit var viewModel: BidViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bid_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BidViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
