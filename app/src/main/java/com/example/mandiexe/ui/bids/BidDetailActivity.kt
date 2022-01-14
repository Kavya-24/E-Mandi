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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import com.example.mandiexe.ui.demands.NewDemandActivity
import com.example.mandiexe.ui.home.FarmerBidHistory
import com.example.mandiexe.utils.PermissionsHelper
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.OfflineTranslate.transliterateToDefault
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.TimeConversionUtils.convertTimeToEpoch
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.createToast
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
import kotlinx.android.synthetic.main.bid_detail_activity.*
import kotlinx.android.synthetic.main.item_owner_detail.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
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
    private lateinit var graph: GraphView
    private var from = ""
    private var isOpen = false

    private var sdf = SimpleDateFormat("dd-MM-yy")

    private var ownerPhone = ""
    private var ownerName = ""
    private var numberOfBids = 0
    private var dialogCurrentBid = ""
    private lateinit var adapter: MyBidHistoryAdapter

    private val pref = PreferenceUtil
    private var mPrice = ""

    private lateinit var pb: ProgressBar
    private lateinit var mSnackbarView: CoordinatorLayout

    private var personObject: PersonObject? = null

    private lateinit var swl: SwipeRefreshLayout
    private var bid_date_created = ""
    private var bid_amount_created = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(pref.getLanguageFromPreference(), this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bid_detail_activity)

        args = intent?.getBundleExtra("bundle")!!


        BID_ID = args.getString("BID_ID").toString()
        from = args.getString("FROM").toString()

        Log.e(TAG, "BID ID  " + BID_ID + "from is $from")

        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = resources.getString(R.string.details)
        this.apply {
            tvTitleToolbar.text = resources.getString(R.string.details)
        }
        tb.setNavigationOnClickListener {
            onBackPressed()
        }

        //Initialization
        graph = findViewById(R.id.graphViewReq)
        pb = findViewById(R.id.pb_my_req_details)
        swl = findViewById(R.id.swl_detailsReq)
        mSnackbarView = findViewById(R.id.container_bid_detail)

        //Set views from 'FROM'
        if (from == MyBidsFragment::class.java.simpleName || from == FarmerBidHistory::class.java.simpleName) {

            this.apply {

                mtb_add_bid.visibility = View.GONE
                mtb_cancel_bid.visibility = View.VISIBLE
                mtb_Modify_bid.visibility = View.VISIBLE
                mMyBid.visibility =
                    View.VISIBLE

                tv_requirement_detail_my_bid.visibility = View.VISIBLE
                tv_view_bid_history_requirement.visibility = View.VISIBLE
                iv_dropdown_bid_history_req.visibility = View.VISIBLE

                graphViewReq.visibility = View.VISIBLE
                tvNoGraph.visibility = View.VISIBLE


            }

        } else if (from == NewDemandActivity::class.java.simpleName) {

            this.apply {
                mtb_add_bid.visibility = View.VISIBLE
                mtb_cancel_bid.visibility = View.GONE
                mtb_Modify_bid.visibility = View.GONE
                mMyBid.visibility =
                    View.GONE

                tv_requirement_detail_my_bid.visibility = View.GONE
                //Hide the view bid history, graph, arrow
                tv_view_bid_history_requirement.visibility = View.GONE
                iv_dropdown_bid_history_req.visibility = View.GONE

                graphViewReq.visibility = View.GONE
                tvNoGraph.visibility = View.GONE
            }
        }


        swl.isRefreshing = true
        makeCall()
        swl.isRefreshing = false

        swl.setOnRefreshListener {
            makeCall()
            swl_detailsReq.isRefreshing = false
        }


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


            mContact.setOnClickListener {
                viewBuyer()
            }

            ivInformation.setOnClickListener {
                getInformationNormalFilters()
            }

        }

    }

    private fun getInformationNormalFilters() {

        val d = androidx.appcompat.app.AlertDialog.Builder(this)
        d.setTitle(resources.getString(R.string.cropDetailtitle))
        if (from == NewDemandActivity::class.java.simpleName) {
            d.setMessage(resources.getString(R.string.bidDetailInfo))
        } else {
            d.setMessage(resources.getString(R.string.bidDetailInfoMy))
        }
        d.setPositiveButton(resources.getString(R.string.ok)) { _, _ -> }
        d.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun makeCall() {

        showProgress(pb, this@BidDetailActivity)



        if (from != NewDemandActivity::class.java.simpleName) {
            val body = ViewBidBody(BID_ID)
            viewModel.viewBidFunction(body, pb, mSnackbarView).observe(this, Observer { mResponse ->
                val success = viewModel.successfulBid.value
                if (success != null) {
                    hideProgress(pb, this)
                    mResponse.let { initViews(it) }

                } else {
                    showProgress(pb, this)
                }
            })
        } else if (from == NewDemandActivity::class.java.simpleName) {

            val body2 = ViewDemandBody(BID_ID)
            viewModel.viewDemandFunction(body2, pb, mSnackbarView)
                .observe(this, Observer { mResponse ->
                    val success = viewModel.successfulDemand.value
                    if (success != null) {
                        hideProgress(pb, this)
                        mResponse.let { initViewsForNewRequirement(it) }

                    } else {
                        showProgress(pb, this)
                    }
                })


        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initViewsForNewRequirement(value: ViewDemandResponse?) {

        findViewById<ConstraintLayout>(R.id.mLayoutReq).visibility = View.VISIBLE
        hideProgress(pb, this)

        try {
            if (value != null) {

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

                bid_amount_created = value.demand.offerPrice.toString()
                bid_date_created = value.demand.demandCreated


                val currrentBid = value.demand.currentBid
                val initialOfferPrice = value.demand.offerPrice


                Log.e(
                    TAG,
                    "Current is $currrentBid and init is $initialOfferPrice"
                )

                //Owner Details
                ownerPhone = value.demand.demander.phone
                ownerName = transliterateToDefault(value.demand.demander.name)

                val bidDemander = value.demand.demander
                val address1 = bidDemander.village + "," + bidDemander.district
                val address2 = bidDemander.state + "," + bidDemander.country

                //#DEMANDER
                personObject = PersonObject(
                    bidDemander.name,
                    bidDemander.phone,
                    bidDemander.address,
                    address1,
                    address2
                )


                this.apply {


                    tv_contact_name.text = ownerName
                    tv_contact_initial.text = ownerName.take(1)
                    tv_requirement_detail_crop_location.text =
                        OfflineTranslate.transliterateToDefault(value.demand.demander.address)

                    ans_detail_bid_quanity.text =
                        resources.getString(
                            R.string.qkg,
                            value.demand.qty.toString(),
                            resources.getString(R.string.kg)
                        )

                    ans_detail_bid_exp.text =
                        resources.getString(
                            R.string.bidEndsOn,
                            convertTimeToEpoch(value.demand.expiry)
                        )
                    ans_detail_bid_init_date.text =
                        convertTimeToEpoch(value.demand.demandCreated)

                    tv_requirement_detail_current_bid.text = currrentBid.toString()

                    tv_requirement_detail_initial_offer_price.text =
                        initialOfferPrice.toString()

                    tvLastUpdatedBidDetail.text = resources.getString(
                        R.string.lastupdated,
                        convertTimeToEpoch(value.demand.lastModified)
                    )

                    tvLastUpdatedBidDetail.visibility = View.GONE
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
                    mMyBid.visibility = View.GONE


                }


                dialogCurrentBid = currrentBid.toString()


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
        proceView.hint = mPrice
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
            x.dismiss()
            confirmModify(proceView.text.toString())


        }
        dialog.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        dialog.show()
        dialog.create()


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun confirmModify(newBid: String) {


        val body = UpdateBidBody(BID_ID, newBid)

        viewModel.updateFunction(body, pb, mSnackbarView).observe(this, Observer { mResponse ->
            //Check with the sucessful of it
            val sucess = viewModel.successfulUpdate.value
            if (sucess != null) {
                hideProgress(pb, this)
                manageModifyResponses(mResponse)
            } else {
                showProgress(pb, this)
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

        viewModel.deleteFunction(body, pb, mSnackbarView).observe(this, Observer { mResponse ->

            //Check with the sucessful of it
            if (viewModel.successfulCancel.value != null) {
                hideProgress(pb, this)

                manageCancelResponses(mResponse)
            } else {
                showProgress(pb, this)
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


        tv.text = dialogCurrentBid + " " + resources.getString(R.string.rs)

        d.setPositiveButton(resources.getString(R.string.add)) { dialog, _ ->

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
        clearObservers()

        showProgress(pb, this)

        viewModel.addFunction(body, pb, mSnackbarView).observe(this, Observer { mResponse ->

            val success = viewModel.successfulAdd.value
            if (success != null) {
                hideProgress(pb, this)
                if (mResponse.msg == "Bid added successfully.") {
                    hideProgress(pb, this)
                    createToast(
                        resources.getString(R.string.bidAdded),
                        this,
                        container_req_details
                    )
                    onBackPressed()
                }
            } else {
                showProgress(pb, this)
            }


        })


    }


    private fun clearObservers() {
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

        viewModel.successfulDemand.removeObservers(this)
        viewModel.successfulDemand.value = null

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
                container_req_details
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initViews(value: ViewBidResponse) {

        findViewById<ConstraintLayout>(R.id.mLayoutReq).visibility = View.VISIBLE
        hideProgress(pb, this)
        Log.e(TAG, "In init views")
        try {

            Log.e(TAG, "\nResposne \nis $value")
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

            bid_amount_created = value.bid.demand.offerPrice.toString()
            bid_date_created = value.bid.demand.demandCreated.toString()

            val currrentBid = value.bid.demand.currentBid
            val initialOfferPrice = value.bid.demand.offerPrice
            val myCurrentBid = value.bid.currentBid

            Log.e(
                TAG,
                "Current is $currrentBid and init is $initialOfferPrice amd my $myCurrentBid"
            )

            this.apply {

                tv_contact_name.text = ownerName
                tv_contact_initial.text = ownerName.take(1)

                tv_requirement_detail_crop_location.text =
                    OfflineTranslate.transliterateToDefault(value.bid.bidder.address)

                ans_detail_bid_quanity.text = resources.getString(
                    R.string.qkg,
                    value.bid.demand.qty.toString(),
                    resources.getString(R.string.kg)
                )

                ans_detail_bid_exp.text =
                    resources.getString(
                        R.string.bidEndsOn,
                        convertTimeToEpoch(value.bid.demand.expiry)
                    )


                this.ans_detail_bid_init_date.text =
                    convertTimeToEpoch(value.bid.demand.demandCreated)

                tv_requirement_detail_current_bid.text = currrentBid.toString()

                tv_requirement_detail_initial_offer_price.text =
                    initialOfferPrice.toString()

                tvLastUpdatedBidDetail.text = resources.getString(
                    R.string.lastupdated,
                    convertTimeToEpoch(value.bid.demand.lastModified)
                )

                if (currrentBid > initialOfferPrice) {
                    tv_requirement_detail_current_bid.setTextColor(
                        resources.getColor(
                            R.color.deltaRed,
                            null
                        )
                    )

                }
                if (from == MyBidsFragment::class.java.simpleName || from == FarmerBidHistory::class.java.simpleName) {
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
            val address1 = bidDemander.village + ", " + bidDemander.district
            val address2 = bidDemander.state + ", " + bidDemander.country

            //#Demander
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

            Log.e(TAG, "The bids are ${value.bid.bids}")
            fillRecyclerView(value.bid)
            createGraph(
                value.bid.demand.lastBid,
                value.bid.currentBid,
                value.bid.lastModified,
                value.bid.bids
            )


        } catch (e: Exception) {
            Log.e(TAG, "Error" + e.cause + e.message)
        }

        hideProgress(pb, this)
    }

    private fun createGraph(
        value: List<ViewBidResponse.Bid.Demand.LastBid>, currentBid: Int,
        lastModified: String,
        bids: List<ViewBidResponse.Bid.BidDetails>
    ) {

        val mList = value.toMutableList()
        numberOfBids = 0
        graph.removeAllSeries()


        if (value.isEmpty()) {
            //When there are no last bids or any bids
            graph.removeAllSeries()
            graph.visibility = View.GONE
            findViewById<TextView>(R.id.tvNoGraphCrop).visibility = View.VISIBLE

            //Also, make this open
            openBidHistory()

            //During inactive, it will show an indefinite snackbar


        } else {

            //Add the current last bid
            graph.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvNoGraph).visibility = View.GONE
            //#This last modifed is wrong, it can be for the the owner also
            mList.add(
                ViewBidResponse.Bid.Demand.LastBid(
                    bids.get(0).amount,
                    bids.get(0)._id,
                    bids.get(0).timestamp
                )
            )
            mList.add(
                ViewBidResponse.Bid.Demand.LastBid(
                    bid_amount_created.toInt(),
                    "",
                    bid_date_created,
                )
            )
            mList.sortBy { it.timestamp }

            numberOfBids = 0
            val series: LineGraphSeries<DataPoint> =
                LineGraphSeries<DataPoint>(getSeriesPoints(mList))


            graph.addSeries(series)


            graph.addSeries(series)


            // graph.gridLabelRenderer.horizontalAxisTitle = resources.getString(R.string.time)
            // graph.gridLabelRenderer.verticalAxisTitle = resources.getString(R.string.price_rs)

            graph.gridLabelRenderer.numHorizontalLabels = 3 // only 4 because of the space
            graph.viewport.isXAxisBoundsManual = true


            graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                override fun formatLabel(value: Double, isValueX: Boolean): String {

                    if (isValueX) {
                        return sdf.format(Date(value.toLong()))
                    }
                    return super.formatLabel(value, isValueX)

                }

            }




            graph.viewport.isScrollable = true // enables horizontal scrolling
            graph.gridLabelRenderer.setHumanRounding(false)

            graph.gridLabelRenderer.numHorizontalLabels = 3
            graph.gridLabelRenderer.labelsSpace = 20



            Log.e(TAG, numberOfBids.toString() + "Number ")

            if (!value.isEmpty()) {

                //Start x will be the initial price of the item


                TimeConversionUtils.convertDateTimestampUtil(bid_date_created)?.time?.toDouble()
                    ?.let {
                        graph.viewport.setMinX(
                            it
                        )
                    }


                graph.viewport.isXAxisBoundsManual = true
            }

            graph.title = resources.getString(R.string.myBidHistory)
            graph.titleColor = Color.BLACK

            series.isDrawDataPoints = true
            series.dataPointsRadius = 6f

            //graph.gridLabelRenderer.labelHorizontalHeight = 300

        }
        numberOfBids = 0
    }

    private fun getSeriesPoints(item: MutableList<ViewBidResponse.Bid.Demand.LastBid>): Array<DataPoint>? {

        val arrayDataPoints: MutableList<DataPoint> = mutableListOf()


        for (element in item) {

            if (element.amount > 0) {
                numberOfBids++

                arrayDataPoints.add(
                    DataPoint(
                        TimeConversionUtils.convertDateTimestampUtil(element.timestamp),
                        element.amount.toDouble()
                    )
                )

                Log.e(
                    TAG,
                    "In get series data point " + TimeConversionUtils.convertDateTimestampUtil(
                        element.timestamp
                    )
                        .toString() + " Amount : " + element.amount.toDouble() + " Num" + numberOfBids.toString()
                )
            }
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
        clearObservers()
        finish()
        super.onBackPressed()

    }

    override fun viewBidDetails(_listItem: BidHistoryBody) {

    }

    //Call buyer
    private fun viewBuyer() {
        val dialog = AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.item_owner_detail, null)
        dialog.setView(v)

        if (personObject != null) {
            v.apply {
                this.ownerName.text = personObject!!.name
                this.ownerPhone.text = personObject!!.phone

                //ownerAddress1.text = personObject!!.address
                //Remove it
                ownerAddress1.text =
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

}
