package com.example.mandiexe.ui.home

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.viewmodels.AddStockViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


class AddStock : Fragment() {


    //Primary constructor
    companion object {
        fun newInstance() = AddStock()
    }

    private lateinit var viewModel: AddStockViewModel
    private lateinit var root: View
    private val myCalendar = Calendar.getInstance()
    private val TAG = AddStock::class.java.simpleName

    //UI variables
    private lateinit var etEst: EditText
    private lateinit var etExp: EditText
    private lateinit var ivLocation: ImageView
    private lateinit var etAddress: EditText
    private lateinit var cropName: AutoCompleteTextView
    private lateinit var cropType: AutoCompleteTextView
    private lateinit var cropQuantity: EditText
    private lateinit var cropQuantityUnit: AutoCompleteTextView
    private lateinit var offerPrice: EditText
    private lateinit var bidSwitch: Switch

    //TILs
    private lateinit var tilName: TextInputLayout
    private lateinit var tilType: TextInputLayout
    private lateinit var tilQuantity: TextInputLayout
    private lateinit var tilPrice: TextInputLayout
    private lateinit var tilAddress: TextInputLayout
    private lateinit var tilEst: TextInputLayout
    private lateinit var tilExp: TextInputLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.add_stock_fragment, container, false)


        //UI Init
        etEst = root.findViewById(R.id.etEstDate)
        etExp = root.findViewById(R.id.etExpDate)
        ivLocation = root.findViewById(R.id.iv_location)
        etAddress = root.findViewById(R.id.actv_address)
        cropName = root.findViewById(R.id.actv_which_crop)
        cropType = root.findViewById(R.id.actv_crop_type)
        cropQuantity = root.findViewById(R.id.actv_quantity)
        cropQuantityUnit = root.findViewById(R.id.actv_quantity_unit)
        offerPrice = root.findViewById(R.id.actv_price)
        bidSwitch = root.findViewById(R.id.switch_for_bid)

        tilName = root.findViewById(R.id.tilWhichCrop)
        tilType = root.findViewById(R.id.tilCropType)
        tilPrice = root.findViewById(R.id.tilOfferPrice)
        tilQuantity = root.findViewById(R.id.tilQuantity)
        tilAddress = root.findViewById(R.id.tv_address)
        tilEst = root.findViewById(R.id.tilEstDate)
        tilExp = root.findViewById(R.id.tilExpDate)


        //The address will either be preset or will come as an argument from Map Activity
        if (arguments != null) {
            //Set the address in the box trimmed
            etAddress.setText(requireArguments().getString("fetchedLocation").toString())

            Log.e(TAG, "Argument str is" + etAddress.text.toString())
        }

        //Date Instance
        val dateEst =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val now = myCalendar.timeInMillis
                view.minDate = now

                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfDate()
            }

        val dateExp =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                val now = myCalendar.timeInMillis
                view.minDate = now
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfExpiry()
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

        ivLocation.setOnClickListener {
            //Start an activity
            root.findNavController().navigate(R.id.action_addStock_to_mapActivity)

        }

        //For the bidding items
        bidSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                tilExp.visibility = View.VISIBLE
            } else {
                tilExp.visibility = View.GONE
            }
        }

        root.findViewById<MaterialButton>(R.id.mtb_add_stock).setOnClickListener {
            if (isValidate()) {

            }
        }

        return root

    }

    private fun isValidate(): Boolean {

        var isValid = true

        if (cropName.text.isEmpty()) {
            isValid = false
            tilName.error = resources.getString(R.string.cropNameError)
        } else {
            tilName.error = null
        }



        if (cropType.text.isEmpty()) {
            isValid = false
            tilType.error = resources.getString(R.string.cropTypeError)
        } else {
            tilType.error = null
        }


        //## Case of zero
        if (cropQuantity.text.isEmpty()) {
            isValid = false
            tilQuantity.error = resources.getString(R.string.cropQuanityError)
        } else {
            tilQuantity.error = null
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

        if (etAddress.text.isEmpty() || etAddress.text.toString() == "null") {
            isValid = false
            tilAddress.error = resources.getString(R.string.addressError)
        } else {
            tilAddress.error = null
        }

        if (bidSwitch.isChecked) {

            if (etExp.text.isEmpty()) {
                isValid = false
                tilExp.error = resources.getString(R.string.expError)
            } else {
                tilExp.error = null
            }
        }





        return isValid
    }


    private fun updateLabelOfDate() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etEst.setText(sdf.format(myCalendar.time))

    }

    private fun updateLabelOfExpiry() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etExp.setText(sdf.format(myCalendar.time))

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddStockViewModel::class.java)

    }


}
