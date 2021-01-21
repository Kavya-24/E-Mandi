package com.example.mandiexe.utils.usables

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.mandiexe.R
import com.google.android.material.textfield.TextInputLayout

object ValidationObject {

    //##Access network state

    private val TAG = ValidationObject::class.java.simpleName

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkIsNetworkConnected(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

    }

    fun validateName(name: String): Boolean {
        //If the string does not have any numbers, slashes, or -
        var isValid = true
        for (c in name) {
            when (c) {
                in '0'..'9' -> isValid = false
                in '!'..'/' -> isValid = false
                in ':'..'@' -> isValid = false
                in '['..'`' -> isValid = false
                in '{'..'~' -> isValid = false
            }
        }

        return isValid
    }

    fun validateOTP(mOtp: String): Boolean {

        var isValid = true
        if (mOtp.length < 6) {
            isValid = false
        }

        return isValid

    }

    fun validateEmptyView(
        etInstance: AutoCompleteTextView,
        tvInstance: TextInputLayout,
        mError: Int,
        context: Context
    ): Boolean {
        var isValid = true

        if (etInstance.text!!.isEmpty()) {
            isValid = false
            tvInstance.error = ExternalUtils.getStringFromResoucrces(context, mError)
        } else {
            tvInstance.error = null
        }

        return isValid
    }

    fun validateEmptyEditText(
        etInstance: EditText,
        tvInstance: TextInputLayout,
        mError: Int,
        context: Context
    ): Boolean {
        var isValid = true

        if (etInstance.text!!.isEmpty()) {
            isValid = false
            tvInstance.error = ExternalUtils.getStringFromResoucrces(context, mError)
        } else {
            tvInstance.error = null
        }

        return isValid
    }

    fun validateTranslations(tvInstance: TextView, context: Context): Boolean {
        var isValid = true

        if (tvInstance.text == context.resources.getString(R.string.temp)) {
            isValid = false
        }
        return isValid
    }


}