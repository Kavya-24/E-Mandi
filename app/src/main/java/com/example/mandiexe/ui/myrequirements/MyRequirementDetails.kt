package com.example.mandiexe.ui.myrequirements

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MyBidHistoryAdapter
import com.example.mandiexe.adapter.OnBidHistoryClickListener
import com.example.mandiexe.models.body.BidHistoryBody
import com.example.mandiexe.models.body.bid.DeletBidBody
import com.example.mandiexe.models.body.bid.UpdateBidBody
import com.example.mandiexe.models.body.bid.ViewBidBody
import com.example.mandiexe.models.responses.bids.DeleteBidResponse
import com.example.mandiexe.models.responses.bids.UpdateBidResponse
import com.example.mandiexe.models.responses.bids.ViewBidResponse
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.OfflineTranslate.transliterateToDefault
import com.example.mandiexe.utils.usables.TimeConversionUtils.convertTimeToEpoch
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.viewmodels.MyRequirementDetailsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.my_crop_bid_details_fragment.*
import kotlinx.android.synthetic.main.my_requirement_details_fragment.*

class MyRequirementDetails : AppCompatActivity(), OnBidHistoryClickListener {

    companion object {
        fun newInstance() = MyRequirementDetails()
    }

    private val viewModel: MyRequirementDetailsViewModel by viewModels()

    //private lateinit var root: View
    private lateinit var args: Bundle

    private var BID_ID = ""
    private val TAG = MyRequirementDetails::class.java.simpleName

    //Modify dialog
    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog

    private var from = ""
    private var isOpen = false


    private var ownerPhone = ""
    private var ownerName = ""

    private lateinit var adapter: MyBidHistoryAdapter

    private val pref = PreferenceUtil


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(pref.getLanguageFromPreference(), this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_requirement_details_fragment)

        args = intent?.getBundleExtra("bundle")!!
        //Set the address in the box trimmed
        BID_ID = args.getString("BID_ID").toString()

        from = args.getString("FROM").toString()

