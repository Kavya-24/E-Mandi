package com.example.mandiexe.ui.myrequirements

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MyBidHistoryAdapter
import com.example.mandiexe.adapter.OnBidHistoryClickListener
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.BidHistoryBody
import com.example.mandiexe.models.body.bid.AddBidBody
import com.example.mandiexe.models.body.bid.ViewBidBody
import com.example.mandiexe.models.responses.bids.AddBidResponse
import com.example.mandiexe.models.responses.bids.ViewBidResponse
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.createToast
import com.example.mandiexe.viewmodels.OpenNewRequirementViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.open_new_requirement_fragment.*
import retrofit2.Call
import retrofit2.Response

class OpenNewRequirementFragment : AppCompatActivity(), OnBidHistoryClickListener {

    companion object {
        fun newInstance() = OpenNewRequirementFragment()
    }

    private lateinit var viewModel: OpenNewRequirementViewModel

    //private lateinit var root: View
    private lateinit var args: Bundle

    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog

    private var currentBid = ""
    private var ownerPhone = ""
    private var BID_ID = ""
    private val sessionManager = SessionManager(this)
    private val TAG = OpenNewRequirementFragment::class.java.simpleName

    //Moduify object
    private var bidObject = AddBidBody("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(PreferenceUtil.getLanguageFromPreference(), this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.open_new_requirement_fragment)


        args = intent?.getBundleExtra("bundle")!!
        //Get the bid ID
        BID_ID = args.getString("BID_ID").toString()

        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = resources.getString(R.string.details)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }
        Log.e(TAG, "Argument str is" + BID_ID)

        makeCall()

        findViewById<ImageView>(R.id.iv_new_requirement_call_buyer).setOnClickListener {
            //Call person
            val i = Intent(Intent.ACTION_CALL, Uri.parse(ownerPhone))
            startActivity(i)

        }

        findViewById<MaterialButton>(R.id.mtb_bid_new).setOnClickListener {
            createBidDialog()
        }

