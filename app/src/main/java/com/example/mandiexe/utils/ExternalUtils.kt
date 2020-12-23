package com.example.mandiexe.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.example.mandiexe.R
import com.google.android.material.snackbar.Snackbar
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeoutException

object ExternalUtils {

    //##Access network state
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkIsNetworkConnected(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

    }

    fun returnStateMessageForThrowable(throwable: Throwable): String {
        var message: String = ""


        when (throwable) {
            is IOException -> {
                message = ApplicationUtils.getContext()
                    .getString(R.string.error_please_check_internet)
            }
            is TimeoutException -> {
                message = ApplicationUtils.getContext()
                    .getString(R.string.error_request_timed_out)
            }

            //##Str for it
            is HttpException -> {
                val httpException = throwable
                val response = httpException.response()?.errorBody()?.string()

                message = response!!
            }
            else -> {
                message = ApplicationUtils.getContext()
                    .getString(R.string.error_something_went_wrong)
            }
        }

        return message
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeToEpoch(timestamp: String): String {

        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone
        val sourceFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val destFormat =
            SimpleDateFormat("dd-MMM-yyyy")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(timestamp)!!
        destFormat.timeZone = timeDestinationZone
        Log.e("Timezone", timezone.toString() + timeDestinationZone.toString())
        return destFormat.format(convertedDate)


    }

    fun validateName(string: String): Boolean {
        //Check if the name has only alphabets and not special characters or numbers
//
//        for (c in string) {
//            if (c !in 'A'..'Z' && c !in 'a'..'z' && c != ' ') {
//                return false
//            }
//        }
        return true
    }

    fun setAppLocale(languageFromPreference: String?, context: Context) {
        if (languageFromPreference != null) {

            val resources: Resources = context.resources
            val dm: DisplayMetrics = resources.displayMetrics
            val config: Configuration = resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(Locale(languageFromPreference.toLowerCase()))
            } else {
                config.locale = Locale(languageFromPreference.toLowerCase())
            }
            resources.updateConfiguration(config, dm)
        }
    }

    fun createSnackbar(value: String?, context: Context, container: View) {

        Snackbar.make(container, value.toString(), Snackbar.LENGTH_SHORT).show()
    }

    fun hideKeyboard(activity: Activity, context: Context) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun convertObjectToNativeLanguage() {

    }


}