        Log.e(TAG, "BID ID  " + BID_ID + "from is $from")

        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = resources.getString(R.string.details)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }


        //Set views from 'FROM'
        if (from == RequirementFragment::class.java.simpleName) {
            //Do nothing
            findViewById<MaterialButton>(R.id.mtb_add_bid).visibility = View.GONE
            findViewById<MaterialButton>(R.id.mtb_cancel_bid).visibility = View.VISIBLE
            findViewById<MaterialButton>(R.id.mtb_Modify_bid).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_requirement_detail_my_bid_text_view).visibility =
                View.VISIBLE
            findViewById<TextView>(R.id.tv_requirement_detail_my_bid).visibility = View.VISIBLE

        } else if (from == AddRequirement::class.java.simpleName) {
            findViewById<MaterialButton>(R.id.mtb_add_bid).visibility = View.VISIBLE
            findViewById<MaterialButton>(R.id.mtb_cancel_bid).visibility = View.GONE
            findViewById<MaterialButton>(R.id.mtb_Modify_bid).visibility = View.GONE
            findViewById<TextView>(R.id.tv_requirement_detail_my_bid_text_view).visibility =
                View.GONE
            findViewById<TextView>(R.id.tv_requirement_detail_my_bid).visibility = View.GONE

        }


        makeCall()


        //initViews
        findViewById<TextView>(R.id.tv_view_bid_history_requirement).setOnClickListener {
            //Open the history
            if (!isOpen) {
                openBidHistory()
            }

        }

        findViewById<ImageView>(R.id.iv_dropdown_bid_history).setOnClickListener {

            //Open the history
            if (isOpen) {
                closeBidHistory()
            } else {
                openBidHistory()
            }

        }

        findViewById<MaterialButton>(R.id.mtb_cancel_bid).setOnClickListener {
            cancelBid()
        }


        findViewById<MaterialButton>(R.id.mtb_Modify_bid).setOnClickListener {

            //Send the Bid Uodate body : Modify Bid Body in the bundle
            //Check for bundle in the BIdFragment
            createModifyBidDialog()

        }

        findViewById<MaterialButton>(R.id.mtb_add_bid).setOnClickListener {
            addBid()
        }


        findViewById<ImageView>(R.id.iv_req_call_buyer).setOnClickListener {
            val i = Intent(Intent.ACTION_CALL, Uri.parse(ownerPhone))
            startActivity(i)
        }

        findViewById<SwipeRefreshLayout>(R.id.swl_detailsReq).setOnRefreshListener {
            makeCall()
            findViewById<SwipeRefreshLayout>(R.id.swl_detailsReq).isRefreshing = false
        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun makeCall() {

        findViewById<ProgressBar>(R.id.pb_my_req_details).visibility = View.VISIBLE

        val body = ViewBidBody(BID_ID)

        viewModel.getFunction(body).observe(this, Observer { mResponse ->
            val success = viewModel.successfulBid.value
            if (success != null) {
                if (!success) {
                    createSnackbar(
                        viewModel.messageCancel.value,
                        this,
                        container_req_details
                    )
                } else if (mResponse.msg == "Bid retrieved successfully.") {
                    mResponse.let { initViews(it) }
                }
            }
        })


        findViewById<ProgressBar>(R.id.pb_my_req_details).visibility = View.GONE

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createModifyBidDialog() {

        d = androidx.appcompat.app.AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.layout_modify_bid, null)
        d.setView(v)

        val et = v.findViewById<EditText>(R.id.actvEditBid_price)
        val til = v.findViewById<TextInputLayout>(R.id.tilEditBidOfferPrice)





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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun confirmModify(newBid: String) {

        tempRef.dismiss()

        val body = UpdateBidBody(BID_ID, newBid)

        viewModel.updateFunction(body).observe(this, Observer { mResponse ->
            //Check with the sucessful of it
            if (viewModel.successfulUpdate.value == false) {
                let {
                    createSnackbar(
                        viewModel.messageUpdate.value,
                        it, container_req_details
                    )
                }
            } else if (mResponse.msg == "Bid updated successfully.") {
                manageModifyResponses(mResponse)
            }
        })


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun manageModifyResponses(mResponse: UpdateBidResponse?) {
        Toast.makeText(
            this,
            resources.getString(R.string.bidUpdated),
            Toast.LENGTH_SHORT
        ).show()
        makeCall()
    }

    private fun cancelBid() {

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(resources.getString(R.string.cancelBid)).setMessage(R.string.doDeleteBid)
        dialog.setPositiveButton(resources.getString(R.string.cancelBid), { _, _ ->
            confirmCancel()
        })
        dialog.setNegativeButton(resources.getString(R.string.no), { _, _ ->

        })

        dialog.create()
        dialog.show()


    }

    private fun confirmCancel() {

        val body = DeletBidBody(BID_ID)

        viewModel.cancelFunction(body).observe(this, Observer { mResponse ->

            //Check with the sucessful of it
            if (viewModel.successfulCancel.value == false) {
                let {
                    createSnackbar(
                        viewModel.messageCancel.value,
                        it, container_req_details
                    )
                }
            } else if (mResponse.msg == "Bid deleted successfully.") {
                manageCancelResponses(mResponse)
            }
        })

    }

    private fun manageCancelResponses(mResponse: DeleteBidResponse) {

        Toast.makeText(
            this,
            resources.getString(R.string.BidDeleted),
            Toast.LENGTH_SHORT
        ).show()

        onBackPressed()
    }

    private fun addBid() {

    }

    private fun closeBidHistory() {
        isOpen = false

        findViewById<RecyclerView>(R.id.rv_bidHistoryMyReq).visibility = View.GONE
        findViewById<TextView>(R.id.tv_view_bid_history_requirement).text =
            resources.getString(R.string.view_bid_history)

        findViewById<ImageView>(R.id.iv_dropdown_bid_history)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_down))

    }

    private fun openBidHistory() {

        isOpen = true
        findViewById<RecyclerView>(R.id.rv_bidHistoryMyReq).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_view_bid_history_requirement).text =
            resources.getString(R.string.myBidHistory)

        findViewById<ImageView>(R.id.iv_dropdown_bid_history)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_top))

        if (adapter.lst.size == 0) {
            UIUtils.createSnackbar(
                resources.getString(R.string.emptyRV),
                this,
                container_crop_bids_details
            )
        }

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initViews(value: ViewBidResponse) {

        //Set the above 8 entities wrt root
        //remove pb
        //View the root
        findViewById<ConstraintLayout>(R.id.mLayoutReq).visibility = View.VISIBLE
        findViewById<ProgressBar>(R.id.pb_my_req_details).visibility = View.GONE


        try {


            //Translate
            OfflineTranslate.translateToDefault(
                this,
                value.bid.demand.crop,
                findViewById<TextView>(R.id.tv_requirement_detail_crop_name)
            )

            OfflineTranslate.translateToDefault(
                this,
                value.bid.demand.variety,
                findViewById<TextView>(R.id.tv_requirement_detail_crop_type)
            )

            OfflineTranslate.translateToDefault(
                this,
                value.bid.demand.description,
                findViewById<TextView>(R.id.tv_requirement_detail_crop_description)
            )


            //Transliterate
            findViewById<TextView>(R.id.tv_requirement_detail_crop_location).text =
                OfflineTranslate.transliterateToDefault(value.bid.bidder.address)

            //Owner Details
            ownerPhone = value.bid.bidder.phone
            ownerName = transliterateToDefault(value.bid.bidder.name)
            //Normal Stuff

            findViewById<TextView>(R.id.ans_detail_bid_quanity).text =
                value.bid.qty.toString()
            findViewById<TextView>(R.id.ans_detail_bid_exp).text =
                convertTimeToEpoch(value.bid.demand.expiry)
            findViewById<TextView>(R.id.ans_detail_init_date).text =
                convertTimeToEpoch(value.bid.demand.demandCreated)

            findViewById<TextView>(R.id.tv_requirement_detail_current_bid).text =
                value.bid.demand.lastBid.toString()
            findViewById<TextView>(R.id.tv_requirement_detail_initial_offer_price).text =
                value.bid.demand.offerPrice.toString()

            if (from == RequirementFragment::class.java.simpleName) {
                //Add in the bidding fragment
                findViewById<TextView>(R.id.tv_requirement_detail_my_bid).text =
                    value.bid.demand.currentBid.toString()
            }


            if (value.bid.demand.currentBid < value.bid.demand.offerPrice) {
                findViewById<TextView>(R.id.tv_requirement_detail_current_bid)
                    .setTextColor(resources.getColor(R.color.deltaRed))

            } else if (value.bid.demand.currentBid == value.bid.demand.offerPrice) {
                findViewById<TextView>(R.id.tv_requirement_detail_current_bid)
                    .setTextColor(resources.getColor(R.color.wildColor))
            }


            fillRecyclerView(value.bid)
            createGraph(value)

        } catch (e: Exception) {
            Log.e(TAG, "Error" + e.cause + e.message)
        }
    }

    private fun createGraph(value: ViewBidResponse) {

    }

    private fun fillRecyclerView(valueBids: ViewBidResponse.Bid) {

//        val rv = findViewById<RecyclerView>(R.id.rv_bidHistoryMyReq)
//        rv.layoutManager = LinearLayoutManager(this)
//        adapter = MyBidHistoryAdapter(this)
//
//        //Create list
//        val mBids: MutableList<BidHistoryBody> = mutableListOf()
//
//        for (element in valueBids.bids) {
//
//            val x = element
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
//
//        mBids.sortByDescending { it.amount }
//        Log.e(TAG, mBids.toString())
//
//        adapter.lst = mBids
//        rv.adapter = adapter

    }


    override fun onBackPressed() {

        //Load the bid
        viewModel.successfulBid.removeObservers(this)
        viewModel.successfulBid.value = null

        //Successfully cancel the bid
        viewModel.successfulCancel.removeObservers(this)
        viewModel.successfulCancel.value = null

        //Upate the bid
        viewModel.successfulUpdate.removeObservers(this)
        viewModel.successfulUpdate.value = null

        finish()
        super.onBackPressed()

    }

    override fun viewBidDetails(_listItem: BidHistoryBody) {
        TODO("Not yet implemented")
    }

}
