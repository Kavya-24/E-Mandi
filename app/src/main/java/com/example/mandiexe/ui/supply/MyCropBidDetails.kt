package com.example.mandiexe.ui.supply

import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.example.mandiexe.models.body.supply.DeleteSupplyBody
import com.example.mandiexe.models.body.supply.ModifySupplyBody
import com.example.mandiexe.models.body.supply.ViewSupplyBody
import com.example.mandiexe.models.responses.supply.DeleteSupplyResponse
import com.example.mandiexe.models.responses.supply.ModifySupplyResponse
import com.example.mandiexe.models.responses.supply.ViewSupplyResponse
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.MyCropBidDetailsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.my_crop_bid_details_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class MyCropBidDetails : AppCompatActivity(), OnBidHistoryClickListener {

    companion object {
        fun newInstance() = MyCropBidDetails()
    }

    private val viewModelCrop: MyCropBidDetailsViewModel by viewModels()
    private lateinit var graph: GraphView
    private lateinit var args: Bundle
    private val mHandler = Handler()


    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog


    private val myCalendar = Calendar.getInstance()
    private lateinit var adapter: MyBidHistoryAdapter

    private var SUPPLY_ID = ""
    private var from = ""
    private val TAG = MyCropBidDetails::class.java.simpleName
    private var modifyBody = ModifySupplyBody.Update(0, "", "", "", "")

    private var mPrice = ""
    private var mCrop = ""
    private var sdf = SimpleDateFormat("dd-MM-yy HH:mm")
    private var numberOfBid = 0
    private var isOpen = false


    private lateinit var tilType: TextInputLayout
    private lateinit var tilPrice: TextInputLayout
    private lateinit var tilEst: TextInputLayout
    private lateinit var tilExp: TextInputLayout
    private lateinit var tilDesc: TextInputLayout

    private lateinit var etEst: EditText
    private lateinit var offerPrice: EditText
    private lateinit var cropType: EditText
    private lateinit var etExp: EditText
    private lateinit var desc: EditText
    private lateinit var v: View

    private val pref = com.example.mandiexe.utils.auth.PreferenceUtil
    private lateinit var pb : ProgressBar
    
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(pref.getLanguageFromPreference(), this)
        setContentView(R.layout.my_crop_bid_details_fragment)

        //  lineChart = findViewById(R.id.lineChart)
        graph = findViewById(R.id.graphView)

        args = intent?.getBundleExtra("bundle")!!
        //Set the address in the box trimmed
        SUPPLY_ID = args.getString("SUPPLY_ID").toString()
        from = args.getString("FROM").toString()

        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = resources.getString(R.string.details)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }

        Log.e(TAG, "Supply id is" + SUPPLY_ID + "\nFrom " + from)
        
        pb = findViewById(R.id.pb_my_crops_details)
        //This gets an id as the argument and makes a call from it
        makeCall()


        //initViews
        findViewById<TextView>(R.id.tv_view_bid_history_stocks).setOnClickListener {

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



        findViewById<MaterialButton>(R.id.mtb_cancel_stock).setOnClickListener {
            cancelStock()
        }



        findViewById<MaterialButton>(R.id.mtb_modify_stock).setOnClickListener {
            modifyStock()
        }


        findViewById<SwipeRefreshLayout>(R.id.swl_details).setOnRefreshListener {
            makeCall()
            findViewById<SwipeRefreshLayout>(R.id.swl_details).isRefreshing = false
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun makeCall() {

        showProgress(pb, this)
        Log.e(TAG, SUPPLY_ID + " is supply id")
        val body = ViewSupplyBody(SUPPLY_ID)
        //Clear graph series
        graph.removeAllSeries()
        graph.clearSecondScale()
        graph.clearAnimation()
        numberOfBid = 0



        viewModelCrop.getFunction(body).observe(this, Observer { mResponse ->
            if (viewModelCrop.successfulSupply.value != null) {
                if (viewModelCrop.successfulSupply.value!! || viewModelCrop.messageSupply.value == "Supply retrieved successfully.") {
                    initViews(mResponse)
                } else {
                    createSnackbar(viewModelCrop.messageCancel.value)
                }
            }

        })

        hideProgress(pb, this)

    }

    private fun cancelStock() {

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(resources.getString(R.string.cancelStock))
        dialog.setPositiveButton(resources.getString(R.string.cancelStock), { _, _ ->
            confirmCancel()
        })
        dialog.setNegativeButton(resources.getString(R.string.no), { _, _ ->

        })

        dialog.create()
        dialog.show()


    }

    private fun confirmCancel() {

        val body = DeleteSupplyBody(SUPPLY_ID)

        val mResponse = viewModelCrop.cancelFunction(body)

        viewModelCrop.cancelFunction(body).observe(this, Observer { mResponse ->
            val success = viewModelCrop.successfulCancel.value
            if (success != null)
                if (success) {
                    manageCancelResponses(mResponse)
                } else {
                    createSnackbar(viewModelCrop.messageCancel.value)

                }

        })


    }

    private fun createSnackbar(value: String?) {
        Snackbar.make(container_crop_bids_details, value.toString(), Snackbar.LENGTH_SHORT).show()
    }

    private fun manageCancelResponses(mResponse: DeleteSupplyResponse?) {

        mResponse?.msg?.let { UIUtils.createToast(it, this, container_crop_bids_details) }
        Log.e(TAG, "In manage cancel response")
        onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun modifyStock() {

        d = androidx.appcompat.app.AlertDialog.Builder(this)

        v = layoutInflater.inflate(R.layout.layout_modify_supply, null)
        d.setView(v)


        //Init views
        etEst = v.findViewById(R.id.etEditEstDate) as EditText
        etExp = v.findViewById(R.id.etEditExpDate) as EditText
        cropType = v.findViewById(R.id.actvEdit_crop_type) as EditText

        offerPrice = v.findViewById(R.id.actvEdit_price) as EditText
        desc = v.findViewById(R.id.etEditDescription) as EditText

        //Auto fill these
        etEst.setText(TimeConversionUtils.reverseToReq(modifyBody.dateOfHarvest))
        etExp.setText(TimeConversionUtils.reverseToReq(modifyBody.expiry))
        offerPrice.setText(modifyBody.askPrice.toString())
        desc.setText(modifyBody.description)
        cropType.setText(modifyBody.variety)

        val nameOfCrop = v.findViewById(R.id.tilEditWhichCrop) as TextView
        nameOfCrop.text = mCrop


        tilType = v.findViewById(R.id.tilEditCropType) as TextInputLayout
        tilPrice = v.findViewById(R.id.tilEditOfferPrice) as TextInputLayout
        tilEst = v.findViewById(R.id.tilEditEstDate) as TextInputLayout
        tilExp = v.findViewById(R.id.tilEditExpDate) as TextInputLayout
        tilDesc = v.findViewById(R.id.tilEditDescription) as TextInputLayout

        //Positive and negative buttons

        //Create observer on Textof dates
        //Create observer on Textof dates
        //Date Instance

        //##Requires N
        etEst.setOnClickListener {

            TimeConversionUtils.clickOnDateObject(myCalendar, etEst, this)
        }

        //##Requires N
        etExp.setOnClickListener {
            TimeConversionUtils.clickOnDateObject(myCalendar, etExp, this)
        }


        d.setPositiveButton(resources.getString(R.string.modifyCrop)) { _, _ ->

            if (isValidate()) {
                confirmModify()
            }
        }

        d.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

        }
        d.create()

        tempRef = d.create()
        d.show()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun confirmModify() {

        showProgress(pb, this)
        getTranslations()

        if (getValidTranslations()) {
            makeModifyCalls()
        } else {
            mHandler.postDelayed({ makeModifyCalls() }, 5000)

        }
        hideProgress(pb, this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getTranslations() {

        OfflineTranslate.translateToEnglish(
            this,
            cropType.text.toString(),
            v.findViewById<TextView>(R.id.tvTempCropTypeModify)
        )
        OfflineTranslate.translateToEnglish(
            this,
            desc.text.toString(),
            v.findViewById<TextView>(R.id.tvTempCropDescModify)
        )

    }

    private fun getValidTranslations(): Boolean {

        return ValidationObject.validateTranslations(
            v.findViewById<TextView>(R.id.tvTempCropTypeModify),
            this
        ) && ValidationObject.validateTranslations(
            v.findViewById<TextView>(R.id.tvTempCropDescModify),
            this
        )

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun makeModifyCalls() {

        val transCropType =
            v.findViewById<TextView>(R.id.tvTempCropTypeModify).text.toString()
                .capitalize((Locale("en")))
        var transDesc = desc.text.toString()

        if (desc.text.toString() != resources.getString(R.string.noDesc)) {
            //If it has something, use uts translated values
            transDesc = v.findViewById<TextView>(R.id.tvTempCropDescModify).text.toString()
                .capitalize((Locale("en")))

        }

        modifyBody = ModifySupplyBody.Update(
            offerPrice.text.toString().toInt(),
            transCropType,
            transDesc,
            TimeConversionUtils.convertDateToReqForm(etExp.text.toString()),
            TimeConversionUtils.convertDateToReqForm(etEst.text.toString())
        )

        val body = ModifySupplyBody(SUPPLY_ID, modifyBody)
        tempRef.dismiss()


        viewModelCrop.updateFunction(body).observe(this, Observer { mResponse ->

            //Check with the sucessful of it
            if (viewModelCrop.successfulUpdate.value == false) {
                createSnackbar(viewModelCrop.messageUpdate.value)
            } else {
                manageModifyResponse(mResponse)
            }
        })


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun manageModifyResponse(mResponse: ModifySupplyResponse?) {
        createSnackbar(mResponse?.msg.toString())

        makeCall()

    }

    private fun isValidate(): Boolean {

        return ValidationObject.validateEmptyEditText(
            cropType,
            tilType,
            R.string.cropTypeError,
            this
        ) && ValidationObject.validateEmptyEditText(
            offerPrice,
            tilPrice,
            R.string.offerPriceError,
            this
        ) && ValidationObject.validateEmptyEditText(
            etEst,
            tilEst,
            R.string.etEstError,
            this
        ) && ValidationObject.validateEmptyEditText(
            etExp,
            tilExp,
            R.string.expError,
            this
        )

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initViews(value: ViewSupplyResponse) {

        //Remove the views if it is from Supply History

        try {
            findViewById<ConstraintLayout>(R.id.mLayoutSup).visibility = View.VISIBLE
            hideProgress(pb, this)

            //#TRANSLATION
            OfflineTranslate.translateToDefault(
                this,
                value.supply.crop,
                findViewById<TextView>(R.id.tv_stock_detail_crop_name)
            )
            OfflineTranslate.translateToDefault(
                this,
                value.supply.variety,
                findViewById<TextView>(R.id.tv_stock_detail_crop_type)
            )
            OfflineTranslate.translateToDefault(
                this,
                value.supply.description,
                findViewById<TextView>(R.id.tv_stock_detail_crop_description)
            )




            findViewById<TextView>(R.id.ans_detail_crop_quanity).text = value.supply.qty.toString()
            findViewById<TextView>(R.id.ans_detail_crop_exp).text =
                TimeConversionUtils.convertTimeToEpoch(value.supply.expiry)
            findViewById<TextView>(R.id.ans_detail_init_date).text =
                TimeConversionUtils.convertTimeToEpoch(value.supply.supplyCreated)

            findViewById<TextView>(R.id.tv_stock_detail_current_bid).text =
                value.supply.currentBid.toString()
            if (value.supply.currentBid < value.supply.askPrice) {
                findViewById<TextView>(R.id.tv_stock_detail_current_bid)
                    .setTextColor(resources.getColor(R.color.deltaRed))

            } else if (value.supply.currentBid == value.supply.askPrice) {
                findViewById<TextView>(R.id.tv_stock_detail_current_bid)
                    .setTextColor(resources.getColor(R.color.wildColor))

            }

            //Else the color is green

            findViewById<TextView>(R.id.tv_stock_detail_initial_offer_price).text =
                value.supply.askPrice.toString()
            mPrice = value.supply.askPrice.toString()
            mCrop = value.supply.crop
            numberOfBid = 0
            modifyBody = ModifySupplyBody.Update(
                askPrice = value.supply.askPrice,
                value.supply.variety,
                value.supply.description,
                value.supply.expiry,
                value.supply.dateOfHarvest
            )

            fillRecyclerView(value.supply.bids)
            createGraphView(
                value.supply.lastBid,
                value.supply.currentBid,
                value.supply.lastModified
            )


            //Hide views if not active
            if (!value.supply.active) {

                findViewById<MaterialButton>(R.id.mtb_modify_stock).visibility = View.GONE
                findViewById<MaterialButton>(R.id.mtb_cancel_stock).visibility = View.GONE
                //Show tge indefinte Snackbar
                Snackbar.make(
                    container_crop_bids_details,
                    resources.getString(R.string.inactiveSupply),
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error" + e.cause + e.message)
        }
    }

    private fun createGraphView(
        item: List<ViewSupplyResponse.Supply.LastBid>,
        currentBid: Int,
        lastModified: String
    ) {

        val mList = item.toMutableList()
        numberOfBid = 0
        if (item.isEmpty()) {
            //When there are no last bids or any bids
            graph.removeAllSeries()
            graph.visibility = View.GONE
//            findViewById<TextView>(R.id.tvNoGraph).visibility = View.VISIBLE

        } else {

            mList.add(ViewSupplyResponse.Supply.LastBid(currentBid, "", lastModified))

            numberOfBid = 0
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
            graph.gridLabelRenderer.numHorizontalLabels = numberOfBid
            graph.gridLabelRenderer.labelsSpace = 20


            Log.e(TAG, numberOfBid.toString() + "Number ")
            graph.gridLabelRenderer.setHorizontalLabelsAngle(90)

            // set manual x bounds to have nice steps

            if (!item.isEmpty()) {
                Log.e(
                    TAG,
                    "Start X is " + TimeConversionUtils.convertDateTimestampUtil(item.get(0).timestamp)
                        .toString()
                )

                TimeConversionUtils.convertDateTimestampUtil(item.get(0).timestamp)?.time?.toDouble()
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
        numberOfBid = 0
    }

    private fun getSeriesPoints(item: MutableList<ViewSupplyResponse.Supply.LastBid>): Array<DataPoint>? {

        val arrayDataPoints: MutableList<DataPoint> = mutableListOf()


        for (element in item) {

            numberOfBid++

            arrayDataPoints.add(
                DataPoint(
                    TimeConversionUtils.convertDateTimestampUtil(element.timestamp),
                    element.amount.toDouble()
                )
            )

            Log.e(
                TAG,
                "In get series data point " + TimeConversionUtils.convertDateTimestampUtil(element.timestamp)
                    .toString() + " Amount : " + element.amount.toDouble() + " Num" + numberOfBid.toString()
            )
        }



        return arrayDataPoints.toTypedArray()

    }

    private fun fillRecyclerView(bids: List<ViewSupplyResponse.Supply.Bid>) {

        val rv = findViewById<RecyclerView>(R.id.rv_bidHistory)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = MyBidHistoryAdapter(this)

        //Create list
        val mBids: MutableList<BidHistoryBody> = mutableListOf()


        for (element in bids) {

            val x = element.bids.get(element.bids.size - 1)
            mBids.add(
                BidHistoryBody(
                    x.amount,
                    x._id,
                    x.timestamp,
                    element.bidder.name,
                    element.bidder.phone,
                    element.bidder.address
                )
            )
        }

        mBids.sortByDescending { it.amount }
        Log.e(TAG, mBids.toString())

        adapter.lst = mBids
        rv.adapter = adapter
    }

    private fun closeBidHistory() {
        isOpen = false

        findViewById<RecyclerView>(R.id.rv_bidHistory).visibility = View.GONE
        findViewById<TextView>(R.id.tv_view_bid_history_stocks).text =
            resources.getString(R.string.view_bid_history)

        findViewById<ImageView>(R.id.iv_dropdown_bid_history)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_down))

    }

    private fun openBidHistory() {

        isOpen = true
        findViewById<RecyclerView>(R.id.rv_bidHistory).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_view_bid_history_stocks).text =
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

    override fun onBackPressed() {

        Log.e(TAG, "In on BackPressed")

//        val navController = findNavController()
//        navController.navigateUp()

        viewModelCrop.successfulSupply.removeObservers(this)
        viewModelCrop.successfulSupply.value = null

        viewModelCrop.successfulCancel.removeObservers(this)
        viewModelCrop.successfulCancel.value = null

        viewModelCrop.successfulUpdate.removeObservers(this)
        viewModelCrop.successfulUpdate.value = null

        super.onBackPressed()
        finish()

    }

    override fun viewBidDetails(_listItem: BidHistoryBody) {
        //The farmer can view the bids from here

    }


}
