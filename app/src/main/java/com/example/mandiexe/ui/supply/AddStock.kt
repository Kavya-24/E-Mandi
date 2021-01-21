package com.example.mandiexe.ui.supply

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.lib.ConversionTable
import com.example.mandiexe.lib.TranslateViewmodel
import com.example.mandiexe.models.body.supply.AddGrowthBody
import com.example.mandiexe.models.body.supply.AddSupplyBody
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.AddStockViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.add_stock_fragment.*
import java.util.*


class AddStock : Fragment() {


    //Primary constructor
    companion object {
        fun newInstance() = AddStock()
    }

    private val viewModel: AddStockViewModel by viewModels()
    private val translateViewModel: TranslateViewmodel by viewModels()

    private lateinit var root: View
    private lateinit var myCalendar: Calendar
    private val TAG = AddStock::class.java.simpleName

    private lateinit var mHandler: Handler

    //UI variables
    private lateinit var etEst: EditText
    private lateinit var etExp: EditText
    private lateinit var ivLocation: ImageView

    //private lateinit var etAddress: EditText
    private lateinit var cropName: AutoCompleteTextView
    private lateinit var cropType: AutoCompleteTextView
    private lateinit var cropDes: EditText

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

        mHandler = Handler()
        //UI Init
        etEst = root.findViewById(R.id.etEstDate)
        etExp = root.findViewById(R.id.etExpDate)
        //ivLocation = root.findViewById(R.id.iv_location)
        //  etAddress = root.findViewById(R.id.actv_address)
        cropName = root.findViewById(R.id.actv_which_crop)
        cropType = root.findViewById(R.id.actv_crop_type)
        cropQuantity = root.findViewById(R.id.actv_quantity)
        cropDes = root.findViewById(R.id.etDescription_add_stock)
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


        // disable dates before today
        myCalendar = Calendar.getInstance()
        myCalendar.add(Calendar.MONTH, 1)

        etEst.setOnClickListener {
            TimeConversionUtils.clickOnDateObject(myCalendar, etEst, requireContext())
        }

        //##Requires N
        etExp.setOnClickListener {
            TimeConversionUtils.clickOnDateObject(myCalendar, etExp, requireContext())
        }

        root.findViewById<EditText>(R.id.etSowDate).setOnClickListener {
            TimeConversionUtils.clickOnDateObject(
                myCalendar,
                root.findViewById<EditText>(R.id.etSowDate),
                requireContext()
            )
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
        UIUtils.getSpinnerAdapter(R.array.arr_crop_types, cropType, requireContext())
    }

    private fun createStock() {


        //Start Progress bar
        root.findViewById<ProgressBar>(R.id.pb_add_stock).visibility = View.VISIBLE

        //Get the translation
        getTranslations()

        if (getValidTranslations()) {

            makeCalls()

        } else {

            //When the things have not been translated
            //The errors will be logged
            //1. Wait for translation to be made (LiveData)
            //Run a handler
            //Make call after 5 seconds with whatever data is there
            mHandler.postDelayed({ makeCalls() }, 5000)
        }


    }

    private fun makeCalls() {


        //Translate three words
        val transCropName =
            root.findViewById<TextView>(R.id.tvTempCropName).text.toString()
                .capitalize(Locale("en"))
        val transCropType =
            root.findViewById<TextView>(R.id.tvTempCropType).text.toString()
                .capitalize((Locale("en")))
        var transDesc = cropDes.text.toString()

        if (cropDes.text.toString() != resources.getString(R.string.noDesc)) {
            //If it has something, use uts translated values
            transDesc = root.findViewById<TextView>(R.id.tvTempCropDesc).text.toString()
                .capitalize((Locale("en")))

        }

        Log.e(TAG, "Translated values are " + transCropName + transCropType + transDesc)
        val body = AddSupplyBody(
            offerPrice.text.toString(),
            transCropName ?: cropName.text.toString().capitalize(Locale.ROOT),
            TimeConversionUtils.convertDateToReqForm(etEst.text.toString()),
            transDesc,
            TimeConversionUtils.convertDateToReqForm(etExp.text.toString()),
            "0",
            transCropType ?: cropType.text.toString().capitalize(Locale.ROOT)
        )

        //Create growth
        val growthBody = AddGrowthBody(
            transCropName ?: cropName.text.toString().capitalize(Locale.ROOT),
            TimeConversionUtils.convertDateToReqForm(etEst.text.toString()),
            TimeConversionUtils.convertDateToReqForm(root.findViewById<EditText>(R.id.etSowDate).text.toString()),
            cropQuantity.text.toString(),
            transCropType ?: cropType.text.toString().capitalize(Locale.ROOT)
        )

        Log.e(
            TAG,
            "AddSupply Body \n" + body.toString() + "\n Add growth body" + growthBody.toString()
        )


        viewModel.growthFunction(growthBody).observe(viewLifecycleOwner, Observer { mResponse ->
            val success = viewModel.successfulGrowth.value
            if (success != null) {
                Log.e(
                    TAG,
                    "In growth function and success is " + success + viewModel.messageGrowth
                )

                if (success == true) {
                    Log.e(TAG, "In successfully added growth")
                } else {
                    Log.e(TAG, "In failed added growth")
                }

            }
        })

        viewModel.addFunction(body).observe(viewLifecycleOwner, Observer { mResponse ->

            //Check with the sucessful of it
            if (viewModel.successful.value == false) {
                Log.e(TAG, viewModel.message.toString())
                UIUtils.createSnackbar(
                    viewModel.message.value,
                    requireContext(),
                    container_add_stock
                )
            } else {
                manageStockCreateResponses(viewModel.addStock.value)
            }
        })

        //Stop Progress bar
        root.findViewById<ProgressBar>(R.id.pb_add_stock).visibility = View.GONE

    }

