package com.example.mandiexe.ui.supply

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import com.example.mandiexe.viewmodels.MyCropBidDetailsViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.my_crop_bid_details_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyCropBidDetails : Fragment(), OnBidHistoryClickListener {

    companion object {
        fun newInstance() = MyCropBidDetails()
    }

    private val viewModelCrop: MyCropBidDetailsViewModel by viewModels()
    private lateinit var root: View
    private lateinit var aaChartView: AAChartView
    private lateinit var lineChart: LineChart
    private lateinit var args: Bundle


    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog

    //Modify stock
    private lateinit var tilType: TextInputLayout
    private lateinit var tilEst: TextInputLayout
    private lateinit var tilPrice: TextInputLayout
    private lateinit var tilExp: TextInputLayout


    private lateinit var etEst: EditText
    private lateinit var etExp: EditText
    private lateinit var cropType: AutoCompleteTextView
    private lateinit var offerPrice: EditText
    private lateinit var desc: EditText

    private val myCalendar = Calendar.getInstance()

    private var SUPPLY_ID = ""
    private val TAG = MyCropBidDetails::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.my_crop_bid_details_fragment, container, false)
        lineChart = root.findViewById(R.id.lineChart)

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

    }

    private fun makeCall() {

        val body = ViewSupplyBody(SUPPLY_ID)
        val mResponse = viewModelCrop.getFunction(body)
        val success = viewModelCrop.successfulSupply.value
        if (success != null) {
            if (!success) {
                createSnackbar(viewModelCrop.messageCancel.value)
            } else {
                mResponse.value?.let { initViews(it) }
            }
        }


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
        val success = viewModelCrop.successfulCancel.value
        if (success != null) {
            if (success) {
                manageCancelResponses(mResponse.value)
            } else {
                createSnackbar(viewModelCrop.messageCancel.value)
            }
        }

    }

    private fun createSnackbar(value: String?) {
        Snackbar.make(container_crop_bids_details, value.toString(), Snackbar.LENGTH_SHORT).show()
    }

    private fun manageCancelResponses(mResponse: DeleteSupplyResponse?) {
        onDestroy()
    }

    private fun modifyStock() {

        d = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val v = layoutInflater.inflate(R.layout.layout_modify_supply, null)
        d.setView(v)


        //Init views
        etEst = v.findViewById(R.id.etEditEstDate)
        etExp = v.findViewById(R.id.etEditExpDate)
        cropType = root.findViewById(R.id.actvEdit_crop_type)
        offerPrice = root.findViewById(R.id.actvEdit_price)
        desc = root.findViewById(R.id.etEditDescription)
        tilType = root.findViewById(R.id.tilEditCropType)
        tilPrice = root.findViewById(R.id.tilEditOfferPrice)
        tilEst = root.findViewById(R.id.tilEditEstDate)
        tilExp = root.findViewById(R.id.tilEditExpDate)


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

            // if (isValidate()) {
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

        val update = ModifySupplyBody.Update(
            offerPrice.text.toString().toInt(),
            cropType.text.toString(),
            desc.text.toString(),
            etExp.text.toString(),
            etEst.text.toString()
        )
        val body = ModifySupplyBody(SUPPLY_ID, update)

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

    private fun initViews(value: ViewSupplyResponse) {

        //Set the above 7 entities wrt root

        root.findViewById<ProgressBar>(R.id.pb_my_crops_details).visibility = View.GONE

        root.findViewById<TextView>(R.id.tv_stock_detail_crop_name).text = value.supply.crop
        root.findViewById<TextView>(R.id.tv_stock_detail_crop_type).text = value.supply.variety
        root.findViewById<TextView>(R.id.tv_stock_detail_crop_description).text =
            value.supply.description

        root.findViewById<TextView>(R.id.ans_detail_crop_quanity).text = value.supply.qty.toString()
        root.findViewById<TextView>(R.id.ans_detail_crop_exp).text = value.supply.expiry
        root.findViewById<TextView>(R.id.ans_detail_init_date).text = value.supply.supplyCreated

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

        fillRecyclerView(value.supply.bids)
        createGraph(value.supply.bids)
    }

    private fun fillRecyclerView(bids: List<ViewSupplyResponse.Supply.Bid>) {

        val rv = root.findViewById<RecyclerView>(R.id.rv_bidHistory)
        rv.layoutManager = LinearLayoutManager(context)
        val adapter = MyBidHistoryAdapter(this)

        //Create list
        val mBids: MutableList<ViewSupplyResponse.Supply.Bid.Bid> = mutableListOf()
        for (element in bids) {
            for (j in element.bids) {
                mBids.add(j)
            }
        }

        adapter.lst = mBids
        rv.adapter = adapter
    }

    private fun createGraph(item: List<ViewSupplyResponse.Supply.Bid>) {

        //First bid is wrt to the first person
        //Second bid is wrt to the bids of the parent bid person
        val entries = ArrayList<Entry>()

        for (element in item) {
            for (j in element.bids) {
                entries.add(
                    Entry(
                        ExternalUtils.convertTimeToEpoch(j.timestamp.toString()).toFloat(),
                        j.amount.toFloat()
                    )
                )
            }
        }


        val vl = LineDataSet(entries, requireContext().resources.getString(R.string.graphTitle))


        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.lineWidth = 3f
        vl.fillColor = requireContext().resources.getColor(R.color.darkBlue)
        vl.fillAlpha = R.color.red


        lineChart.xAxis.labelRotationAngle = 0f

        lineChart.data = LineData(vl)


        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        lineChart.description.text = resources.getString(R.string.graphTitle)
        lineChart.setNoDataText(resources.getString(R.string.graphError))

        lineChart.animateX(1800, Easing.EaseInExpo)


    }

    override fun onDestroy() {
        super.onDestroy()

        viewModelCrop.successfulSupply.removeObservers(this)
        viewModelCrop.successfulSupply.value = null

        viewModelCrop.successfulCancel.removeObservers(this)
        viewModelCrop.successfulCancel.value = null

        viewModelCrop.successfulUpdate.removeObservers(this)
        viewModelCrop.successfulUpdate.value = null


    }

    override fun viewBidDetails(_listItem: ViewSupplyResponse.Supply.Bid.Bid) {
        //The farmer can view the bids from here

    }


}