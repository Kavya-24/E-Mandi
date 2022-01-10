package com.example.mandiexe.ui.supply

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.example.mandiexe.R
import com.example.mandiexe.libModel.TranslateViewmodel
import com.example.mandiexe.models.body.supply.AddGrowthBody
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.AddStockViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.add_stock_fragment.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.util.*


class AddStock : AppCompatActivity() {


    //Primary constructor
    companion object {
        fun newInstance() = AddStock()
        lateinit var mActivityInstance: Activity
    }

    //Get an instance for further destruction
    // public lateinit var

    private val viewModel: AddStockViewModel by viewModels()
    private val translateViewModel: TranslateViewmodel by viewModels()

    //private lateinit var root: View
    private lateinit var myCalendar: Calendar
    private val TAG = AddStock::class.java.simpleName

    private lateinit var mHandler: Handler

    //UI variables
    private lateinit var etEst: EditText
    private lateinit var etSow: EditText

    //private lateinit var etAddress: EditText
    private lateinit var cropName: AutoCompleteTextView
    private lateinit var cropType: EditText
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
    private lateinit var mtb: MaterialButton
    private lateinit var pb: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(pref.getLanguageFromPreference(), this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_stock_fragment)


        mActivityInstance = this


        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = resources.getString(R.string.add_crop)
        this.apply {
            tvTitleToolbar.text = resources.getString(R.string.add_crop)
        }
        tb.setNavigationOnClickListener {
            onBackPressed()
        }



        mHandler = Handler()
        //UI Init
        etEst = findViewById(R.id.etEstDate)
        etSow = findViewById<EditText>(R.id.etSowDate)

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
        mtb = findViewById(R.id.mtb_go_to_bidding)
        pb = findViewById(R.id.pb_add_stock)

        cropQuantity.setText(resources.getString(R.string.num50), TextView.BufferType.EDITABLE)


        //Populate views
        setUpCropNameSpinner()



        myCalendar = Calendar.getInstance()
        myCalendar.add(Calendar.MONTH, -1)



        this.apply {

            etEst.setOnClickListener {
                TimeConversionUtils.clickOnDateObject(myCalendar, etEst, this)
            }

            etSow.setOnClickListener {
                TimeConversionUtils.clickOnDateObject(myCalendar, etSow, this)
            }

            ivEst.setOnClickListener {
                TimeConversionUtils.clickOnDateObject(myCalendar, etEst, this)
            }

            ivSow.setOnClickListener {
                TimeConversionUtils.clickOnDateObject(
                    myCalendar,
                    etSow,
                    this
                )
            }

            ivInformation.setOnClickListener {
                getInformationNormalFilters()
            }

            mic_crop_name.setOnClickListener {
                makeSearchForItems(RC_NAME)
            }

            mic_crop_type.setOnClickListener {
                makeSearchForItems(RC_TYPE)
            }


        }


        val bidCheck = findViewById<CheckBox>(R.id.checkbox)
        bidCheck.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                //Then the button says next
                mtb.text = resources.getString(R.string.next)
                mtb.icon = resources.getDrawable(R.drawable.ic_arrow_forward, null)

            } else {
                mtb.text = resources.getString(R.string.add)
                mtb.icon = resources.getDrawable(R.drawable.ic_check_black_24dp, null)

            }
        }

        mtb.setOnClickListener {
            if (isValidate()) {
                if (bidCheck.isChecked) {
                    goToNewSupply()
                } else {
                    createGrowth()
                }
            }
        }


    }

    private fun getInformationNormalFilters() {

        val kgLocale = resources.getString(R.string.kg)
        val kmLocale = resources.getString(R.string.kilometeres)

        val d = AlertDialog.Builder(this)
        d.setTitle(resources.getString(R.string.add_crop))
        d.setMessage(resources.getString(R.string.addStockProposal))
        d.setPositiveButton(resources.getString(R.string.ok)) { _, _ -> }
        d.create().show()
    }

    private fun createGrowth() {


        showProgress(pb, this)
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

    private fun goToNewSupply() {
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

    private fun makeCallForGrowth() {

        //Translate three words
        val transCropName =
            findViewById<TextView>(R.id.tvTempCropName).text.toString()
                .capitalize(Locale("en"))
        val transCropType =
            findViewById<TextView>(R.id.tvTempCropType).text.toString()
                .capitalize((Locale("en")))

        val growthBody = AddGrowthBody(
            transCropName,
            TimeConversionUtils.convertDateToReqForm(etEst.text.toString()),
            TimeConversionUtils.convertDateToReqForm(findViewById<EditText>(R.id.etSowDate).text.toString()),
            cropQuantity.text.toString(),
            transCropType
        )

        Log.e(
            TAG,
            "In add growth" + growthBody.toString()
        )


        clearObservers()
        val mSnackbar = findViewById<CoordinatorLayout>(R.id.container_add_stock)

        viewModel.growthFunction(growthBody, mSnackbar, pb).observe(this, Observer { mResponse ->
            val success = viewModel.successfulGrowth.value
            if (success != null) {
                hideProgress(pb, this)


                UIUtils.createToast(
                    viewModel.messageGrowth.value!!,
                    this,
                    container_add_stock

                )

                onBackPressed()


            } else {
                showProgress(pb, this)
            }
        })


    }

    private fun clearObservers() {
        viewModel.successfulGrowth.value = null
        viewModel.messageGrowth.value = null
        viewModel.successfulGrowth.removeObservers(this)
        viewModel.messageGrowth.removeObservers(this)

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
                && ValidationObject.validateEmptyEditText(
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
        //Now we need to destroy this fragment and on resume of home, go to remove views
        Log.e(TAG, "In on destroy")
        clearObservers()
        super.onBackPressed()
        finish()

    }

    override fun onPause() {

        Log.e(TAG, "On Pause")
        super.onPause()

    }

}
