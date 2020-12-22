package com.example.mandiexe.ui.myrequirements

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MyRequirementAdapter
import com.example.mandiexe.adapter.OnMyBidClickListener
import com.example.mandiexe.models.responses.bids.FarmerBidsResponse
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.viewmodels.RequirementsViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.requirement_fragment.*

class RequirementFragment : Fragment(), OnMyBidClickListener {

    companion object {
        fun newInstance() = RequirementFragment()
    }

    private lateinit var viewModel: RequirementsViewmodel
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.requirement_fragment, container, false)

        //Get the items from retrofit call and paged adapter

        loadRequirements()

        val swl = root.findViewById<SwipeRefreshLayout>(R.id.swl_stock_fragment)
        swl.setOnRefreshListener {
            loadRequirements()
            swl.isRefreshing = false
        }

        root.findViewById<FloatingActionButton>(R.id.fab_add_requirement).setOnClickListener {
            root.findNavController().navigate(R.id.action_nav_home_to_addRequirement)
        }

        return root
    }

    private fun loadRequirements() {

        viewModel.reqFunction().observe(viewLifecycleOwner, Observer { mResponse ->

            //Check with the sucessful of it
            if (viewModel.successful.value == false) {
                ExternalUtils.createSnackbar(
                    viewModel.message.value,
                    requireContext(),
                    container_req
                )
            } else {
                manageReqLoadedResponses(mResponse)
            }
        })

    }

    @SuppressLint("CutPasteId")
    private fun manageReqLoadedResponses(mResponse: FarmerBidsResponse?) {
        //Create rv
        val rv = root.findViewById<RecyclerView>(R.id.rv_requirement)
        val adapter = MyRequirementAdapter(this)

        if (mResponse != null) {
            if (mResponse.bids.isEmpty()) {
                root.findViewById<AppCompatTextView>(R.id.tvEmptyList).visibility = View.VISIBLE
                root.findViewById<AppCompatTextView>(R.id.tvEmptyList).text =
                    context?.resources?.getString(R.string.noDemand)

            } else {
                adapter.lst = mResponse.bids
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = adapter
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RequirementsViewmodel::class.java)

    }

    override fun viewMyBidDetails(_listItem: FarmerBidsResponse.Bid) {

    }

}
