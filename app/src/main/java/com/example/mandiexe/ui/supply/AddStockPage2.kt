package com.example.mandiexe.ui.supply

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.mandiexe.R
import com.example.mandiexe.models.body.supply.AddGrowthBody
import com.example.mandiexe.models.body.supply.AddSupplyBody
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.ExternalUtils.setAppLocale
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.UIUtils.createSnackbar
import com.example.mandiexe.utils.usables.UIUtils.hideProgress
import com.example.mandiexe.utils.usables.UIUtils.showProgress
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.AddStockViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_add_stock_page2.*
import kotlinx.android.synthetic.main.layout_add_stock.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.util.*

class AddStockPage2 : AppCompatActivity() {

    private val viewModel: AddStockViewModel by viewModels()

    private lateinit var myCalendar: Calendar
    private val TAG = AddStockPage2::class.java.simpleName
    private var mHandler = Handler()

    private lateinit var etExp: EditText
    private lateinit var cropDes: EditText
    private lateinit var offerPrice: EditText

    private lateinit var tilPrice: TextInputLayout
    private lateinit var tilExp: TextInputLayout

    private val pref = PreferenceUtil
    private lateinit var args: Bundle
    private lateinit var pb: ProgressBar


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale(pref.getLanguageFromPreference(), this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stock_page2)

        args = intent?.getBundleExtra("bundle")!!
        pb = findViewById(R.id.pb_add_stock_page_2)
        val tb = findViewById<Toolbar>(R.id.toolbarExternal)
        tb.title = resources.getString(R.string.addCropforBidding)

        this.apply {
            tvTitleToolbar.text = resources.getString(R.string.addCropforBidding)
        }
        tb.setNavigationOnClickListener {
            onBackPressed()
        }

        //Set name and type
        findViewById<TextView>(R.id.tvAddStockCropName).text = args.getString("NAME")
        findViewById<TextView>(R.id.tvAddStockCropType).text = args.getString("TYPE")

        //UI Init
        etExp = findViewById(R.id.etExpDate)
        cropDes = findViewById(R.id.etDescription_add_stock)
        offerPrice = findViewById(R.id.actv_price)

        //TIL
        tilPrice = findViewById(R.id.tilOfferPrice)
        tilExp = findViewById(R.id.tilExpDate)

        // disable dates before today
        myCalendar = Calendar.getInstance()
        myCalendar.add(Calendar.MONTH, 1)



        etExp.setOnClickListener {
            TimeConversionUtils.clickOnDateObject(myCalendar, etExp, this)
        }

        findViewById<ImageView>(R.id.ivExp).setOnClickListener {
            TimeConversionUtils.clickOnDateObject(myCalendar, etExp, this)
        }

        findViewById<MaterialButton>(R.id.mtb_add_stock).setOnClickListener {
            if (isValidate()) {
                createConfirmDialog()
            }
        }


