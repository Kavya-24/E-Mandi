package com.example.mandiexe.ui.supply

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.MyBidHistoryAdapter
import com.example.mandiexe.adapter.OnBidHistoryClickListener
import com.example.mandiexe.models.body.supply.DeleteSupplyBody
import com.example.mandiexe.models.body.supply.ModifySupplyBody
import com.example.mandiexe.models.body.supply.ViewSupplyBody
import com.example.mandiexe.models.responses.supply.DeleteSupplyResponse
import com.example.mandiexe.models.responses.supply.ModifySupplyResponse
import com.example.mandiexe.models.responses.supply.ViewSupplyResponse
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.ExternalUtils.createToast
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

class MyCropBidDetails : Fragment(), OnBidHistoryClickListener {

    companion object {
        fun newInstance() = MyCropBidDetails()
    }

    private val viewModelCrop: MyCropBidDetailsViewModel by viewModels()
    private lateinit var root: View

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

    private var sdf = SimpleDateFormat("dd-MM-yy HH:mm")
    private var numberOfBid = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.my_crop_bid_details_fragment, container, false)

        //  lineChart = root.findViewById(R.id.lineChart)
        graph = root.findViewById(R.id.graphView)

        if (arguments != null) {
            //Set the address in the box trimmed
            SUPPLY_ID = requireArguments().getString("SUPPLY_ID").toString()

            Log.e(TAG, "Argument str is" + SUPPLY_ID)
        }

        //This gets an id as the argument and makes a call from it
        makeCall()


        //initViews
        root.findViewById<TextView>(R.id.tv_view_bid_history_stocks).setOnClickListener {

            //Open the history
            openBidHistory()

        }

        root.findViewById<MaterialButton>(R.id.mtb_cancel_stock).setOnClickListener {
            cancelStock()
        }


        root.findViewById<MaterialButton>(R.id.mtb_modify_stock).setOnClickListener {
            modifyStock()
        }

        return root
    }

    private fun openBidHistory() {

        root.findViewById<RecyclerView>(R.id.rv_bidHistory).visibility = View.VISIBLE
        if (adapter.lst.size == 0) {
            ExternalUtils.createSnackbar(
                resources.getString(R.string.emptyRV),
                requireContext(),
                container_crop_bids_details
            )
        }

    }

    private fun makeCall() {

        Log.e(TAG, SUPPLY_ID + " is supply id")
        val body = ViewSupplyBody(SUPPLY_ID)

        viewModelCrop.getFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->
            if (viewModelCrop.successfulSupply.value != null) {
                if (viewModelCrop.successfulSupply.value!!) {
                    Log.e(TAG, "Response is " + mResponse.toString())
                    initViews(mResponse)
                } else {
                    createSnackbar(viewModelCrop.messageCancel.value)
                }
            }

        })


    }

    private fun cancelStock() {

        val dialog = AlertDialog.Builder(context)
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

        viewModelCrop.cancelFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->
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

        mResponse?.msg?.let { createToast(it, requireContext(), container_crop_bids_details) }
        Log.e(TAG, "In manage cancel response")
        onDestroy()
    }

    private fun modifyStock() {

        d = androidx.appcompat.app.AlertDialog.Builder(requireContext())

        val v = layoutInflater.inflate(R.layout.layout_modify_supply, null)
        d.setView(v)


        //Init views
        val etEst = v.findViewById(R.id.etEditEstDate) as EditText
        val etExp = v.findViewById(R.id.etEditExpDate) as EditText
        val cropType = v.findViewById(R.id.actvEdit_crop_type) as EditText

        val offerPrice = v.findViewById(R.id.actvEdit_price) as EditText
        val desc = v.findViewById(R.id.etEditDescription) as EditText
        val tilType = v.findViewById(R.id.tilEditCropType) as TextInputLayout
        val tilPrice = v.findViewById(R.id.tilEditOfferPrice) as TextInputLayout
        val tilEst = v.findViewById(R.id.tilEditEstDate) as TextInputLayout
        val tilExp = v.findViewById(R.id.tilEditExpDate) as TextInputLayout


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
            context?.let { it1 ->
                DatePickerDialog(
                    it1, dateEst, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }

        //##Requires N
        etExp.setOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1, dateExp, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }


        d.setPositiveButton(resources.getString(R.string.modifyCrop)) { _, _ ->

            if (offerPrice.text.toString() != "") {
                modifyBody = ModifySupplyBody.Update(
                    offerPrice.text.toString().toInt(),
                    cropType.toString(),
                    desc.text.toString(),
                    ExternalUtils.convertDateToReqForm(etExp.text.toString()),
                    ExternalUtils.convertDateToReqForm(etEst.text.toString())
                )

            } else {

                modifyBody = ModifySupplyBody.Update(
                    mPrice.toInt(),
                    cropType.toString(),
                    desc.text.toString(),
                    ExternalUtils.convertDateToReqForm(etExp.text.toString()),
                    ExternalUtils.convertDateToReqForm(etEst.text.toString())
                )

            }
            confirmModify()

            //}

        }
        d.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

        }
        d.create()

        tempRef = d.create()
        d.show()

    }

    private fun confirmModify() {
        tempRef.dismiss()

        val body = ModifySupplyBody(SUPPLY_ID, modifyBody)

        if (body != null) {
            viewModelCrop.updateFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->

                //Check with the sucessful of it
                if (viewModelCrop.successfulUpdate.value == false) {
                    createSnackbar(viewModelCrop.messageUpdate.value)
                } else {
                    manageModifyResponse(mResponse)
                }
            })
        }


    }

    private fun manageModifyResponse(mResponse: ModifySupplyResponse?) {
        createSnackbar(mResponse?.msg.toString())
    }

