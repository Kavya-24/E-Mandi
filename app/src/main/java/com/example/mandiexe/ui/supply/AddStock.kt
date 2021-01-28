package com.example.mandiexe.ui.supply

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.example.mandiexe.R
import com.example.mandiexe.libModel.TranslateViewmodel
import com.example.mandiexe.models.body.supply.AddGrowthBody
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.createToast
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.AddStockViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.add_stock_fragment.*
import java.util.*


class AddStock : AppCompatActivity() {


    //Primary constructor
    companion object {
        fun newInstance() = AddStock()
    }

    private val viewModel: AddStockViewModel by viewModels()
    private val translateViewModel: TranslateViewmodel by viewModels()

    //private lateinit var root: View
    private lateinit var myCalendar: Calendar
    private val TAG = AddStock::class.java.simpleName

    private lateinit var mHandler: Handler

    //UI variables
    private lateinit var etEst: EditText

    //private lateinit var etAddress: EditText
    private lateinit var cropName: AutoCompleteTextView
    private lateinit var cropType: AutoCompleteTextView
    private lateinit var cropQuantity: EditText


    //TILs
    private lateinit var tilName: TextInputLayout
    private lateinit var tilType: TextInputLayout
    private lateinit var tilQuantity: TextInputLayout

    // private lateinit var tilAddress: TextInputLayout
    private lateinit var tilEst: TextInputLayout


    private val RC_MAP_STOCK_ADD = 111

    private val ACTION_VOICE_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION"
    private val RC_NAME = 1
    private val RC_TYPE = 2
    private val pref = PreferenceUtil

    private lateinit var args: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(pref.getLanguageFromPreference(), this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_stock_fragment)

        if (intent?.getBundleExtra("bundle") != null) {
            //When there is an argumenet of cimpletetion
            onBackPressed()
        }


        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = resources.getString(R.string.add_crop)
        tb.setNavigationOnClickListener {
            onBackPressed()
        }

        mHandler = Handler()
        //UI Init
        etEst = findViewById(R.id.etEstDate)
        //ivLocation = findViewById(R.id.iv_location)
        //  etAddress = findViewById(R.id.actv_address)
        cropName = findViewById(R.id.actv_which_crop)
        cropType = findViewById(R.id.actv_crop_type)
        cropQuantity = findViewById(R.id.actv_quantity)
        //bidSwitch = findViewById(R.id.switch_for_bid)

        tilName = findViewById(R.id.tilWhichCrop)
        tilType = findViewById(R.id.tilCropType)
        tilQuantity = findViewById(R.id.tilQuantity)
        //tilAddress = findViewById(R.id.tv_address)
        tilEst = findViewById(R.id.tilEstDate)
        val etSow = findViewById<EditText>(R.id.etSowDate)


        //Populate views
        setUpCropNameSpinner()
        setUpVaietyNameSpinner()


        // disable dates before today
        myCalendar = Calendar.getInstance()
        myCalendar.add(Calendar.MONTH, 1)

        etEst.setOnClickListener {
            TimeConversionUtils.clickOnDateObject(myCalendar, etEst, this)
        }


        findViewById<EditText>(R.id.etSowDate).setOnClickListener {
            TimeConversionUtils.clickOnDateObject(
                myCalendar,
                findViewById<EditText>(R.id.etSowDate),
                this
            )
        }


        //Mic units
        findViewById<ImageView>(R.id.mic_crop_name).setOnClickListener {
            makeSearchForItems(RC_NAME)
        }


        findViewById<ImageView>(R.id.mic_crop_type).setOnClickListener {
            makeSearchForItems(RC_TYPE)
        }


        findViewById<MaterialButton>(R.id.mtb_go_to_bidding).setOnClickListener {

            if (isValidate()) {
                val bundle = bundleOf(
                    "NAME" to cropName.text.toString(),
                    "TYPE" to cropType.text.toString(),
                    "QUANTITY" to cropQuantity.text.toString(),
                    "SOW" to etSow.text.toString(),
                    "EST" to etEst.text.toString()
                )

                val i = Intent(this, AddStockPage2::class.java)
                i.putExtra("bundle", bundle)
                startActivity(i)
            }
        }


