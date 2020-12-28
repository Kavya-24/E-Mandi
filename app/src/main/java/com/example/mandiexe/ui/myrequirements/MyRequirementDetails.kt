package com.example.mandiexe.ui.myrequirements

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.adapter.OnBidHistoryClickListener
import com.example.mandiexe.models.body.BidHistoryBody
import com.example.mandiexe.models.body.bid.DeletBidBody
import com.example.mandiexe.models.body.bid.UpdateBidBody
import com.example.mandiexe.models.body.bid.ViewBidBody
import com.example.mandiexe.models.responses.bids.DeleteBidResponse
import com.example.mandiexe.models.responses.bids.UpdateBidResponse
import com.example.mandiexe.models.responses.bids.ViewBidResponse
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.ExternalUtils.createSnackbar
import com.example.mandiexe.viewmodels.MyRequirementDetailsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.my_requirement_details_fragment.*

class MyRequirementDetails : Fragment(), OnBidHistoryClickListener {

    companion object {
        fun newInstance() = MyRequirementDetails()
    }

    private val viewModel: MyRequirementDetailsViewModel by viewModels()
    private lateinit var root: View
    private lateinit var args: Bundle

    private var BID_ID = ""
    private val TAG = MyRequirementDetails::class.java.simpleName

    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog

    private lateinit var lineChart: LineChart
    private var ownerPhone = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.my_requirement_details_fragment, container, false)
        lineChart = root.findViewById(R.id.lineChartMyBids)

        args = requireArguments()

        //This will be a call using BID ID
        if (arguments != null) {
            //Set the address in the box trimmed
            BID_ID = requireArguments().getString("BID_ID").toString()

            Log.e(TAG, "BID ID  " + BID_ID)
        }

        //This will be a call and then instantiate views
        makeCall()


        //initViews
        root.findViewById<TextView>(R.id.tv_view_bid_history_requirement).setOnClickListener {
            //##Send the crop object

        }

        root.findViewById<MaterialButton>(R.id.mtb_cancel_bid).setOnClickListener {
            cancelBid()
        }


        root.findViewById<MaterialButton>(R.id.mtb_Modify_bid).setOnClickListener {

            //Send the Bid Uodate body : Modify Bid Body in the bundle
            //Check for bundle in the BIdFragment
            createModifyBidDialog()

        }



        root.findViewById<ImageView>(R.id.iv_req_call_buyer).setOnClickListener {
            val i = Intent(Intent.ACTION_CALL, Uri.parse("number"))
            startActivity(i)
        }

        root.findViewById<TextView>(R.id.tv_view_bid_history_requirement).setOnClickListener {
            viewBidHistory()
        }


        return root
    }

    private fun makeCall() {

        root.findViewById<ProgressBar>(R.id.pb_my_req_details).visibility = View.VISIBLE

        val body = ViewBidBody(BID_ID)
        val mResponse = viewModel.getFunction(body)
        val success = viewModel.successfulBid.value
        if (success != null) {
            if (!success) {
                createSnackbar(
                    viewModel.messageCancel.value,
                    requireContext(),
                    container_req_details
                )
            } else {
                mResponse.value?.let { initViews(it) }
            }
        }

        root.findViewById<ProgressBar>(R.id.pb_my_req_details).visibility = View.GONE

    }

    private fun createModifyBidDialog() {

        d = androidx.appcompat.app.AlertDialog.Builder(requireContext())
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

    private fun confirmModify(newBid: String) {
        tempRef.dismiss()

        val body = UpdateBidBody(BID_ID, newBid)

        if (body != null) {
            viewModel.updateFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->
                //Check with the sucessful of it
                if (viewModel.successfulUpdate.value == false) {
                    context?.let {
                        createSnackbar(
                            viewModel.messageUpdate.value,
                            it, container_req_details
                        )
                    }
                } else {
                    manageModifyResponses(mResponse)
                }
            })
        }


    }

    private fun manageModifyResponses(mResponse: UpdateBidResponse?) {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.bidUpdated),
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun cancelBid() {

        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(resources.getString(R.string.cancelBid))
        dialog.setPositiveButton(resources.getString(R.string.cancelStock), { _, _ ->
            confirmCancel()
        })
        dialog.setNegativeButton(resources.getString(R.string.no), { _, _ ->

        })

        dialog.create()
        dialog.show()


    }

    private fun confirmCancel() {

        val body = DeletBidBody(BID_ID)

        if (body != null) {
            viewModel.cancelFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->

                //Check with the sucessful of it
                if (viewModel.successfulCancel.value == false) {
                    context?.let {
                        createSnackbar(
                            viewModel.messageCancel.value,
                            it, container_req_details
                        )
                    }
                } else {
                    manageCancelResponses(mResponse)
                }
            })
        }

    }

    private fun manageCancelResponses(mResponse: DeleteBidResponse) {

        Toast.makeText(
            requireContext(),
            resources.getString(R.string.BidDeleted),
            Toast.LENGTH_SHORT
        )
        onDestroy()
    }

    private fun viewBidHistory() {

        root.findViewById<RecyclerView>(R.id.rv_bidHistoryMyReq).visibility = View.VISIBLE
    }

    private fun initViews(value: ViewBidResponse) {

        //Set the above 8 entities wrt root
        //remove pb
        //View the root
        root.findViewById<ConstraintLayout>(R.id.mLayoutReq).visibility = View.VISIBLE
        root.findViewById<ProgressBar>(R.id.pb_my_req_details).visibility = View.GONE


        root.findViewById<TextView>(R.id.tv_requirement_detail_crop_name).text =
            value.bid.demand.crop
        root.findViewById<TextView>(R.id.tv_requirement_detail_crop_type).text =
            value.bid.demand.variety
        root.findViewById<TextView>(R.id.tv_requirement_detail_crop_location).text =
            value.bid.bidder.address.toString()

        root.findViewById<TextView>(R.id.ans_detail_bid_quanity).text =
            value.bid.demand.qty.toString()
        root.findViewById<TextView>(R.id.ans_detail_bid_exp).text = value.bid.demand.expiry
        root.findViewById<TextView>(R.id.ans_detail_init_date).text = value.bid.demand.demandCreated

        root.findViewById<TextView>(R.id.tv_requirement_detail_current_bid).text =
            value.bid.demand.currentBid.toString()

        if (value.bid.demand.currentBid < value.bid.demand.offerPrice) {
            root.findViewById<TextView>(R.id.tv_requirement_detail_current_bid)
                .setTextColor(resources.getColor(R.color.red_A700))

        } else if (value.bid.demand.currentBid == value.bid.demand.offerPrice) {
            root.findViewById<TextView>(R.id.tv_requirement_detail_current_bid)
                .setTextColor(resources.getColor(R.color.blue_A700))

        }

        //Else the color is green

        //Store owner number
        ownerPhone = value.bid.bidder.phone
        root.findViewById<TextView>(R.id.tv_requirement_detail_initial_offer_price).text =
            value.bid.demand.offerPrice.toString()

        fillRecyclerView(value.bid.bids)
        createGraph(value.bid.bids)

    }

    private fun fillRecyclerView(bids: List<ViewBidResponse.Bid.BidDetails>) {

//        val rv = root.findViewById<RecyclerView>(R.id.rv_bidHistoryMyReq)
//        rv.layoutManager = LinearLayoutManager(context)
//        val adapter = MyBidHistoryAdapter(this)
//
//        //Create list
//        //Fill the rv wit
//        val mBids: MutableList<BidHistoryBody> = mutableListOf()
//        for (element in bids) {
//
//        }
//
//        adapter.lst = mBids
//        rv.adapter = adapter
//
    }

    private fun createGraph(item: List<ViewBidResponse.Bid.BidDetails>) {

        //Sample
        //First bid is wrt to the first person
        //Second bid is wrt to the bids of the parent bid person
        val entries = ArrayList<Entry>()

        for (element in item) {
            entries.add(
                Entry(
                    ExternalUtils.convertTimeToEpoch(element.timestamp.toString()).toFloat(),
                    element.amount.toFloat()
                )
            )

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

        viewModel.successfulBid.removeObservers(this)
        viewModel.successfulBid.value = null

        viewModel.successfulCancel.removeObservers(this)
        viewModel.successfulCancel.value = null

        viewModel.successfulUpdate.removeObservers(this)
        viewModel.successfulUpdate.value = null


    }

    override fun viewBidDetails(_listItem: BidHistoryBody) {

    }

}
