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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mandiexe.R
import com.example.mandiexe.libModel.TranslateViewmodel
import com.example.mandiexe.models.body.supply.AddGrowthBody
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.UIUtils
import com.example.mandiexe.utils.usables.UIUtils.createToast
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.AddStockViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_add_stock_page2.*
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


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.add_stock_fragment, container, false)

        mHandler = Handler()
        //UI Init
        etEst = root.findViewById(R.id.etEstDate)
        //ivLocation = root.findViewById(R.id.iv_location)
        //  etAddress = root.findViewById(R.id.actv_address)
        cropName = root.findViewById(R.id.actv_which_crop)
        cropType = root.findViewById(R.id.actv_crop_type)
        cropQuantity = root.findViewById(R.id.actv_quantity)
        //bidSwitch = root.findViewById(R.id.switch_for_bid)

        tilName = root.findViewById(R.id.tilWhichCrop)
        tilType = root.findViewById(R.id.tilCropType)
        tilQuantity = root.findViewById(R.id.tilQuantity)
        //tilAddress = root.findViewById(R.id.tv_address)
        tilEst = root.findViewById(R.id.tilEstDate)
        val etSow = root.findViewById<EditText>(R.id.etSowDate)


        //Populate views
        setUpCropNameSpinner()
        setUpVaietyNameSpinner()


        // disable dates before today
        myCalendar = Calendar.getInstance()
        myCalendar.add(Calendar.MONTH, 1)

        etEst.setOnClickListener {
            TimeConversionUtils.clickOnDateObject(myCalendar, etEst, requireContext())
        }


        root.findViewById<EditText>(R.id.etSowDate).setOnClickListener {
            TimeConversionUtils.clickOnDateObject(
                myCalendar,
                root.findViewById<EditText>(R.id.etSowDate),
                requireContext()
            )
        }


        //Mic units
        root.findViewById<ImageView>(R.id.mic_crop_name).setOnClickListener {
            makeSearchForItems(RC_NAME)
        }


        root.findViewById<ImageView>(R.id.mic_crop_type).setOnClickListener {
            makeSearchForItems(RC_TYPE)
        }


        root.findViewById<MaterialButton>(R.id.mtb_go_to_bidding).setOnClickListener {

            if(isValidate()) {
                val bundle = bundleOf(
                    "NAME" to cropName.text.toString(),
                    "TYPE" to cropType.text.toString(),
                    "QUANTITY" to cropQuantity.text.toString(),
                    "SOW" to etSow.text.toString(),
                    "EST" to etEst.text.toString()
                )

                val i = Intent(requireContext(), AddStockPage2::class.java)
                i.putExtra("bundle", bundle)
                startActivity(i)
            }
        }


        root.findViewById<MaterialButton>(R.id.mtb_add_without_bidding).setOnClickListener {
            if (isValidate()) {
                createGrowth()
            }
        }
        return root

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createGrowth() {

        root.findViewById<ProgressBar>(R.id.pb_add_stock).visibility = View.VISIBLE
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun makeCallForGrowth() {

        //Translate three words
        val transCropName =
            root.findViewById<TextView>(R.id.tvTempCropName).text.toString()
                .capitalize(Locale("en"))
        val transCropType =
            root.findViewById<TextView>(R.id.tvTempCropType).text.toString()
                .capitalize((Locale("en")))

        val growthBody = AddGrowthBody(
            transCropName ?: cropName.text.toString().capitalize(Locale.ROOT),
            TimeConversionUtils.convertDateToReqForm(etEst.text.toString()),
            TimeConversionUtils.convertDateToReqForm(root.findViewById<EditText>(R.id.etSowDate).text.toString()),
            cropQuantity.text.toString(),
            transCropType ?: cropType.text.toString().capitalize(Locale.ROOT)
        )

        Log.e(
            TAG,
            "In add growth" + growthBody.toString()
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
                } else if (viewModel.messageGrowth.value == "Crop growth added successfully.") {
                    Log.e(TAG, "In success ")
                    createToast(
                        requireContext().resources.getString(R.string.supplyAdded),
                        requireContext(),
                        container_add_stock
                    )
                    onDestroy()
                } else{
                    UIUtils.createSnackbar(
                        viewModel.messageGrowth.value,
                        requireContext(),
                        container_add_stock
                    )
                }
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

    private fun getValidTranslations(): Boolean {

        return ValidationObject.validateTranslations(
            root.findViewById<TextView>(R.id.tvTempCropName),
            requireContext()
        ) && ValidationObject.validateTranslations(
            root.findViewById<TextView>(R.id.tvTempCropType),
            requireContext()
        )

    }

    @RequiresApi(Build.VERSION_CODES.Q)
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

                && TimeConversionUtils.validateDates(
            etSow,
            etEst,
            R.string.etEstLessThanSow,
            R.string.etEstLessIncomplete,
            etEst,
            tilEst,
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
                cropName.setText(OfflineTranslate.transliterateToDefault(resultInEnglish))


            }
        } else if (requestCode == RC_TYPE) {
            if (data != null) {
                //Put result
                val res: java.util.ArrayList<String>? =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val resultInEnglish = res?.get(0)
                //  val conversionTable = onversionTable()
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
