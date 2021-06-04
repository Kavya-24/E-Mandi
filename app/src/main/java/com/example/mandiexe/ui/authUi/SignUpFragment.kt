package com.example.mandiexe.ui.authUi

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mandiexe.R
import com.example.mandiexe.ui.home.MapActivity
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.ValidationObject
import com.example.mandiexe.viewmodels.MapViewmodel
import com.example.mandiexe.viewmodels.OTViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var root: View

    //UI elements
    private lateinit var tilName: TextInputLayout
    private lateinit var tilAddress: TextInputLayout
    private lateinit var tilArea: TextInputLayout
    private lateinit var tilAUnit: TextInputLayout


    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etArea: EditText
    private lateinit var etAreaUnit: AutoCompleteTextView

    private val RC_MAP_SIGNUP = 111
    private val permissionRequestCode = 1234


    //A special code to tell it that this is from SignUp and it need to create dialog
    private val mapFromSignUp = "10"
    private val pref = PreferenceUtil

    private var TOKEN = ""
    private var PHONE = ""

    private lateinit var pb_sign_main: ProgressBar
    private lateinit var pb_sign_add: ProgressBar
    private lateinit var tvAddress: TextView

    private val viewModelSignup: MapViewmodel by viewModels()
    private val viewModelLogin: OTViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.sign_up_fragment, container, false)

        //Ui Init
        tilName = root.findViewById(R.id.tilUSerName)
        tilAddress = root.findViewById(R.id.tilUserAddress)
        tilAUnit = root.findViewById(R.id.tilAUnit)
        tilArea = root.findViewById(R.id.tilArea)

        etName = root.findViewById(R.id.etUserName)
        etAddress = root.findViewById(R.id.etUserAddress)
        etArea = root.findViewById(R.id.etArea)
        etAreaUnit = root.findViewById(R.id.actv_area_unit)

        pb_sign_add = root.findViewById(R.id.pb_sig_address)
        pb_sign_main = root.findViewById(R.id.pb_sig_up_main)
        tvAddress = root.findViewById(R.id.tv_address_fetched)


        if (arguments != null) {
            TOKEN = requireArguments().getString("TOKEN").toString()
            PHONE = requireArguments().getString("PHONE").toString()
        }

        //Populate units
        populateAreaUnit()
        getAutocorrectLocation()


        root.findViewById<MaterialButton>(R.id.mtb_sign_up).setOnClickListener {
            if (isValidate()) {
                //valid details
                //Save the prefered unit

                pref.setAreaUnitFromPreference(etArea.text.toString())
                goToMapActivity()

            }
        }


        return root
    }

    private fun getAutocorrectLocation() {

    }

    private fun populateAreaUnit() {

        etAreaUnit.setText(resources.getString(R.string.bigha))
        val array: Array<String> = resources.getStringArray(R.array.arr_area_units)
        val adapter: ArrayAdapter<String>? = context?.let {
            ArrayAdapter<String>(
                it, android.R.layout.simple_spinner_item,
                array
            )
        }
        etAreaUnit.setAdapter(adapter)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun goToMapActivity() {

        val i = Intent(requireContext(), MapActivity::class.java)


        val b = bundleOf(
            "TOKEN" to TOKEN,
            "PHONE" to PHONE,
            "RC" to mapFromSignUp,                          //For the thing that it goes from Login to Map Activity
            "NAME" to OfflineTranslate.transliterateToEnglish(etName.text.toString()).toString()
                .capitalize(Locale.ROOT),//(T)
            "AREA" to etArea.text.toString(),
            "AREA_UNIT" to etAreaUnit.text.toString(),
            "ADDRESS_USER" to OfflineTranslate.transliterateToEnglish(etAddress.text.toString())
                .toString().capitalize(Locale.ROOT)//(T)         //This is the village
        )

        Log.e("SignUp ", "Bundle passed is " + b.toString())

        i.putExtra("bundle", b)
        Log.e("SIGN", PreferenceUtil.getLanguageFromPreference().toString())
        startActivityForResult(i, RC_MAP_SIGNUP)
    }


    private fun isValidate(): Boolean {
        var isValid = true

        when {
            etName.text.isEmpty() -> {
                isValid = false
                tilName.error = resources.getString(R.string.emptyName)
            }
            !ValidationObject.validateName(etName.text.toString()) -> {
                isValid = false
                tilName.error = resources.getString(R.string.invalidName)
            }
            else -> {
                tilName.error = null
            }
        }


        if (etArea.text.isEmpty() || etArea.text.toString() == "0") {
            isValid = false
            tilArea.error = resources.getString(R.string.emptyAreaError)
        } else {
            tilArea.error = null
        }


        if (etAddress.text.isEmpty() || etAddress.text.toString() == "null") {
            isValid = false
            tilAddress.error = resources.getString(R.string.addressError)
        } else {
            tilAddress.error = null
        }


        return isValid
    }


}
