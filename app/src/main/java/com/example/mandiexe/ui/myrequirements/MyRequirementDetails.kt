package com.example.mandiexe.ui.myrequirements

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
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
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.TimeConversionUtils.convertTimeToEpoch
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.viewmodels.MyRequirementDetailsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.my_crop_bid_details_fragment.*
import kotlinx.android.synthetic.main.my_requirement_details_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class MyRequirementDetails : AppCompatActivity(), OnBidHistoryClickListener {

    companion object {
        fun newInstance() = MyRequirementDetails()
    }

    private val viewModel: MyRequirementDetailsViewModel by viewModels()

    private lateinit var args: Bundle

    private var BID_ID = ""
    private val TAG = MyRequirementDetails::class.java.simpleName

    //Modify dialog
    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog
    private lateinit var graph: GraphView
    private var from = ""
    private var isOpen = false

    private var sdf = SimpleDateFormat("dd-MM-yy HH:mm")

    private var ownerPhone = ""
    private var ownerName = ""
    private var numberOfBids = 0

    private lateinit var adapter: MyBidHistoryAdapter

    private val pref = PreferenceUtil

    private lateinit var pb: ProgressBar

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
        graph = findViewById(R.id.graphViewReq)
        pb = findViewById(R.id.pb_my_req_details)

        //Set views from 'FROM'
        if (from == RequirementFragment::class.java.simpleName) {
            //Do nothing
            this.apply {

                mtb_add_bid.visibility = View.GONE
                mtb_cancel_bid.visibility = View.VISIBLE
                mtb_Modify_bid.visibility = View.VISIBLE
                mMyBid.visibility =
                    View.VISIBLE
                tv_requirement_detail_my_bid.visibility = View.VISIBLE


            }

        } else if (from == AddRequirement::class.java.simpleName) {
            mtb_add_bid.visibility = View.VISIBLE
            mtb_cancel_bid.visibility = View.GONE
            mtb_Modify_bid.visibility = View.GONE
            mMyBid.visibility =
                View.GONE
            tv_requirement_detail_my_bid.visibility = View.GONE


        }


        makeCall()


        //initViews
        this.apply {
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

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun makeCall() {

        showProgress(pb, this)

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


        hideProgress(pb, this)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createModifyBidDialog() {

        d = androidx.appcompat.app.AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.layout_modify_bid, null)
        d.setView(v)

        val et = v.findViewById<EditText>(R.id.actvEditBid_price)
        val til = v.findViewById<TextInputLayout>(R.id.tilEditBidOfferPrice)





        d.setPositiveButton(resources.getString(R.string.modifyBid)) { x, _ ->

            if (et.text.isEmpty()) {
                til.error = resources.getString(R.string.offerPriceError)
            } else {
                confirmModify(et.text.toString())
            }
            x.dismiss()


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

        findViewById<ConstraintLayout>(R.id.mLayoutReq).visibility = View.VISIBLE
     

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
            //Set in the Panel
            this.apply{
                tv_req_details_buyer_name.setText((ownerName))
            }



            findViewById<TextView>(R.id.ans_detail_bid_quanity).text =
                value.bid.demand.qty.toString()
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
            createGraph(value.bid.demand.lastBid)

        } catch (e: Exception) {
            Log.e(TAG, "Error" + e.cause + e.message)
        } 
        
        hideProgress(pb,this)
    }

    private fun createGraph(value: List<ViewBidResponse.Bid.Demand.LastBid>) {

        val mList = value.toMutableList()
        numberOfBids = 0
        if (value.isEmpty()) {
            //When there are no last bids or any bids
            graph.removeAllSeries()
            graph.visibility = View.GONE
            findViewById<TextView>(R.id.tvNoGraphCrop).visibility = View.VISIBLE

            //Also, make this open
            openBidHistory()

            //During inactive, it will show an indefinite snackbar


        } else {


            numberOfBids = 0
            val series: LineGraphSeries<DataPoint> =
                LineGraphSeries<DataPoint>(getSeriesPoints(mList))


            graph.addSeries(series)


            // graph.gridLabelRenderer.horizontalAxisTitle = resources.getString(R.string.time)
            // graph.gridLabelRenderer.verticalAxisTitle = resources.getString(R.string.price_rs)

            graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                override fun formatLabel(value: Double, isValueX: Boolean): String {

                    if (isValueX) {
                        return sdf.format(Date(value.toLong()))
                    }
                    return super.formatLabel(value, isValueX)

                }

            }

            //Enable scrolling and zooming
            graph.viewport.isScalable = true
            graph.viewport.setScalableY(true)
            graph.gridLabelRenderer.numHorizontalLabels = numberOfBids
            graph.gridLabelRenderer.labelsSpace = 20


            Log.e(TAG, numberOfBids.toString() + "Number ")
            graph.gridLabelRenderer.setHorizontalLabelsAngle(90)

            // set manual x bounds to have nice steps

            if (!value.isEmpty()) {
                Log.e(
                    TAG,
                    "Start X is " + TimeConversionUtils.convertDateTimestampUtil(value.get(0).timestamp)
                        .toString()
                )

                TimeConversionUtils.convertDateTimestampUtil(value.get(0).timestamp)?.time?.toDouble()
                    ?.let {
                        graph.getViewport().setMinX(
                            it
                        )
                    };
                graph.viewport.isXAxisBoundsManual = true
            }

            graph.title = resources.getString(R.string.myBidHistory)
            graph.titleColor = Color.BLACK

            graph.gridLabelRenderer.labelHorizontalHeight = 300
        }
        numberOfBids = 0
    }

    private fun getSeriesPoints(item: MutableList<ViewBidResponse.Bid.Demand.LastBid>): Array<DataPoint>? {

        val arrayDataPoints: MutableList<DataPoint> = mutableListOf()


        for (element in item) {

            numberOfBids++

            arrayDataPoints.add(
                DataPoint(
                    TimeConversionUtils.convertDateTimestampUtil(element.timestamp),
                    element.amount.toDouble()
                )
            )

            Log.e(
                TAG,
                "In get series data point " + TimeConversionUtils.convertDateTimestampUtil(element.timestamp)
                    .toString() + " Amount : " + element.amount.toDouble() + " Num" + numberOfBids.toString()
            )
        }



        return arrayDataPoints.toTypedArray()

    }
    
    private fun fillRecyclerView(valueBids: ViewBidResponse.Bid) {

        val rv = findViewById<RecyclerView>(R.id.rv_bidHistoryMyReq)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = MyBidHistoryAdapter(this)

        //Create list
        val mBids: MutableList<BidHistoryBody> = mutableListOf()

        for (element in valueBids.bids) {

            val x = element
            val mBidder = valueBids.bidder
            mBids.add(
                BidHistoryBody(
                    x.amount,
                    x._id,
                    x.timestamp,
                    mBidder.name,
                    mBidder.phone,
                    mBidder.address
                )
            )
        }

        mBids.sortByDescending { it.amount }
        Log.e(TAG, mBids.toString())

        adapter.lst = mBids
        rv.adapter = adapter

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

    }

}
