package com.example.mandiexe.ui.myrequirements

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.R
import com.example.mandiexe.adapter.OnBidHistoryClickListener
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.BidHistoryBody
import com.example.mandiexe.models.body.bid.AddBidBody
import com.example.mandiexe.models.body.bid.ViewBidBody
import com.example.mandiexe.models.responses.bids.AddBidResponse
import com.example.mandiexe.models.responses.bids.ViewBidResponse
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.ExternalUtils.createSnackbar
import com.example.mandiexe.utils.ExternalUtils.createToast
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.viewmodels.OpenNewRequirementViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.open_new_requirement_fragment.*
import retrofit2.Call
import retrofit2.Response

class OpenNewRequirementFragment : Fragment(), OnBidHistoryClickListener {

    companion object {
        fun newInstance() = OpenNewRequirementFragment()
    }

    private lateinit var viewModel: OpenNewRequirementViewModel
    private lateinit var root: View
    private lateinit var args: Bundle

    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog

    private var currentBid = ""
    private var ownerPhone = ""
    private var BID_ID = ""
    private val sessionManager = context?.let { SessionManager(it) }

    //Moduify object
    private var bidObject = AddBidBody("", "", "")

    //UI element
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.open_new_requirement_fragment, container, false)
        args = requireArguments()

        //Get the bid ID
        BID_ID = args.getString("BID_ID").toString()

        makeCall()

        root.findViewById<ImageView>(R.id.iv_new_requirement_call_buyer).setOnClickListener {
            //Call person
            val i = Intent(Intent.ACTION_CALL, Uri.parse("number"))
            startActivity(i)

        }

        root.findViewById<MaterialButton>(R.id.mtb_bid_new).setOnClickListener {
            createBidDialog()
        }

        root.findViewById<TextView>(R.id.tv_view_bid_history_requirement_new).setOnClickListener {
            viewBidHistory()
        }

        return root
    }

    private fun makeCall() {

        val service = RetrofitClient.makeCallsForBids(requireContext())
        val body = ViewBidBody(BID_ID)
        service.getFarmerViewParticularBid(
            body, accessToken = "Bearer ${sessionManager!!.fetchAcessToken()}",
        ).enqueue(object : retrofit2.Callback<ViewBidResponse> {
            override fun onResponse(
                call: Call<ViewBidResponse>,
                response: Response<ViewBidResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        initViews(response.body()!!)
                    }

                } else {
                    createSnackbar(
                        response.message(),
                        requireContext(),
                        container_open_new_req
                    )
                }
            }

            override fun onFailure(call: Call<ViewBidResponse>, t: Throwable) {
                createSnackbar(
                    ExternalUtils.returnStateMessageForThrowable(t),
                    requireContext(),
                    container_open_new_req
                )
            }
        })

    }

    private fun initViews(value: ViewBidResponse) {

        //Init current bid
        currentBid = value.bid.currentBid.toString()
        ownerPhone = value.bid.bidder.phone
        bidObject = AddBidBody("", value.bid.demand._id, value.bid.qty.toString())

        root.findViewById<ConstraintLayout>(R.id.mLayoutReqOpen).visibility = View.VISIBLE
        root.findViewById<ProgressBar>(R.id.pb_new_req_open).visibility = View.GONE



        root.findViewById<TextView>(R.id.tv_new_requirement_detail_crop_name).text =
            value.bid.demand.crop
        root.findViewById<TextView>(R.id.tv_new_requirement_detail_crop_type).text =
            value.bid.demand.variety
        root.findViewById<TextView>(R.id.tv_new_requirement_detail_crop_location).text =
            value.bid.bidder.address.toString()

        root.findViewById<TextView>(R.id.ans_detail_crop_quanity_open_req).text =
            value.bid.demand.qty.toString()
        root.findViewById<TextView>(R.id.ans_detail_crop_exp_open_req).text =
            value.bid.demand.expiry
        root.findViewById<TextView>(R.id.ans_detail_init_date_open_req).text =
            value.bid.demand.demandCreated

        root.findViewById<TextView>(R.id.tv_new_requirement_detail_current_bid).text =
            value.bid.demand.currentBid.toString()

        if (value.bid.demand.currentBid < value.bid.demand.offerPrice) {
            root.findViewById<TextView>(R.id.tv_new_requirement_detail_current_bid)
                .setTextColor(resources.getColor(R.color.red_A700))

        } else if (value.bid.demand.currentBid == value.bid.demand.offerPrice) {
            root.findViewById<TextView>(R.id.tv_new_requirement_detail_current_bid)
                .setTextColor(resources.getColor(R.color.blue_A700))

        }

        //Else the color is green

        root.findViewById<TextView>(R.id.tv_new_requirement_detail_initial_offer_price).text =
            value.bid.demand.offerPrice.toString()

        fillRecyclerView(value.bid.bids)


    }

    private fun fillRecyclerView(bids: List<ViewBidResponse.Bid.BidDetails>) {

//        val rv = root.findViewById<RecyclerView>(R.id.rv_bidHistoryOPneReq)
//        rv.layoutManager = LinearLayoutManager(context)
//        val adapter = MyBidHistoryAdapter(this)
//
//        //Create list
//        //Fill the rv wit
//        val mBids: MutableList<ViewSupplyResponse.Supply.Bid.BidDetails> = mutableListOf()
//        for (element in bids) {
//            mBids.add(
//                ViewSupplyResponse.Supply.Bid.BidDetails(
//                    element.amount,
//                    element.id,
//                    element.timestamp
//                )
//            )
//        }
//
//        adapter.lst = mBids
//        rv.adapter = adapter

    }

    private fun createBidDialog() {

        d = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val v = layoutInflater.inflate(R.layout.layout_add_bid, null)
        d.setView(v)

        val et = v.findViewById<EditText>(R.id.actvEditBid_priceNew)
        val til = v.findViewById<TextInputLayout>(R.id.tilEditBidOfferPriceNew)
        val tv = v.findViewById<TextView>(R.id.tv_add_bid_current_bid)


        tv.text = currentBid

        d.setPositiveButton(resources.getString(R.string.modifyBid)) { _, _ ->

            if (et.text.isEmpty()) {
                til.error = resources.getString(R.string.offerPriceError)
            } else {
                confirmModify(et.text.toString())
            }


        }
        d.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

        }
        d.create()

        tempRef = d.create()
        d.show()

    }

    private fun confirmModify(bidValue: String) {

        tempRef.dismiss()
        root.findViewById<ProgressBar>(R.id.pb_new_req_open).visibility = View.VISIBLE
        //Place bid and male call
        val service = RetrofitClient.makeCallsForBids(requireContext())
        bidObject.bid = bidValue

        Log.e("OPEN REQ", " Bid object " + bidObject)

        service.getFarmerAddBid(
            bidObject,
            accessToken = "Bearer ${sessionManager!!.fetchAcessToken()}"
        ).enqueue(object : retrofit2.Callback<AddBidResponse> {
            override fun onResponse(
                call: Call<AddBidResponse>,
                response: Response<AddBidResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.msg == "Bid updated successfully.") {
                        manageConfirmAddBid()

                    } else {
                        createSnackbar(
                            response.body()?.msg,
                            requireContext(),
                            container_open_new_req
                        )

                    }
                } else {
                    createSnackbar(response.body()?.msg, requireContext(), container_open_new_req)
                }

            }

            override fun onFailure(call: Call<AddBidResponse>, t: Throwable) {
                createSnackbar(
                    ExternalUtils.returnStateMessageForThrowable(t),
                    requireContext(),
                    container_open_new_req
                )
            }
        })


    }

    private fun manageConfirmAddBid() {
        root.findViewById<ProgressBar>(R.id.pb_new_req_open).visibility = View.GONE
        createToast(
            resources.getString(R.string.successfullBidAdd),
            requireContext(),
            container_open_new_req
        )
        onDestroy()

    }

    private fun viewBidHistory() {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OpenNewRequirementViewModel::class.java)

    }

    override fun viewBidDetails(_listItem: BidHistoryBody) {

    }

}