        findViewById<MaterialButton>(R.id.mtb_add_without_bidding).setOnClickListener {
            if (isValidate()) {
                createGrowth()
            }
        }


    }


    private fun createGrowth() {

        findViewById<ProgressBar>(R.id.pb_add_stock).visibility = View.VISIBLE
        getTranslations()


        if (getValidTranslations()) {

            makeCallForGrowth()

        } else {

            //When the things have not been translated
            //The errors will be logged
            //1. Wait for translation to be made (LiveData)
            //Run a handler
            //Make call after 5 seconds with whatever data is there
            mHandler.postDelayed({ makeCallForGrowth() }, 5000)
        }


    }


    private fun makeCallForGrowth() {

        //Translate three words
        val transCropName =
            findViewById<TextView>(R.id.tvTempCropName).text.toString()
                .capitalize(Locale("en"))
        val transCropType =
            findViewById<TextView>(R.id.tvTempCropType).text.toString()
                .capitalize((Locale("en")))

        val growthBody = AddGrowthBody(
            transCropName ?: cropName.text.toString().capitalize(Locale.ROOT),
            TimeConversionUtils.convertDateToReqForm(etEst.text.toString()),
            TimeConversionUtils.convertDateToReqForm(findViewById<EditText>(R.id.etSowDate).text.toString()),
            cropQuantity.text.toString(),
            transCropType ?: cropType.text.toString().capitalize(Locale.ROOT)
        )

        Log.e(
            TAG,
            "In add growth" + growthBody.toString()
        )
        viewModel.growthFunction(growthBody).observe(this, Observer { mResponse ->
            val success = viewModel.successfulGrowth.value
            if (success != null) {
                Log.e(
                    TAG,
                    "In growth function and success is " + success + viewModel.messageGrowth
                )

                if (success == true) {
                    Log.e(TAG, "In successfully added growth")
                } else if (viewModel.messageGrowth.value == "Crop growth added successfully.") {
                    Log.e(TAG, "In success ")
                    createToast(
                        this.resources.getString(R.string.supplyAdded),
                        this,
                        container_add_stock
                    )
                    onBackPressed()
                } else {
                    UIUtils.createSnackbar(
                        viewModel.messageGrowth.value,
                        this,
                        container_add_stock
                    )
                }
            }
        })


    }

    private fun makeSearchForItems(code: Int) {

        val mLanguage = pref.getLanguageFromPreference() ?: "en"
        val Voiceintent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        //Put language
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            mLanguage
        )

        Voiceintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, mLanguage)
        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
            mLanguage
        )

        Voiceintent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            resources.getString(R.string.searchHead)
        )
        startActivityForResult(Voiceintent, code)

    }

    private fun setUpVaietyNameSpinner() {
        UIUtils.getSpinnerAdapter(R.array.arr_crop_types, cropType, this)
    }

    private fun getValidTranslations(): Boolean {

        return ValidationObject.validateTranslations(
            findViewById<TextView>(R.id.tvTempCropName),
            this
        ) && ValidationObject.validateTranslations(
            findViewById<TextView>(R.id.tvTempCropType),
            this
        )

    }


    private fun getTranslations() {
        //Run an async task to get the values for the three categories
        OfflineTranslate.translateToEnglish(
            this,
            cropName.text.toString(),
            findViewById<TextView>(R.id.tvTempCropName)
        )
        OfflineTranslate.translateToEnglish(
            this,
            cropType.text.toString(),
            findViewById<TextView>(R.id.tvTempCropType)
        )
    }

    private fun manageStockCreateResponses(mResponse: AddSupplyResponse?) {
        //On creating this stock
        Toast.makeText(
            this,
            resources.getString(R.string.supplyAdded),
            Toast.LENGTH_SHORT
        )
            .show()

    }

    private fun setUpCropNameSpinner() {

        UIUtils.getSpinnerAdapter(R.array.arr_crop_names, cropName, this)
    }

    private fun isValidate(): Boolean {

        val etSow = findViewById<EditText>(R.id.etSowDate)
        val tilSow = findViewById<TextInputLayout>(R.id.tilSowDate)

        return ValidationObject.validateEmptyView(
            cropName,
            tilName,
            R.string.cropNameError,
            this
        )
                && ValidationObject.validateEmptyView(
            cropType,
            tilType,
            R.string.cropTypeError,
            this
        )
                && ValidationObject.validateEmptyEditText(
            cropQuantity,
            tilQuantity,
            R.string.cropQuanityError,
            this
        )

                && ValidationObject.validateEmptyEditText(
            etSow,
            tilSow,
            R.string.etSowError,
            this
        )
                && ValidationObject.validateEmptyEditText(
            etEst,
            tilEst,
            R.string.etEstError,
            this
        )

                && TimeConversionUtils.validateDates(
            etSow,
            etEst,
            R.string.etEstLessThanSow,
            R.string.etEstLessIncomplete,
            etEst,
            tilEst,
            this
        )


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
                val resultInDefault = res?.get(0)
                cropName.setText(resultInDefault)


            }
        } else if (requestCode == RC_TYPE) {
            if (data != null) {
                //Put result
                val res: java.util.ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val resultInEnglish = res?.get(0)
                //  val conversionTable = onversionTable()
                cropType.setText(resultInEnglish)

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        //Now we need to destroy this fragment and on resume of home, go to remove views
        Log.e(TAG, "In on destroy")
        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null
        finish()


    }
//    override fun onDestroy() {
//
//
//        val navController = findNavController()
//        navController.navigateUp()
//
//        super.onDestroy()
//    }
}
