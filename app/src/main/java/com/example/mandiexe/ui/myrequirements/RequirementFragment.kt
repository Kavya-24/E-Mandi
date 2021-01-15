package com.example.mandiexe.ui.myrequirements

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MyRequirementAdapter
import com.example.mandiexe.adapter.OnMyBidClickListener
import com.example.mandiexe.models.responses.bids.FamerBidsResponse
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.viewmodels.RequirementsViewmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.requirement_fragment.*

class RequirementFragment : Fragment(), OnMyBidClickListener {

    companion object {
        fun newInstance() = RequirementFragment()
    }


    private val viewModel: RequirementsViewmodel by viewModels()
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
            val i = Intent(requireContext(), AddRequirement::class.java)
            startActivity(i)
        }

        return root
    }

    private fun loadRequirements() {

        root.findViewById<ProgressBar>(R.id.pb_requirement).visibility = View.VISIBLE

        viewModel.reqFunction().observe(viewLifecycleOwner, Observer { mResponse ->

            //Check with the successful of it
            if (viewModel.successful.value != null) {
                if (viewModel.successful.value == false) {
                    ExternalUtils.createSnackbar(
                        viewModel.message.value,
                        requireContext(),
                        container_req
                    )
                } else {
                    manageReqLoadedResponses(mResponse)
                }
            }
        })

        root.findViewById<ProgressBar>(R.id.pb_requirement).visibility = View.GONE

    }

    @SuppressLint("CutPasteId")
    private fun manageReqLoadedResponses(mResponse: FamerBidsResponse?) {
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


    override fun onDestroy() {
        super.onDestroy()
        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null
    }

    override fun viewMyBidDetails(_listItem: FamerBidsResponse.Bid) {

        val bundle = bundleOf(
            "BID_ID" to _listItem._id
        )

        root.findNavController()
            .navigate(R.id.action_nav_home_to_myRequirementDetails, bundle)

    }


}
