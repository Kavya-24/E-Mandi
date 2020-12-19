package com.example.mandiexe.ui.myrequirements

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.MyRequirementDetailsViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.button.MaterialButton

class MyRequirementDetails : Fragment() {

    companion object {
        fun newInstance() = MyRequirementDetails()
    }

    private lateinit var viewModel: MyRequirementDetailsViewModel
    private lateinit var root: View
    private lateinit var aaChartView: AAChartView
    private lateinit var args: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.my_requirement_details_fragment, container, false)

        aaChartView = root.findViewById<AAChartView>(R.id.chartView_req)
        args = requireArguments()


        initViews()

        //initViews
        root.findViewById<TextView>(R.id.tv_view_bid_history_requirement).setOnClickListener {
            //##Send the crop object


        }
        root.findViewById<MaterialButton>(R.id.mtb_cancel_bid).setOnClickListener {

        }

        root.findViewById<MaterialButton>(R.id.mtb_bid).setOnClickListener {
            //## Send crop item
        }

        root.findViewById<ImageView>(R.id.iv_req_call_buyer).setOnClickListener {
            val i = Intent(Intent.ACTION_CALL, Uri.parse("number"))
            startActivity(i)
        }


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyRequirementDetailsViewModel::class.java)
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
}
