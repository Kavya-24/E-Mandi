package com.example.mandiexe.ui.supply

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.models.body.supply.DeleteSupplyBody
import com.example.mandiexe.models.body.supply.ModifySupplyBody
import com.example.mandiexe.models.responses.supply.DeleteSupplyResponse
import com.example.mandiexe.models.responses.supply.ModifySupplyResponse
import com.example.mandiexe.viewmodels.MyCropBidDetailsViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.my_crop_bid_details_fragment.*

class MyCropBidDetails : Fragment() {

    companion object {
        fun newInstance() = MyCropBidDetails()
    }

    private lateinit var viewModelCrop: MyCropBidDetailsViewModel
    private lateinit var root: View
    private lateinit var aaChartView: AAChartView
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.my_crop_bid_details_fragment, container, false)
        aaChartView = root.findViewById<AAChartView>(R.id.chartView_details)

        args = requireArguments()

        //This gets an id as the argument and makes a call from it
        makeCall()


        //initViews
        root.findViewById<TextView>(R.id.tv_view_bid_history_stocks).setOnClickListener {
            //##Send the crop object
            root.findNavController().navigate(R.id.action_myBidDetails_to_bidHistory)

        }

        root.findViewById<MaterialButton>(R.id.mtb_cancel_stock).setOnClickListener {
            cancelStock()
        }


        root.findViewById<MaterialButton>(R.id.mtb_modify_stock).setOnClickListener {
            modifyStock()
        }

        return root
    }

    private fun makeCall() {


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
        val body = args.getString("SUPPLY_ID")?.let { DeleteSupplyBody(it) }

        if (body != null) {
            viewModelCrop.cancelFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->

                //Check with the sucessful of it
                if (viewModelCrop.successfulCancel.value == false) {
                    createSnackbar(viewModelCrop.messageCancel.value)
                } else {
                    manageCancelResponses(mResponse)
                }
            })
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
        etEst = v.findViewById(R.id.etEstDate)
        etExp = v.findViewById(R.id.etExpDate)
        cropType = root.findViewById(R.id.actv_crop_type)
        offerPrice = root.findViewById(R.id.actv_price)
        desc = root.findViewById(R.id.etDescription)
        tilType = root.findViewById(R.id.tilCropType)
        tilPrice = root.findViewById(R.id.tilOfferPrice)
        tilEst = root.findViewById(R.id.tilEstDate)
        tilExp = root.findViewById(R.id.tilExpDate)


        //Positive and negative buttons

        //Create observer on Text

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
        val body = args.getString("SUPPLY_ID")?.let { ModifySupplyBody(it, update) }

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

    private fun initViews() {

        //Set the above 7 entities wrt root
        createGraph()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelCrop = ViewModelProviders.of(this).get(MyCropBidDetailsViewModel::class.java)
    }

    private fun createGraph() {

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

}
