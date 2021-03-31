package com.example.mandiexe.ui.bids

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MyBidHistoryAdapter
import com.example.mandiexe.adapter.OnBidHistoryClickListener
import com.example.mandiexe.models.PersonObject
import com.example.mandiexe.models.body.BidHistoryBody
import com.example.mandiexe.models.body.bid.*
import com.example.mandiexe.models.responses.bids.DeleteBidResponse
import com.example.mandiexe.models.responses.bids.UpdateBidResponse
import com.example.mandiexe.models.responses.bids.ViewBidResponse
import com.example.mandiexe.models.responses.demand.ViewDemandResponse
import com.example.mandiexe.ui.demands.AddRequirement
import com.example.mandiexe.ui.home.FarmerBidHistory
import com.example.mandiexe.utils.PermissionsHelper
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
import com.example.mandiexe.viewmodels.BidDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.item_owner_detail.view.*
import kotlinx.android.synthetic.main.supply_detail_activity.*
import kotlinx.android.synthetic.main.bid_detail_activity.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class BidDetailActivity : AppCompatActivity(), OnBidHistoryClickListener {

    companion object {
        fun newInstance() = BidDetailActivity()
    }

    private val viewModel: BidDetailsViewModel by viewModels()

    private lateinit var args: Bundle

    private var BID_ID = ""
    private val TAG = BidDetailActivity::class.java.simpleName

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
    private var dialogCurrentBid = ""
    private lateinit var adapter: MyBidHistoryAdapter

    private val pref = PreferenceUtil
    private var mPrice = ""

    private lateinit var pb: ProgressBar

    private var personObject: PersonObject? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(pref.getLanguageFromPreference(), this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bid_detail_activity)

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
        if (from == MyBidsFragment::class.java.simpleName) {
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

            tv_view_bid_history_requirement.setOnClickListener {
                //Open the history
                if (!isOpen) {
                    openBidHistory()
                }

            }

            iv_dropdown_bid_history_req.setOnClickListener {

                //Open the history
                if (isOpen) {
                    closeBidHistory()
                } else {
                    openBidHistory()
                }

            }

            mtb_cancel_bid.setOnClickListener {
                cancelBid()
            }


            mtb_Modify_bid.setOnClickListener {

                //Send the Bid Uodate body : Modify Bid Body in the bundle
                //Check for bundle in the BIdFragment
                createModifyBidDialog()

            }

            mtb_add_bid.setOnClickListener {
                addBid()
            }


            iv_req_call_buyer.setOnClickListener {
                checkCallPermissions()

            }

            swl_detailsReq.setOnRefreshListener {
                makeCall()
                swl_detailsReq.isRefreshing = false
            }

            tv_req_details_buyer_name.setOnClickListener {
                viewBuyer()
            }

        }

    }

    private fun viewBuyer() {
        val dialog = AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.item_owner_detail, null)
        dialog.setView(v)

        if (personObject != null) {
            v.apply {
                this.ownerName.text = personObject!!.name
                this.ownerPhone.text = personObject!!.phone
                ownerAddress1.text = personObject!!.address
                ownerAddress2.text =
                    personObject!!.addressLine1 + "\n" + personObject!!.addressLine2

            }
        }

        dialog.setPositiveButton(resources.getString(R.string.call)) { dialog, _ ->
            checkCallPermissions()  //For calling
            dialog.dismiss()
        }
        dialog.setNegativeButton(resources.getString(R.string.ok)) { _, _ -> }


        dialog.create()
        dialog.show()
    }

    private fun checkCallPermissions() {

        PermissionsHelper.requestCallsAndMessagePermissions(this)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { callback ->
                    Log.e(TAG, "In on next and call back is $callback")
                    if (callback) {
                        //Done
                        callBuyer()
                    }
                },
                onError = {
                    Log.e(TAG, "Error for permissions and ${it.message} ${it.cause}")
                },
                onComplete = {
                    Log.e(TAG, "In complete")
                    callBuyer()

                }
            )

    }

    private fun callBuyer() {
        try {
            val i = Intent(Intent.ACTION_CALL, Uri.fromParts("tel", ownerPhone, null))
            startActivity(i)

        } catch (e: Exception) {
            Log.e(TAG, "In except" + e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun makeCall() {

        showProgress(pb, this)

        val body = ViewBidBody(BID_ID)

        if (from != AddRequirement::class.java.simpleName) {
            viewModel.getFunction(body).observe(this, Observer { mResponse ->
                val success = viewModel.successfulBid.value
                if (success != null) {
                    if (!success) {
                        createSnackbar(
                            viewModel.messageBid.value,
                            this,
                            container_req_details
                        )
                    } else if (mResponse.msg == "Bid retrieved successfully.") {
                        mResponse.let { initViews(it) }
                    }
                }
            })
        }

        if (from == AddRequirement::class.java.simpleName) {

            val body2 = ViewDemandBody(BID_ID)
            viewModel.getDemandFunction(body2).observe(this, Observer { mResponse ->
                val success = viewModel.successfulDemand.value
                if (success != null) {
                    if (!success) {
                        createSnackbar(
                            viewModel.messageDemand.value,
                            this,
                            container_req_details
                        )
                    } else if (mResponse.msg == "Demand retrieved successfully.") {
                        mResponse.let { initViewsForNewRequirement(it) }
                    }
                }
            })

        }



        hideProgress(pb, this)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initViewsForNewRequirement(value: ViewDemandResponse?) {

        findViewById<ConstraintLayout>(R.id.mLayoutReq).visibility = View.VISIBLE
        hideProgress(pb, this)

        try {
            if (value != null) {
                Log.e(TAG, "\nREsposne \nis $value")
                //Translate
                OfflineTranslate.translateToDefault(
                    this,
                    value.demand.crop,
                    findViewById<TextView>(R.id.tv_requirement_detail_crop_name)
                )

                OfflineTranslate.translateToDefault(
                    this,
                    value.demand.variety,
                    findViewById<TextView>(R.id.tv_requirement_detail_crop_type)
                )

                OfflineTranslate.translateToDefault(
                    this,
                    value.demand.description,
                    findViewById<TextView>(R.id.tv_requirement_detail_crop_description)
                )


                //Owner Details
                ownerPhone = value.demand.demander.phone
                ownerName = transliterateToDefault(value.demand.demander.name)


                val currrentBid = value.demand.currentBid
                val initialOfferPrice = value.demand.offerPrice


                Log.e(
                    TAG,
                    "Current is $currrentBid and init is $initialOfferPrice"
                )

                this.apply {

                    tv_req_details_buyer_name.setText((ownerName))
                    tv_requirement_detail_crop_location.text =
                        OfflineTranslate.transliterateToDefault(value.demand.demander.address)

                    ans_detail_bid_quanity.text =
                        value.demand.qty.toString()

                    ans_detail_bid_exp.text =
                        convertTimeToEpoch(value.demand.expiry)
                    this.ans_detail_bid_init_date.text =
                        convertTimeToEpoch(value.demand.demandCreated)

                    tv_requirement_detail_current_bid.setText(
                        currrentBid.toString()
                    )

                    tv_requirement_detail_initial_offer_price.text =
                        initialOfferPrice.toString()

                    if (currrentBid > initialOfferPrice) {
                        tv_requirement_detail_current_bid.setTextColor(
                            resources.getColor(
                                R.color.deltaRed,
                                null
                            )
                        )

                    }
                    //Add in the bidding fragment
                    tv_requirement_detail_my_bid.visibility = View.GONE
                    this.mMyBid.visibility = View.GONE


                }

                //Popultae Person object
                val bidDemander = value.demand.demander
                val address1 = bidDemander.village + "," + bidDemander.district
                val address2 = bidDemander.state + "," + bidDemander.country
                personObject = PersonObject(
                    bidDemander.name,
                    bidDemander.phone,
                    bidDemander.address,
                    address1,
                    address2
                )



                dialogCurrentBid = currrentBid.toString()

                //Hide
                //fillRecyclerView(value.demand.bids)
                //createGraph(value.bid.demand.lastBid)


                this.apply {

                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error" + e.cause + e.message)
        }

        hideProgress(pb, this)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createModifyBidDialog() {

        val dialog = AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.layout_modify_bid, null)

        val proceView = v.findViewById<EditText>(R.id.numberPickerModifyPrice)
        proceView.setHint(mPrice)
        proceView.setText(mPrice, TextView.BufferType.EDITABLE)

        val tv = v.findViewById<TextView>(R.id.tvIncDec)!!

        proceView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(progress: Editable?) {
                Log.e(TAG, "In on orogress changed now $progress")
                if (progress.isNullOrEmpty()) {
                    //Do nothing
                    tv.text = resources.getString(R.string.updateBid)
                    tv.setTextColor(resources.getColor(R.color.bgGreen, null))
                } else if (progress.toString().toInt() == mPrice.toInt()) {
                    tv.text = resources.getString(R.string.noChangeInBid)
                    tv.setTextColor(resources.getColor(R.color.wildColor, null))
                } else if (progress.toString().toInt() > mPrice.toInt()) {
                    tv.text = resources.getString(R.string.incDec, mPrice, progress.toString())
                    tv.setTextColor(resources.getColor(R.color.deltaGreen, null))
                } else if (progress.toString().toInt() < mPrice.toInt()) {
                    tv.text = resources.getString(R.string.decreasing, mPrice, progress.toString())
                    tv.setTextColor(resources.getColor(R.color.deltaRed, null))
                }


            }
        })





        dialog.setView(v)
        dialog.setPositiveButton(resources.getString(R.string.updateBid)) { x, y ->
            confirmModify(proceView.text.toString())
            x.dismiss()

        }
        dialog.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        dialog.show()
        dialog.create()


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
        dialog.setPositiveButton(resources.getString(R.string.cancelBid)) { _, _ ->
            confirmCancel()
        }
        dialog.setNegativeButton(resources.getString(R.string.no)) { _, _ ->

        }

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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addBid() {

        val d = androidx.appcompat.app.AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.layout_add_bid, null)
        d.setView(v)

        val et = v.findViewById<EditText>(R.id.actvEditBid_priceNew)
        val til = v.findViewById<TextInputLayout>(R.id.tilEditBidOfferPriceNew)
        val tv = v.findViewById<TextView>(R.id.tv_add_bid_current_bid)


        tv.text = dialogCurrentBid

        d.setPositiveButton(resources.getString(R.string.modifyBid)) { dialog, _ ->

            if (et.text.isEmpty()) {
                til.error = resources.getString(R.string.offerPriceError)
            } else {
                confirmAddBid(et.text.toString())
            }
            dialog.dismiss()

        }
        d.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

        }

        d.create()
        d.show()


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun confirmAddBid(valueOfBidPrice: String) {

        val body = AddBidBody(valueOfBidPrice, BID_ID)
        showProgress(pb, this)
        viewModel.addFunction(body).observe(this, Observer { mResponse ->
            if (mResponse.msg == "Bid added successfully.") {
                createSnackbar(resources.getString(R.string.bidAdded), this, container_req_details)
                makeCall()
                hideProgress(pb, this)

            }

        })

        hideProgress(pb, this)
    }

    private fun closeBidHistory() {
        isOpen = false

        findViewById<RecyclerView>(R.id.rv_bidHistoryMyReq).visibility = View.GONE
        findViewById<TextView>(R.id.tv_view_bid_history_requirement).text =
            resources.getString(R.string.view_bid_history)

        findViewById<ImageView>(R.id.iv_dropdown_bid_history_req)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_down))

    }

    private fun openBidHistory() {

        isOpen = true
        findViewById<RecyclerView>(R.id.rv_bidHistoryMyReq).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_view_bid_history_requirement).text =
            resources.getString(R.string.myBidHistory)

        findViewById<ImageView>(R.id.iv_dropdown_bid_history_req)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_top))

        if (adapter.lst.isEmpty()) {
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
        hideProgress(pb, this)

        try {

            Log.e(TAG, "\nREsposne \nis $value")
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


            //Owner Details
            ownerPhone = value.bid.bidder.phone
            ownerName = transliterateToDefault(value.bid.bidder.name)
            mPrice = value.bid.currentBid.toString()

            val currrentBid = value.bid.demand.currentBid
            val initialOfferPrice = value.bid.demand.offerPrice
            val myCurrentBid = value.bid.currentBid

            Log.e(
                TAG,
                "Current is $currrentBid and init is $initialOfferPrice amd my $myCurrentBid"
            )

            this.apply {

                tv_req_details_buyer_name.setText((ownerName))
                tv_requirement_detail_crop_location.text =
                    OfflineTranslate.transliterateToDefault(value.bid.bidder.address)

                ans_detail_bid_quanity.text =
                    value.bid.demand.qty.toString()

                ans_detail_bid_exp.text =
                    convertTimeToEpoch(value.bid.demand.expiry)
                this.ans_detail_bid_init_date.text =
                    convertTimeToEpoch(value.bid.demand.demandCreated)

                tv_requirement_detail_current_bid.setText(
                    currrentBid.toString()
                )

                tv_requirement_detail_initial_offer_price.text =
                    initialOfferPrice.toString()

                if (currrentBid > initialOfferPrice) {
                    tv_requirement_detail_current_bid.setTextColor(
                        resources.getColor(
                            R.color.deltaRed,
                            null
                        )
                    )

                }
                if (from == MyBidsFragment::class.java.simpleName) {
                    //Add in the bidding fragment
                    tv_requirement_detail_my_bid.text =
                        myCurrentBid.toString()

                    if (myCurrentBid > initialOfferPrice) {
                        tv_requirement_detail_my_bid.setTextColor(
                            resources.getColor(
                                R.color.deltaRed,
                                null
                            )
                        )
                    }

                }

            }

            //Popultae Person object
            val bidDemander = value.bid.bidder
            val address1 = bidDemander.village + "," + bidDemander.district
            val address2 = bidDemander.state + "," + bidDemander.country
            personObject = PersonObject(
                bidDemander.name,
                bidDemander.phone,
                bidDemander.address,
                address1,
                address2
            )


            //Populate the views for when coming from the Bid Hiostiory
            if (from == FarmerBidHistory::class.java.simpleName) {

                //If the bid is not active, hide some views and create indefinite snackbar
                if (!value.bid.active) {
                    this.apply {
                        mtb_Modify_bid.visibility = View.GONE
                        mtb_cancel_bid.visibility = View.GONE
                        Snackbar.make(
                            container_req_details,
                            resources.getString(R.string.bidInactive),
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                }

            }


            dialogCurrentBid = currrentBid.toString()
            fillRecyclerView(value.bid)
            createGraph(value.bid.demand.lastBid)

        } catch (e: Exception) {
            Log.e(TAG, "Error" + e.cause + e.message)
        }

        hideProgress(pb, this)
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

        //Add observors
        viewModel.successfulAdd.removeObservers(this)
        viewModel.successfulAdd.value = null


        finish()
        super.onBackPressed()

    }

    override fun viewBidDetails(_listItem: BidHistoryBody) {

    }

}
