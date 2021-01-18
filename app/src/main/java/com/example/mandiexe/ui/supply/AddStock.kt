package com.example.mandiexe.ui.supply

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.icu.text.Transliterator
import android.os.Bundle
import android.speech.RecognizerIntent
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
import com.example.mandiexe.lib.ConversionTable
import com.example.mandiexe.models.body.supply.AddGrowthBody
import com.example.mandiexe.models.body.supply.AddSupplyBody
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.auth.PreferenceUtil
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

    private val ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION"
    private val RC_NAME = 1
    private val RC_TYPE = 2
    private val pref = PreferenceUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.add_stock_fragment, container, false)

//        val tb = root.findViewById<Toolbar>(R.id.toolbarExternal)
//        tb.title = resources.getString(R.string.add_stock)
//        tb.setNavigationOnClickListener {
//            onBackPressed()
//        }

        //UI Init
        etEst = root.findViewById(R.id.etEstDate)
        etExp = root.findViewById(R.id.etExpDate)
        //ivLocation = root.findViewById(R.id.iv_location)
        //  etAddress = root.findViewById(R.id.actv_address)
        cropName = root.findViewById(R.id.actv_which_crop)
        cropType = root.findViewById(R.id.actv_crop_type)
        cropQuantity = root.findViewById(R.id.actv_quantity)
        offerPrice = root.findViewById(R.id.actv_price)
        //bidSwitch = root.findViewById(R.id.switch_for_bid)

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

        // disable dates before today
        val today = Calendar.getInstance()
        val now = today.timeInMillis

        //Date Instance
        val dateEst =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                view.minDate = now
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfDate()
            }

        val dateExp =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                view.minDate = now
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfExpiry()
            }

        val dateSow =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                view.minDate = now
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfSowing()
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

        root.findViewById<EditText>(R.id.etSowDate).setOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1, dateSow, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }

//        ivLocation.setOnClickListener {
//
//            //Start an activity
//            val i = Intent(this(), MapActivity::class.java)
//            startActivityForResult(i, RC_MAP_STOCK_ADD)
//
//        }

        //For the bidding items
//        bidSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
//
//            if (isChecked) {
//                tilExp.visibility = View.VISIBLE
//            } else {
//                tilExp.visibility = View.GONE
//            }
//        }

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

        //Mic units
        root.findViewById<ImageView>(R.id.mic_crop_name).setOnClickListener {
            makeSearchForItems(RC_NAME)
        }


        root.findViewById<ImageView>(R.id.mic_crop_type).setOnClickListener {
            makeSearchForItems(RC_TYPE)
        }


        return root

    }


    private fun makeSearchForItems(code: Int) {
        val Voiceintent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        //Put language
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale(pref.getLanguageFromPreference() + "-IN")
        )
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            resources.getString(R.string.searchHead)
        )
        startActivityForResult(Voiceintent, code)

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
            ExternalUtils.convertDateToReqForm(etEst.text.toString()),
            str,
            ExternalUtils.convertDateToReqForm(etExp.text.toString()),
            "0",
            cropType.text.toString()
        )

        //Create growth
        val growthBody = AddGrowthBody(
            cropName.text.toString(),
            ExternalUtils.convertDateToReqForm(etEst.text.toString()),
            ExternalUtils.convertDateToReqForm(root.findViewById<EditText>(R.id.etSowDate).text.toString()),
            cropQuantity.text.toString(),
            cropType.text.toString()
        )
        viewModel.growthFunction(growthBody).observe(viewLifecycleOwner, Observer { mResponse ->
            val success = viewModel.successfulGrowth.value
            if (success != null) {
                Log.e(TAG, "In growth function and success is " + success + viewModel.messageGrowth)

            }
        })

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
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.supplyAdded),
            Toast.LENGTH_SHORT
        )
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


        if (root.findViewById<EditText>(R.id.etSowDate).text.isEmpty()) {
            isValid = false
            root.findViewById<TextInputLayout>(R.id.tilSowDate).error =
                resources.getString(R.string.etSowError)
        } else {
            root.findViewById<TextInputLayout>(R.id.tilSowDate).error = null
        }


//        if (etAddress.text.isEmpty() || etAddress.text.toString() == "null") {
//            isValid = false
//            tilAddress.error = resources.getString(R.string.addressError)
//        } else {
//            tilAddress.error = null
//        }

        //    if (bidSwitch.isChecked) {

        if (etExp.text.isEmpty()) {
            isValid = false
            tilExp.error = resources.getString(R.string.expError)
        } else {
            tilExp.error = null
        }
        //  }


        return isValid
    }

    private fun updateLabelOfDate() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etEst.setText(sdf.format(myCalendar.time))

    }

    private fun updateLabelOfSowing() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        root.findViewById<EditText>(R.id.etSowDate).setText(sdf.format(myCalendar.time))

    }

    private fun updateLabelOfExpiry() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etExp.setText(sdf.format(myCalendar.time))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      //   super.onActivityResult(requestCode, resultCode, data)

        //Get the map data result
        Log.e(
            TAG,
            "In activty result and req is $requestCode and res $resultCode and data is ${
                data?.getStringExtra("fetchedLocation").toString()
            }"
        )
//        etAddress.setText(data?.getStringExtra("fetchedLocation").toString())


        if (requestCode == RC_NAME) {

            if (data != null) {
                //Put result
                val res: java.util.ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val resultInEnglish = res?.get(0)
                val conversionTable = ConversionTable()
                val transformedString: String? = resultInEnglish?.let { conversionTable.transform(it) }

                //Val translietrated
              //  val tx = resultInEnglish?.let { ExternalUtils.transliterateFromEnglishToDefault(it) }
                cropName.setText(transformedString)
              //  Log.e(TAG, "Res in eng " + resultInEnglish + " transf" + transformedString + tx)

            }
        } else if (requestCode == RC_TYPE) {
            if (data != null) {
                //Put result
                val res: java.util.ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                cropType.setText(res?.get((0)), false)
            }
        }

    }

    override fun onDestroy() {

        //Now we need to destroy this fragment and on resume of home, go to remove views
        Log.e(TAG, "In on destroy")
        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null
        val navController = findNavController()
        navController.navigateUp()

        super.onDestroy()
    }
}