//    private fun isValidate(): Boolean {
//
//        var isValid = true
//
//
//        if (cropType.text.isEmpty()) {
//            isValid = false
//            tilType.error = resources.getString(R.string.cropTypeError)
//        } else {
//            tilType.error = null
//        }
//
//
//
//        if (offerPrice.text.isEmpty()) {
//            isValid = false
//            tilPrice.error = resources.getString(R.string.offerPriceError)
//        } else {
//            tilPrice.error = null
//        }
//
//
//        if (etEst.text.isEmpty()) {
//            isValid = false
//            tilEst.error = resources.getString(R.string.etEstError)
//        } else {
//            tilEst.error = null
//        }
//
//
//        if (etExp.text.isEmpty()) {
//            isValid = false
//            tilExp.error = resources.getString(R.string.expError)
//        } else {
//            tilExp.error = null
//        }
//
//
//
//
//
//        return isValid
//    }

    private fun initViews(value: ViewSupplyResponse) {

        root.findViewById<ConstraintLayout>(R.id.mLayoutSup).visibility = View.VISIBLE
        root.findViewById<ProgressBar>(R.id.pb_my_crops_details).visibility = View.GONE

        root.findViewById<TextView>(R.id.tv_stock_detail_crop_name).text = value.supply.crop
        root.findViewById<TextView>(R.id.tv_stock_detail_crop_type).text = value.supply.variety
        root.findViewById<TextView>(R.id.tv_stock_detail_crop_description).text =
            value.supply.description

        root.findViewById<TextView>(R.id.ans_detail_crop_quanity).text = value.supply.qty.toString()
        root.findViewById<TextView>(R.id.ans_detail_crop_exp).text =
            ExternalUtils.convertTimeToEpoch(value.supply.expiry)
        root.findViewById<TextView>(R.id.ans_detail_init_date).text =
            ExternalUtils.convertTimeToEpoch(value.supply.supplyCreated)

        root.findViewById<TextView>(R.id.tv_stock_detail_current_bid).text =
            value.supply.currentBid.toString()
        if (value.supply.currentBid < value.supply.askPrice) {
            root.findViewById<TextView>(R.id.tv_stock_detail_current_bid)
                .setTextColor(resources.getColor(R.color.red_A700))

        } else if (value.supply.currentBid == value.supply.askPrice) {
            root.findViewById<TextView>(R.id.tv_stock_detail_current_bid)
                .setTextColor(resources.getColor(R.color.blue_A700))

        }

        //Else the color is green

        root.findViewById<TextView>(R.id.tv_stock_detail_initial_offer_price).text =
            value.supply.askPrice.toString()
        mPrice = value.supply.askPrice.toString()

        fillRecyclerView(value.supply.bids)
        createGraphView(value.supply.bids)
    }

    private fun createGraphView(item: List<ViewSupplyResponse.Supply.Bid>) {

        val calendar = Calendar.getInstance()
        val series: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>(getSeriesPoints(item))

        Log.e(TAG, "Series is given by " + series.toString())

        graph.addSeries(series)

        graph.gridLabelRenderer.horizontalAxisTitle = resources.getString(R.string.time)
        graph.gridLabelRenderer.verticalAxisTitle = resources.getString(R.string.price_rs)

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


        Log.e(TAG, numberOfBid.toString() + "Number ")
        graph.gridLabelRenderer.setHorizontalLabelsAngle(90)

        // set manual x bounds to have nice steps
        //graph.getViewport().setMinX(getInitialDate(item).time.toDouble());
        //graph.getViewport().setMaxX(getEndingDate(item).time.toDouble());
        //graph.getViewport().setXAxisBoundsManual(true);

        graph.title = resources.getString(R.string.myBidHistory)
        graph.titleColor = Color.BLACK

        graph.gridLabelRenderer.labelHorizontalHeight = 300

    }

    private fun getEndingDate(item: List<ViewSupplyResponse.Supply.Bid>): Date {

        if (item.size != 0) {
            Log.e(
                TAG,
                "Ending date " + ExternalUtils.convertDateTimestampUtil(
                    item.get(item.size - 1).bids.get(item.size - 1).timestamp
                ).toString()
            )
            return ExternalUtils.convertDateTimestampUtil(
                item.get(item.size - 1).bids.get(item.size - 1).timestamp
            )!!
        }
        return Date()
    }


    private fun getInitialDate(item: List<ViewSupplyResponse.Supply.Bid>): Date {

        if (item.size != 0) {
            Log.e(
                TAG,
                "Starting adte date " + ExternalUtils.convertDateTimestampUtil(
                    item.get(0).bids.get(
                        0
                    ).timestamp
                )
            )
            return ExternalUtils.convertDateTimestampUtil(item.get(0).bids.get(0).timestamp)!!
        }

        return Date()
    }


    private fun getSeriesPoints(item: List<ViewSupplyResponse.Supply.Bid>): Array<DataPoint>? {

        val arrayDataPoints: MutableList<DataPoint> = mutableListOf()


        for (element in item) {


            for (j in element.bids) {

                numberOfBid++

                arrayDataPoints.add(
                    DataPoint(
                        ExternalUtils.convertDateTimestampUtil(j.timestamp),
                        j.amount.toDouble()
                    )
                )

            }
        }



        return arrayDataPoints.toTypedArray()

    }

    private fun fillRecyclerView(bids: List<ViewSupplyResponse.Supply.Bid>) {

        val rv = root.findViewById<RecyclerView>(R.id.rv_bidHistory)
        rv.layoutManager = LinearLayoutManager(context)
        adapter = MyBidHistoryAdapter(this)

        //Create list
        val mBids: MutableList<ViewSupplyResponse.Supply.Bid.BidDetails> = mutableListOf()

        for (element in bids) {
            mBids.add(element.bids.get(element.bids.size - 1))
        }

        adapter.lst = mBids
        rv.adapter = adapter
    }


    override fun onDestroy() {

        Log.e(TAG, "In on destroy")

        val navController = findNavController()
        navController.navigateUp()

        viewModelCrop.successfulSupply.removeObservers(this)
        viewModelCrop.successfulSupply.value = null

        viewModelCrop.successfulCancel.removeObservers(this)
        viewModelCrop.successfulCancel.value = null

        viewModelCrop.successfulUpdate.removeObservers(this)
        viewModelCrop.successfulUpdate.value = null

        super.onDestroy()


    }

    override fun viewBidDetails(_listItem: ViewSupplyResponse.Supply.Bid.BidDetails) {
        //The farmer can view the bids from here

    }


}
