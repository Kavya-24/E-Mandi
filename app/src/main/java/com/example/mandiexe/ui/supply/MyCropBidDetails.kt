package com.example.mandiexe.ui.supply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.MyBidDetailsViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.button.MaterialButton

class MyCropBidDetails : Fragment() {

    companion object {
        fun newInstance() = MyCropBidDetails()
    }

    private lateinit var viewModel: MyBidDetailsViewModel
    private lateinit var root: View
    private lateinit var aaChartView: AAChartView
    private lateinit var args: Bundle

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

    }

    private fun modifyStock() {

    }

    private fun initViews() {

        //Set the above 7 entities wrt root
        createGraph()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyBidDetailsViewModel::class.java)
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
