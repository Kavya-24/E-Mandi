package com.example.mandiexe.ui.myrequirements

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mandiexe.R
import com.example.mandiexe.models.body.bid.DeletBidBody
import com.example.mandiexe.models.body.bid.UpdateBidBody
import com.example.mandiexe.models.responses.bids.DeleteBidResponse
import com.example.mandiexe.models.responses.bids.UpdateBidResponse
import com.example.mandiexe.utils.ExternalUtils.createSnackbar
import com.example.mandiexe.viewmodels.MyRequirementDetailsViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.my_requirement_details_fragment.*

class MyRequirementDetails : Fragment() {

    companion object {
        fun newInstance() = MyRequirementDetails()
    }

    private val viewModel: MyRequirementDetailsViewModel by viewModels()
    private lateinit var root: View
    private lateinit var aaChartView: AAChartView
    private lateinit var args: Bundle

    private var BID_ID = ""
    private val TAG = MyRequirementDetails::class.java.simpleName

    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.my_requirement_details_fragment, container, false)

        aaChartView = root.findViewById<AAChartView>(R.id.chartView_req)
        args = requireArguments()

        //This will be a call using BID ID
        if (arguments != null) {
            //Set the address in the box trimmed
            BID_ID = requireArguments().getString("BID_ID").toString()

            Log.e(TAG, "BID ID  " + BID_ID)
        }

        //This will be a call and then instantiate views
        initViews()

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

    }

    private fun initViews() {

        //Set the above 8 entities wrt root
        createGraph()
    }

    private fun createGraph() {

        //Sample
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .title("Bid History")
            .backgroundColor("#4b2b7f")
            .yAxisTitle(resources.getString(R.string.price))
            .dataLabelsEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("12 Dec")
                        .data(
                            arrayOf(
                                35.7
                            )
                        ),

                    AASeriesElement()
                        .name("13 Dec")
                        .data(
                            arrayOf(35.9)
                        ),

                    AASeriesElement()
                        .name("18 Dec")
                        .data(
                            arrayOf(38.7)
                        )
                )
            )


        aaChartView.aa_drawChartWithChartModel(aaChartModel)


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

}
