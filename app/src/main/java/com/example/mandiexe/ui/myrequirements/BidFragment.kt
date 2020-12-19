package com.example.mandiexe.ui.myrequirements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mandiexe.R
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

            }
        }
        return root
    }

    private fun initViews() {
    }

    private fun isValidate(): Boolean {
        var isValid = true

        if (etBidPrice.text.isEmpty()) {
            isValid = false
            tilBidPrice.error = resources.getString(R.string.emptyBidError)
        } else if (etBidPrice.text.toString().toInt() >= args.getString("price")!!.toInt()) {
            isValid = false
            tilBidPrice.setBackgroundResource(R.color.red_50)
            tilBidPrice.error = resources.getString(R.string.lesserBidError)
        } else {
            tilBidPrice.error = null
        }



        return isValid

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BidViewModel::class.java)
    }

}
