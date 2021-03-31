package com.example.mandiexe.ui.demands

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.R
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.viewmodels.BidViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class BidFragment : Fragment() {

    companion object {
        fun newInstance() = BidFragment()
    }

    private lateinit var viewModel: BidViewModel
    private lateinit var root: View
    private lateinit var args: Bundle

    private lateinit var tilBidPrice: TextInputLayout
    private lateinit var etBidPrice: EditText


    private lateinit var d: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var tempRef: androidx.appcompat.app.AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.bid_fragment, container, false)
        args = requireArguments()

        //UI Init
        tilBidPrice = root.findViewById(R.id.tilMyBidPrice)
        etBidPrice = root.findViewById(R.id.actv_my_bid_price)

        //Init the views()
        initViews()


        root.findViewById<MaterialButton>(R.id.mtb_place_bid).setOnClickListener {
            if (isValidate()) {
                createDialog()
            }
        }
        return root
    }

    private fun createDialog() {

        d = androidx.appcompat.app.AlertDialog.Builder(ApplicationUtils.getContext())
        val v = layoutInflater.inflate(R.layout.layout_place_bid, null)
        d.setView(v)


        val tvInitial = v.findViewById<TextView>(R.id.et_initial_bid)
        val tvMyBid = v.findViewById<TextView>(R.id.et_my_bid)
        val tvDiffernece = v.findViewById<TextView>(R.id.et_difference)

        tvInitial.text = resources.getString(R.string.initPrice) + args.getString("ASK_PRICE")
        tvMyBid.text =
            resources.getString(R.string.myBidPrice) + args.getString(etBidPrice.text.toString())
        val ans = getDifference()
        if (ans > 0) {
            tvDiffernece.text = "+" + ans.toString()
            tvDiffernece.setTextColor(resources.getColor(R.color.deltaGreen))
        } else if (ans < 0) {
            tvDiffernece.text = ans.toString()
            tvDiffernece.setTextColor(resources.getColor(R.color.deltaRed))
        } else if (ans == 0) {
            tvDiffernece.text = resources.getString(R.string.noChangeInBid)
            tvDiffernece.setTextColor(resources.getColor(R.color.wildColor))
        }


        //Positive and negative buttons

        //Create observer on Text

        d.setPositiveButton("Register") { _, _ ->

            createBid()


        }
        d.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

        }
        d.create()

        tempRef = d.create()
        d.show()


    }

    private fun getDifference(): Int {
        val askPrice = args.getString("ASK_PRICE").toString().toInt()
        val myBid = etBidPrice.text.toString().toInt()

        val ans = askPrice - myBid
        return ans
    }

    private fun createBid() {
        tempRef.dismiss()
        //Create bid

    }

    private fun initViews() {
    }

    private fun isValidate(): Boolean {
        var isValid = true

        if (etBidPrice.text.isEmpty()) {
            isValid = false
            tilBidPrice.error = resources.getString(R.string.emptyBidError)
        }
//        else if (etBidPrice.text.toString().toInt() >= args.getString("price")!!.toInt()) {
//            isValid = false
//            tilBidPrice.setBackgroundResource(R.color.red_50)
//            tilBidPrice.error = resources.getString(R.string.lesserBidError)
//        }
        else {
            tilBidPrice.error = null
        }



        return isValid

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BidViewModel::class.java)
    }

}
