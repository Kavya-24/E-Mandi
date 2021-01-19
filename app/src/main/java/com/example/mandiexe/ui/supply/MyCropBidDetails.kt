package com.example.mandiexe.ui.supply

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
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
import com.example.mandiexe.lib.OfflineTranslate
import com.example.mandiexe.models.body.BidHistoryBody
import com.example.mandiexe.models.body.supply.DeleteSupplyBody
import com.example.mandiexe.models.body.supply.ModifySupplyBody
import com.example.mandiexe.models.body.supply.ViewSupplyBody
import com.example.mandiexe.models.responses.supply.DeleteSupplyResponse
import com.example.mandiexe.models.responses.supply.ModifySupplyResponse
import com.example.mandiexe.models.responses.supply.ViewSupplyResponse
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.ExternalUtils.createToast
import com.example.mandiexe.utils.ExternalUtils.setAppLocale
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
    //private lateinit var root: View

    // private lateinit var lineChart: LineChart
    private lateinit var graph: GraphView
    private lateinit var args: Bundle


    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog


    private val myCalendar = Calendar.getInstance()
    private lateinit var adapter: MyBidHistoryAdapter

    private var SUPPLY_ID = ""
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
    private lateinit var etEst: EditText
    private lateinit var offerPrice: EditText
    private lateinit var cropType: EditText
    private lateinit var etExp: EditText

    private val pref = com.example.mandiexe.utils.auth.PreferenceUtil

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

        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = resources.getString(R.string.details)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }

        Log.e(TAG, "Argument str is" + SUPPLY_ID)

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
            ExternalUtils.createSnackbar(
                resources.getString(R.string.emptyRV),
                this,
                container_crop_bids_details
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun makeCall() {

        Log.e(TAG, SUPPLY_ID + " is supply id")
        val body = ViewSupplyBody(SUPPLY_ID)
        //Clear graph series
        graph.removeAllSeries()
        graph.clearSecondScale()
        graph.clearAnimation()
        numberOfBid = 0



        viewModelCrop.getFunction(body).observe(this, Observer { mResponse ->
            if (viewModelCrop.successfulSupply.value != null) {
                if (viewModelCrop.successfulSupply.value!!) {
                    initViews(mResponse)
                } else {
                    createSnackbar(viewModelCrop.messageCancel.value)
                }
            }

        })


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

        mResponse?.msg?.let { createToast(it, this, container_crop_bids_details) }
        Log.e(TAG, "In manage cancel response")
        onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun modifyStock() {

        d = androidx.appcompat.app.AlertDialog.Builder(this)

        val v = layoutInflater.inflate(R.layout.layout_modify_supply, null)
        d.setView(v)


        //Init views
        etEst = v.findViewById(R.id.etEditEstDate) as EditText
        etExp = v.findViewById(R.id.etEditExpDate) as EditText
        cropType = v.findViewById(R.id.actvEdit_crop_type) as EditText

        offerPrice = v.findViewById(R.id.actvEdit_price) as EditText
        val desc = v.findViewById(R.id.etEditDescription) as EditText

        //Auto fill these
        etEst.setText(ExternalUtils.reverseToReq(modifyBody.dateOfHarvest))
        etExp.setText(ExternalUtils.reverseToReq(modifyBody.expiry))
        offerPrice.setText(modifyBody.askPrice.toString())
        desc.setText(modifyBody.description)
        cropType.setText(modifyBody.variety)

        val nameOfCrop = v.findViewById(R.id.tilEditWhichCrop) as TextView
        nameOfCrop.text = mCrop


        tilType = v.findViewById(R.id.tilEditCropType) as TextInputLayout
        tilPrice = v.findViewById(R.id.tilEditOfferPrice) as TextInputLayout
        tilEst = v.findViewById(R.id.tilEditEstDate) as TextInputLayout
        tilExp = v.findViewById(R.id.tilEditExpDate) as TextInputLayout


        //Positive and negative buttons

        //Create observer on Textof dates
        //Date Instance

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        val dateEst =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val now = myCalendar.timeInMillis
                view.minDate = now

                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                etEst.setText(sdf.format(myCalendar.time))

            }

        val dateExp =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                val now = myCalendar.timeInMillis
                view.minDate = now
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                etExp.setText(sdf.format(myCalendar.time))

            }


        //##Requires N
        etEst.setOnClickListener {
            let { it1 ->
                DatePickerDialog(
                    it1, dateEst, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }

        //##Requires N
        etExp.setOnClickListener {
            let { it1 ->
                DatePickerDialog(
                    it1, dateExp, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }


        d.setPositiveButton(resources.getString(R.string.modifyCrop)) { _, _ ->

            if (isValidate()) {
                modifyBody = ModifySupplyBody.Update(
                    offerPrice.text.toString().toInt(),
                    cropType.text.toString(),
                    desc.text.toString(),
                    ExternalUtils.convertDateToReqForm(etExp.text.toString()),
                    ExternalUtils.convertDateToReqForm(etEst.text.toString())
                )

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
        tempRef.dismiss()

        val body = ModifySupplyBody(SUPPLY_ID, modifyBody)

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

        var isValid = true


        if (cropType.text.isEmpty()) {
            isValid = false
            tilType.error = resources.getString(R.string.cropTypeError)
        } else {
            tilType.error = null
        }



        if (offerPrice.text.isEmpty()) {
            isValid = false
            tilPrice.error = resources.getString(R.string.offerPriceError)
        } else {
            tilPrice.error = null
        }


        if (etEst.text.isEmpty()) {
            isValid = false
            tilEst.error = resources.getString(R.string.etEstError)
        } else {
            tilEst.error = null
        }


        if (etExp.text.isEmpty()) {
            isValid = false
            tilExp.error = resources.getString(R.string.expError)
        } else {
            tilExp.error = null
        }


        return isValid
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initViews(value: ViewSupplyResponse) {

        findViewById<ConstraintLayout>(R.id.mLayoutSup).visibility = View.VISIBLE
        findViewById<ProgressBar>(R.id.pb_my_crops_details).visibility = View.GONE

        //#TRANSLATION
        findViewById<TextView>(R.id.tv_stock_detail_crop_name).text = OfflineTranslate.translateToDefault(this,value.supply.crop)
        findViewById<TextView>(R.id.tv_stock_detail_crop_type).text =
            OfflineTranslate.translateToDefault(this,value.supply.variety)
        findViewById<TextView>(R.id.tv_stock_detail_crop_description).text =
            OfflineTranslate.translateToDefault(this,value.supply.description)


        findViewById<TextView>(R.id.ans_detail_crop_quanity).text = value.supply.qty.toString()
        findViewById<TextView>(R.id.ans_detail_crop_exp).text =
            ExternalUtils.convertTimeToEpoch(value.supply.expiry)
        findViewById<TextView>(R.id.ans_detail_init_date).text =
            ExternalUtils.convertTimeToEpoch(value.supply.supplyCreated)

        findViewById<TextView>(R.id.tv_stock_detail_current_bid).text =
            value.supply.currentBid.toString()
        if (value.supply.currentBid < value.supply.askPrice) {
            findViewById<TextView>(R.id.tv_stock_detail_current_bid)
                .setTextColor(resources.getColor(R.color.red_A700))

        } else if (value.supply.currentBid == value.supply.askPrice) {
            findViewById<TextView>(R.id.tv_stock_detail_current_bid)
                .setTextColor(resources.getColor(R.color.blue_A700))

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
        createGraphView(value.supply.lastBid, value.supply.currentBid, value.supply.lastModified)
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
            findViewById<TextView>(R.id.tvNoGraph).visibility = View.VISIBLE

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
                    "Start X is " + ExternalUtils.convertDateTimestampUtil(item.get(0).timestamp)
                        .toString()
                )

                ExternalUtils.convertDateTimestampUtil(item.get(0).timestamp)?.time?.toDouble()?.let {
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
                    ExternalUtils.convertDateTimestampUtil(element.timestamp),
                    element.amount.toDouble()
                )
            )

            Log.e(
                TAG,
                "In get series data point " + ExternalUtils.convertDateTimestampUtil(element.timestamp)
                    .toString() +" Amount : "+  element.amount.toDouble() + " Num" + numberOfBid.toString()
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