    private fun getValidTranslations(): Boolean {

        return ValidationObject.validateTranslations(
            root.findViewById<TextView>(R.id.tvTempCropName),
            requireContext()
        ) && ValidationObject.validateTranslations(
            root.findViewById<TextView>(R.id.tvTempCropType),
            requireContext()
        ) && ValidationObject.validateTranslations(
            root.findViewById<TextView>(R.id.tvTempCropDesc),
            requireContext()
        )

    }

    private fun getTranslations() {
        //Run an async task to get the values for the three categories
        OfflineTranslate.translateToEnglish(
            requireContext(),
            cropName.text.toString(),
            root.findViewById<TextView>(R.id.tvTempCropName)
        )
        OfflineTranslate.translateToEnglish(
            requireContext(),
            cropType.text.toString(),
            root.findViewById<TextView>(R.id.tvTempCropType)
        )
        OfflineTranslate.translateToEnglish(
            requireContext(),
            cropDes.text.toString(),
            root.findViewById<TextView>(R.id.tvTempCropDesc)
        )

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

    private fun setUpCropNameSpinner() {

        UIUtils.getSpinnerAdapter(R.array.arr_crop_names, cropName, requireContext())
    }

    private fun isValidate(): Boolean {

        val etSow = root.findViewById<EditText>(R.id.etSowDate)
        val tilSow = root.findViewById<TextInputLayout>(R.id.tilSowDate)

        return ValidationObject.validateEmptyView(
            cropName,
            tilName,
            R.string.cropNameError,
            requireContext()
        )
                && ValidationObject.validateEmptyView(
            cropType,
            tilType,
            R.string.cropTypeError,
            requireContext()
        )
                && ValidationObject.validateEmptyEditText(
            cropQuantity,
            tilQuantity,
            R.string.cropQuanityError,
            requireContext()
        )
                && ValidationObject.validateEmptyEditText(
            offerPrice,
            tilPrice,
            R.string.offerPriceError,
            requireContext()
        )
                && ValidationObject.validateEmptyEditText(
            etSow,
            tilSow,
            R.string.etSowError,
            requireContext()
        )
                && ValidationObject.validateEmptyEditText(
            etEst,
            tilEst,
            R.string.etEstError,
            requireContext()
        )
                && ValidationObject.validateEmptyEditText(
            etExp,
            tilExp,
            R.string.expError,
            requireContext()
        )
                && TimeConversionUtils.validateDates(
            etSow,
            etEst,
            R.string.etEstLessThanSow,
            R.string.etEstLessIncomplete,
            etEst,
            tilEst,
            requireContext()
        )
                && TimeConversionUtils.validateDates(
            etSow,
            etExp,
            R.string.expLess,
            R.string.expm20,
            etExp,
            tilExp,
            requireContext()
        )


    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
                val transformedString: String? =
                    resultInEnglish?.let { conversionTable.transform(it) }
                cropName.setText(OfflineTranslate.transliterateToDefault(resultInEnglish))


            }
        } else if (requestCode == RC_TYPE) {
            if (data != null) {
                //Put result
                val res: java.util.ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val resultInEnglish = res?.get(0)
                val conversionTable = ConversionTable()
                val transformedString: String? =
                    resultInEnglish?.let { conversionTable.transform(it) }
                cropType.setText(OfflineTranslate.transliterateToDefault(resultInEnglish))

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
