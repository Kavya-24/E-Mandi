package com.example.mandiexe.ui.supply

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.models.body.supply.AddSupplyBody
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.viewmodels.AddStockViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.add_stock_fragment.*
import java.text.SimpleDateFormat
import java.util.*


class AddStock : Fragment() {


    //Primary constructor
    companion object {
        fun newInstance() = AddStock()
    }

    private val viewModel: AddStockViewModel by viewModels()
    private lateinit var root: View
    private val myCalendar = Calendar.getInstance()
    private val TAG = AddStock::class.java.simpleName

    //UI variables
    private lateinit var etEst: EditText
    private lateinit var etExp: EditText
    private lateinit var ivLocation: ImageView

    //private lateinit var etAddress: EditText
    private lateinit var cropName: AutoCompleteTextView
    private lateinit var cropType: AutoCompleteTextView
    private lateinit var cropQuantity: EditText
    private lateinit var offerPrice: EditText
    private lateinit var bidSwitch: Switch

    //TILs
    private lateinit var tilName: TextInputLayout
    private lateinit var tilType: TextInputLayout
    private lateinit var tilQuantity: TextInputLayout
    private lateinit var tilPrice: TextInputLayout

    // private lateinit var tilAddress: TextInputLayout
    private lateinit var tilEst: TextInputLayout
    private lateinit var tilExp: TextInputLayout


    private val RC_MAP_STOCK_ADD = 111

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.add_stock_fragment, container, false)


        //UI Init
        etEst = root.findViewById(R.id.etEstDate)
        etExp = root.findViewById(R.id.etExpDate)
        //ivLocation = root.findViewById(R.id.iv_location)
        //  etAddress = root.findViewById(R.id.actv_address)
        cropName = root.findViewById(R.id.actv_which_crop)
        cropType = root.findViewById(R.id.actv_crop_type)
        cropQuantity = root.findViewById(R.id.actv_quantity)
        offerPrice = root.findViewById(R.id.actv_price)
        bidSwitch = root.findViewById(R.id.switch_for_bid)

        tilName = root.findViewById(R.id.tilWhichCrop)
        tilType = root.findViewById(R.id.tilCropType)
        tilPrice = root.findViewById(R.id.tilOfferPrice)
        tilQuantity = root.findViewById(R.id.tilQuantity)
        //tilAddress = root.findViewById(R.id.tv_address)
        tilEst = root.findViewById(R.id.tilEstDate)
        tilExp = root.findViewById(R.id.tilExpDate)


        //Populate views
        setUpCropNameSpinner()
        setUpVaietyNameSpinner()


        //The address will either be preset or will come as an argument from Map Activity
//        if (arguments != null) {
//            //Set the address in the box trimmed
//            etAddress.setText(requireArguments().getString("fetchedLocation").toString())
//
//            Log.e(TAG, "Argument str is" + etAddress.text.toString())
//        }

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

//        ivLocation.setOnClickListener {
//
//            //Start an activity
//            val i = Intent(requireContext(), MapActivity::class.java)
//            startActivityForResult(i, RC_MAP_STOCK_ADD)
//
//        }

        //For the bidding items
        bidSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                tilExp.visibility = View.VISIBLE
            } else {
                tilExp.visibility = View.GONE
            }
        }

        tilName.setOnClickListener {
            setUpCropNameSpinner()
        }

        cropName.setOnClickListener {
            setUpCropNameSpinner()
        }


        root.findViewById<MaterialButton>(R.id.mtb_add_stock).setOnClickListener {
            if (isValidate()) {

                createStock()

            }
        }

        return root

    }

    private fun setUpVaietyNameSpinner() {

        val array: Array<String> = resources.getStringArray(R.array.arr_crop_types)
        val adapter: ArrayAdapter<String>? = context?.let {
            ArrayAdapter<String>(
                it, android.R.layout.simple_spinner_item,
                array
            )
        }
        cropType.setAdapter(adapter)

    }

    private fun createStock() {

        val des = root.findViewById<EditText>(R.id.etDescription_add_stock)
        var str = "NA"
        if (!des.text.isEmpty()) {
            str = des.text.toString()
        }

        val body = AddSupplyBody(
            offerPrice.text.toString(),
            cropName.text.toString(),
            etEst.text.toString(),
            str,
            etExp.text.toString(),
            "0",
            cropType.text.toString()
        )

        viewModel.addFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->

            //Check with the sucessful of it
            if (viewModel.successful.value == false) {
                createSnackbar(viewModel.message.value)
            } else {
                manageStockCreateResponses(viewModel.addStock.value)
            }
        })


    }

    private fun manageStockCreateResponses(mResponse: AddSupplyResponse?) {
        //On creating this stock
        Toast.makeText(context, resources.getString(R.string.supplyAdded), Toast.LENGTH_SHORT)
            .show()
        onDestroy()
    }

    private fun createSnackbar(value: String?) {
        Snackbar.make(container_add_stock, value.toString(), Snackbar.LENGTH_SHORT).show()
    }

    private fun setUpCropNameSpinner() {

        //Crop names

        val array: Array<String> = resources.getStringArray(R.array.arr_crop_names)
        val adapter: ArrayAdapter<String>? = context?.let {
            ArrayAdapter<String>(
                it, android.R.layout.simple_spinner_item,
                array
            )
        }
        cropName.setAdapter(adapter)


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

//        if (etAddress.text.isEmpty() || etAddress.text.toString() == "null") {
//            isValid = false
//            tilAddress.error = resources.getString(R.string.addressError)
//        } else {
//            tilAddress.error = null
//        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Get the map data result
        Log.e(
            TAG,
            "In activty result and req is $requestCode and res $resultCode and data is ${
                data?.getStringExtra("fetchedLocation").toString()
            }"
        )
//        etAddress.setText(data?.getStringExtra("fetchedLocation").toString())


    }

    override fun onDestroy() {

        //Now we need to destroy this fragment and on resume of home, go to remove views
        Log.e(TAG, "In on destroy")
        val navController = findNavController()
        navController.navigateUp()
        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null


        super.onDestroy()
    }
}