        this.apply {

            ivInformation.setOnClickListener {
                getInformationNormalFilters()
            }
        }

    }

    private fun getInformationNormalFilters() {

        val kgLocale = resources.getString(R.string.kg)
        val d = AlertDialog.Builder(this)
        d.setTitle(resources.getString(R.string.addCropforBidding))
        d.setMessage(resources.getString(R.string.addBidProposal))
        d.setPositiveButton(resources.getString(R.string.ok)) { _, _ -> }
        d.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createConfirmDialog() {

        val dialog = android.app.AlertDialog.Builder(this)

        val nCrop = args.getString("NAME").toString()
        val nQuantity = args.getString("QUANTITY").toString()
        val nQuantityTranslated = resources.getString(R.string.kg)
        val nPrice = offerPrice.text.toString()
        val nExpBid = etExp.text.toString()

        val rsLocale = resources.getString(R.string.rs)

        //Inflate View
        val v = layoutInflater.inflate(R.layout.layout_add_stock, null)
        dialog.setView(v)

        //Get references of the views in the layout_add_stock
        v.apply {
            tvcnfAddCrop.text = resources.getString(
                R.string.adding_crop,
                nCrop,
                nQuantity,
                nQuantityTranslated,
                rsLocale,
                nPrice
            )

            tvcnfAddBiddate.text = resources.getString(R.string.adding_bid_date, nExpBid)
        }

        dialog.setPositiveButton(resources.getString(R.string.confirm)) { mDialog_, mInt ->
            mDialog_.dismiss()
            createStock()
        }
        dialog.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

        }

        dialog.create()
        dialog.show()

    }

    private fun getValidTranslations(): Boolean {

        return ValidationObject.validateTranslations(
            findViewById<TextView>(R.id.tvTempCropDesc),
            this
        ) && ValidationObject.validateTranslations(
            findViewById<TextView>(R.id.tvTempCropNamePage2),
            this
        ) && ValidationObject.validateTranslations(
            findViewById<TextView>(R.id.tvTempCropTypePage2),
            this
        )

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getTranslations() {
        //Run an async task to get the values for the three categories
        OfflineTranslate.translateToEnglish(
            this,
            cropDes.text.toString(),
            findViewById(R.id.tvTempCropDesc)
        )

        //Comes from argumenets
        OfflineTranslate.translateToEnglish(
            this,
            args.getString("NAME").toString(),
            findViewById(R.id.tvTempCropNamePage2)
        )
        OfflineTranslate.translateToEnglish(
            this,
            args.getString("TYPE").toString(),
            findViewById(R.id.tvTempCropTypePage2)
        )

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createStock() {

        showProgress(pb, this)

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

        hideProgress(pb, this)
    }

    private fun makeCalls() {

        val transCropName =
            findViewById<TextView>(R.id.tvTempCropNamePage2).text.toString()
                .capitalize(Locale("en"))
        val transCropType =
            findViewById<TextView>(R.id.tvTempCropTypePage2).text.toString()
                .capitalize((Locale("en")))

        var transDesc = cropDes.text.toString()

        if (cropDes.text.toString() != resources.getString(R.string.noDesc)) {
            //If it has something, use uts translated values
            transDesc = findViewById<TextView>(R.id.tvTempCropDesc).text.toString()
                .capitalize((Locale("en")))

        }

        Log.e(TAG, "Translated values are $transCropName$transCropType$transDesc")
        val body = AddSupplyBody(
            offerPrice.text.toString(),
            transCropName,
            TimeConversionUtils.convertDateToReqForm(args.getString("EST").toString()),
            transDesc,
            TimeConversionUtils.convertDateToReqForm(etExp.text.toString()),
            args.getString("QUANTITY").toString(),
            transCropType
        )

        //Create growth

        val growthBody = AddGrowthBody(
            transCropName,
            TimeConversionUtils.convertDateToReqForm(args.getString("EST").toString()),
            TimeConversionUtils.convertDateToReqForm(args.getString("SOW").toString()),
            args.getString("QUANTITY").toString(),
            transCropType
        )

        Log.e(
            TAG,
            "AddSupply Body \n$body\n Add growth body$growthBody"
        )

        val mSnackbar = findViewById<CoordinatorLayout>(R.id.container_add_stock_page_2)

        viewModel.growthFunction(growthBody, mSnackbar, pb)
            .observe(this, { mResponse ->
                val success = viewModel.successfulGrowth.value
                if (success != null) {
                    hideProgress(pb, this)
                    Log.e(
                        TAG,
                        "In growth function and success is " + success + viewModel.messageGrowth
                    )

                    if (success == true) {
                        Log.e(TAG, "In successfully added growth")
                    } else {
                        Log.e(TAG, "In failed added growth")
                    }

                } else {
                    showProgress(pb, this)
                }
            })


        viewModel.addFunction(body, mSnackbar, pb).observe(this, { mResponse ->

            if (viewModel.successful.value != null) {
                hideProgress(pb, this)

                manageStockCreateResponses(mResponse)
            } else {
                showProgress(pb, this)
            }


        })

        //Stop Progress bar
        findViewById<ProgressBar>(R.id.pb_add_stock_page_2).visibility = View.GONE

    }

    private fun manageStockCreateResponses(value: AddSupplyResponse?) {

        if (value != null) {
            if (value.msg == "Supply added successfully.") {

                Toast.makeText(
                    this,
                    resources.getString(R.string.supplyAdded),
                    Toast.LENGTH_SHORT
                )
                    .show()

                //Destroy AddStock
                AddStock.mActivityInstance.finish()
                onBackPressed()
            } else {
                val errorMessgae = value.msg
                //Create snackbar for the error message
                createSnackbar(errorMessgae.toString(), this, container_add_stock_page_2)

            }
        } else {

            createSnackbar(
                resources.getString(R.string.unableToAddStock),
                this,
                container_add_stock_page_2
            )

        }

    }

    private fun isValidate(): Boolean {

        var isValid = true

        if (offerPrice.text.isNullOrEmpty()) {
            tilPrice.error = resources.getString(R.string.offerPriceError)
            isValid = false
        } else {
            tilPrice.error = null
        }

        val sow_date = args.getString("SOW").toString()
        if (etExp.text.isNullOrEmpty()) {
            tilExp.error = resources.getString(R.string.expError)
            isValid = false
        } else if (TimeConversionUtils.getDateInstanceFromString(etExp.text.toString()) < TimeConversionUtils.getDateInstanceFromString(
                sow_date
            )
        ) {
            tilExp.error = resources.getString(R.string.expLess)
            isValid = false
        } else {
            tilExp.error = null
        }





        return isValid

    }

    override fun onBackPressed() {

        viewModel.successfulGrowth.removeObservers(this)
        viewModel.successfulGrowth.value = null
        viewModel.successful.removeObservers(this)
        viewModel.successful.value = null

        //Finish this activty
        super.onBackPressed()
        finish()

    }
}
