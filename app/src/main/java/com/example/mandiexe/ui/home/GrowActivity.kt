package com.example.mandiexe.ui.home

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mandiexe.R
import com.example.mandiexe.models.body.supply.AddGrowthBody
import com.example.mandiexe.models.body.supply.AddSupplyBody
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.ExternalUtils.createToast
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.viewmodels.AddStockViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_grow.*
import java.text.SimpleDateFormat
import java.util.*

class GrowActivity : AppCompatActivity() {


    private val viewModel: AddStockViewModel by viewModels()
    private val myCalendar = Calendar.getInstance()
    private val TAG = GrowActivity::class.java.simpleName

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grow)

        //UI Init
        etEst = findViewById(R.id.growth_etEstDate)
        etExp = findViewById(R.id.growth_etExpDate)
        //ivLocation = findViewById(R.id.growth_iv_location)
        //  etAddress = findViewById(R.id.growth_actv_address)
        cropName = findViewById(R.id.growth_actv_which_crop)
        cropType = findViewById(R.id.growth_actv_crop_type)
        cropQuantity = findViewById(R.id.growth_actv_quantity)
        offerPrice = findViewById(R.id.growth_actv_price)
        bidSwitch = findViewById(R.id.growth_switch_for_bid)

        tilName = findViewById(R.id.growth_tilWhichCrop)
        tilType = findViewById(R.id.growth_tilCropType)
        tilPrice = findViewById(R.id.growth_tilOfferPrice)
        tilQuantity = findViewById(R.id.growth_tilQuantity)
        //tilAddress = findViewById(R.id.growth_tv_address)
        tilEst = findViewById(R.id.growth_tilEstDate)
        tilExp = findViewById(R.id.growth_tilExpDate)


        //Populate views
        setUpCropNameSpinner()
        setUpVaietyNameSpinner()

        // disable dates before today

        // disable dates before today
        val today = Calendar.getInstance()
        val now = today.timeInMillis

        //Date Instance
        val dateEst =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                view.minDate = now
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfDate()
            }

        val dateExp =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                view.minDate = now
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfExpiry()
            }

        val dateSow =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                view.minDate = now
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelOfSowing()
            }

        //##Requires N
        etEst.setOnClickListener {
            this.let { it1 ->
                DatePickerDialog(
                    it1, dateEst, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }

        //##Requires N
        etExp.setOnClickListener {
            this.let { it1 ->
                DatePickerDialog(
                    it1, dateExp, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }


        findViewById<EditText>(R.id.growth_etSowDate).setOnClickListener {
            this.let { it1 ->
                DatePickerDialog(
                    it1, dateSow, myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }


        //For the bidding items
        bidSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                tilExp.visibility = View.VISIBLE
                tilPrice.visibility = View.VISIBLE
                findViewById<TextView>(R.id.growth_tv_rs).visibility = View.VISIBLE
                findViewById<TextInputLayout>(R.id.growth_tilDescription).visibility = View.VISIBLE

            } else {
                tilExp.visibility = View.GONE
                tilPrice.visibility = View.GONE
                findViewById<TextView>(R.id.growth_tv_rs).visibility = View.GONE
                findViewById<TextInputLayout>(R.id.growth_tilDescription).visibility = View.GONE

            }

        }

        tilName.setOnClickListener {
            setUpCropNameSpinner()
        }

        cropName.setOnClickListener {
            setUpCropNameSpinner()
        }


        findViewById<MaterialButton>(R.id.growth_mtb_add_stock).setOnClickListener {
            if (isValidate()) {

                createStock()

            }
        }

        //Mic units
        findViewById<ImageView>(R.id.growth_mic_crop_name).setOnClickListener {
            makeSearchForItems(RC_NAME)
        }


        findViewById<ImageView>(R.id.growth_mic_crop_type).setOnClickListener {
            makeSearchForItems(RC_TYPE)
        }


    }

    private fun createStock() {


        //Create growth

        val growthBody = AddGrowthBody(
            cropName.text.toString(),
            ExternalUtils.convertDateToReqForm(etEst.text.toString()),
            ExternalUtils.convertDateToReqForm(findViewById<EditText>(R.id.growth_etSowDate).text.toString()),
            cropQuantity.text.toString(),
            cropType.text.toString()
        )

        Log.e("GROW", "In create and body is " + growthBody)
        //Add growth
        viewModel.growthFunction(growthBody)
            .observe(this, androidx.lifecycle.Observer { mResponse ->
                val success = viewModel.successfulGrowth.value

                Log.e(
                    "GROW",
                    "Succes is and bidCheck is " + success + bidSwitch.isChecked.toString()
                )
                if (mResponse.msg == "Crop growth added successfully." && !bidSwitch.isChecked) {
                    createToast(mResponse.msg, this, growth_container_grow)
                    destroyActivity()
                } else if (mResponse.msg == "Crop growth added successfully." && bidSwitch.isChecked) {
                    makeAddCall()
                } else {
                    createSnackbar(mResponse.msg)
                }

            })


    }

    private fun makeAddCall() {


        Log.e("GROW", "In make add call")
        val des = findViewById<EditText>(R.id.growth_etDescription_add_stock)
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

        viewModel.addFunction(body).observe(this, androidx.lifecycle.Observer { mResponse ->

            //Check with the sucessful of it
            if (mResponse.msg == "Supply added successfully.") {
                manageStockCreateResponses(mResponse)
            } else {
                createSnackbar(mResponse.msg)
            }
        })

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
            Locale(pref.getLanguageFromPreference() ?: "en")
        )
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            resources.getString(R.string.searchHead)
        )
        startActivityForResult(Voiceintent, code)

    }

    private fun setUpVaietyNameSpinner() {

        val array: Array<String> = resources.getStringArray(R.array.arr_crop_types)

        val adapter: ArrayAdapter<String>? = this.let {
            ArrayAdapter<String>(
                it, android.R.layout.simple_spinner_item,
                array
            )
        }
        cropType.setAdapter(adapter)

    }

    private fun manageStockCreateResponses(mResponse: AddSupplyResponse?) {
        //On creating this stock

        Log.e("GROW", "In supply success")
        Toast.makeText(this, resources.getString(R.string.supplyAdded), Toast.LENGTH_SHORT)
            .show()
        destroyActivity()
    }

    private fun destroyActivity() {

        Log.e("GROW", "In on destroy")
        onBackPressed()
    }

    private fun createSnackbar(value: String?) {
        Snackbar.make(growth_container_grow, value.toString(), Snackbar.LENGTH_SHORT).show()
    }

    private fun setUpCropNameSpinner() {

        //Crop names

        val array: Array<String> = resources.getStringArray(R.array.arr_crop_names)
        val adapter: ArrayAdapter<String>? = this.let {
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





        if (etEst.text.isEmpty()) {
            isValid = false
            tilEst.error = resources.getString(R.string.etEstError)
        } else {
            tilEst.error = null
        }


        if (findViewById<EditText>(R.id.growth_etSowDate).text.isEmpty()) {
            isValid = false
            findViewById<TextInputLayout>(R.id.growth_tilSowDate).error =
                resources.getString(R.string.etSowError)
        } else {
            findViewById<TextInputLayout>(R.id.growth_tilSowDate).error = null
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


            if (offerPrice.text.isEmpty()) {
                isValid = false
                tilPrice.error = resources.getString(R.string.offerPriceError)
            } else {
                tilPrice.error = null
            }


        }


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
        findViewById<EditText>(R.id.growth_etSowDate).setText(sdf.format(myCalendar.time))

    }

    private fun updateLabelOfExpiry() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etExp.setText(sdf.format(myCalendar.time))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
                cropName.setText(res?.get((0)), false)

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


    override fun onBackPressed() {
        super.onBackPressed()

        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null

        viewModel.successfulGrowth.removeObservers(this)
        viewModel.successfulGrowth.value = null



        finish()
    }
}