        findViewById<TextView>(R.id.tv_view_bid_history_requirement_new).setOnClickListener {
            viewBidHistory()
        }

    }

    private fun makeCall() {

        val service = RetrofitClient.makeCallsForBids(this)
        val body = ViewBidBody(BID_ID)
        service.getFarmerViewParticularBid(
            body
        ).enqueue(object : retrofit2.Callback<ViewBidResponse> {
            @RequiresApi(Build.VERSION_CODES.Q)
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
                        this@OpenNewRequirementFragment,
                        container_open_new_req
                    )
                }
            }

            override fun onFailure(call: Call<ViewBidResponse>, t: Throwable) {
                createSnackbar(
                    ExternalUtils.returnStateMessageForThrowable(t),
                    this@OpenNewRequirementFragment,
                    container_open_new_req
                )
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initViews(value: ViewBidResponse) {

        //Init current bid
        currentBid = value.bid.currentBid.toString()
        ownerPhone = value.bid.bidder.phone
        bidObject = AddBidBody("", value.bid.demand._id)

        findViewById<ConstraintLayout>(R.id.mLayoutReqOpen).visibility = View.VISIBLE
        findViewById<ProgressBar>(R.id.pb_new_req_open).visibility = View.GONE


        //TRANSLATE
        OfflineTranslate.translateToDefault(
            this,
            value.bid.demand.crop,
            findViewById<TextView>(R.id.tv_new_requirement_detail_crop_name)
        )
        OfflineTranslate.translateToDefault(
            this,
            value.bid.demand.variety,
            findViewById<TextView>(R.id.tv_new_requirement_detail_crop_type)
        )
        OfflineTranslate.translateToDefault(
            this,
            value.bid.demand.description,
            findViewById<TextView>(R.id.tv_new_description)
        )

        //Transliteration
        findViewById<TextView>(R.id.tv_new_requirement_detail_crop_location).text =
            OfflineTranslate.transliterateToDefault(value.bid.bidder.address.toString())

        findViewById<TextView>(R.id.ans_detail_crop_quanity_open_req).text =
            value.bid.demand.qty.toString()
        findViewById<TextView>(R.id.ans_detail_crop_exp_open_req).text =
            TimeConversionUtils.convertTimeToEpoch(value.bid.demand.expiry)
        findViewById<TextView>(R.id.ans_detail_init_date_open_req).text =
            TimeConversionUtils.convertTimeToEpoch(value.bid.demand.demandCreated)

        findViewById<TextView>(R.id.tv_new_requirement_detail_current_bid).text =
            value.bid.demand.currentBid.toString()

        if (value.bid.demand.currentBid < value.bid.demand.offerPrice) {
            findViewById<TextView>(R.id.tv_new_requirement_detail_current_bid)
                .setTextColor(resources.getColor(R.color.deltaRed))

        } else if (value.bid.demand.currentBid == value.bid.demand.offerPrice) {
            findViewById<TextView>(R.id.tv_new_requirement_detail_current_bid)
                .setTextColor(resources.getColor(R.color.wildColor))

        }


        //Set the buyer
        //Transliterate
        findViewById<TextView>(R.id.tv_new_requirement_details_buyer_name).text =
            OfflineTranslate.transliterateToDefault(value.bid.bidder.name)
        ownerPhone = value.bid.bidder.phone.toString()
        //Else the color is green

        findViewById<TextView>(R.id.tv_new_requirement_detail_initial_offer_price).text =
            value.bid.demand.offerPrice.toString()


        fillRecyclerView(value.bid.bids)


    }

    private fun fillRecyclerView(bids: List<ViewBidResponse.Bid.BidDetails>) {

        val rv = findViewById<RecyclerView>(R.id.rv_bidHistoryOPneReq)
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = MyBidHistoryAdapter(this)

        //Create list
        val mBids: MutableList<BidHistoryBody> = mutableListOf()

//
//        for (element in bids) {
//            val x = element.bids.get(element.bids.size - 1)
//            mBids.add(
//                BidHistoryBody(
//                    x.amount,
//                    x._id,
//                    x.timestamp,
//                    element.bidder.name,
//                    element.bidder.phone,
//                    element.bidder.address
//                )
//            )
//        }

        mBids.sortByDescending { it.amount }
        Log.e(TAG, mBids.toString())

        adapter.lst = mBids
        rv.adapter = adapter
    }

    private fun createBidDialog() {

        d = androidx.appcompat.app.AlertDialog.Builder(this@OpenNewRequirementFragment)
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
        findViewById<ProgressBar>(R.id.pb_new_req_open).visibility = View.VISIBLE
        //Place bid and male call
        val service = RetrofitClient.makeCallsForBids(this@OpenNewRequirementFragment)
        bidObject.bid = bidValue

        Log.e("OPEN REQ", " Bid object " + bidObject)

        service.getFarmerAddBid(
            bidObject
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
                            this@OpenNewRequirementFragment,
                            container_open_new_req
                        )

                    }
                } else {
                    createSnackbar(
                        response.body()?.msg,
                        this@OpenNewRequirementFragment,
                        container_open_new_req
                    )
                }

            }

            override fun onFailure(call: Call<AddBidResponse>, t: Throwable) {
                createSnackbar(
                    ExternalUtils.returnStateMessageForThrowable(t),
                    this@OpenNewRequirementFragment,
                    container_open_new_req
                )
            }
        })


    }

    private fun manageConfirmAddBid() {
        findViewById<ProgressBar>(R.id.pb_new_req_open).visibility = View.GONE
        createToast(
            resources.getString(R.string.successfullBidAdd),
            this,
            container_open_new_req
        )
        onDestroy()

    }

    private fun viewBidHistory() {
    }


    override fun viewBidDetails(_listItem: BidHistoryBody) {

    }

}
