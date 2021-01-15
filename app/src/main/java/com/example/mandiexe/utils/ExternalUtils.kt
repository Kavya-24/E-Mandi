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
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.mandiexe.R
import com.google.android.material.snackbar.Snackbar
import retrofit2.HttpException
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeoutException

//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;


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

    //For the graph util
    @SuppressLint("SimpleDateFormat")
    fun convertDateTimestampUtil(timestamp: String): Date? {
        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone
        val sourceFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(timestamp)!!

        val destFormat =
            SimpleDateFormat("dd-MM-yy HH:mm")
        destFormat.timeZone = timeDestinationZone

        val resultDate = destFormat.format(convertedDate)

        Log.e("ExternalUtils", resultDate)

        return destFormat.parse(resultDate)

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


    fun reverseToReq(timestamp: String): String {

        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone
        val sourceFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val destFormat =
            SimpleDateFormat("dd/MM/yyyy")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(timestamp)!!

        destFormat.timeZone = timeDestinationZone
        Log.e("Timezone", timezone.toString() + timeDestinationZone.toString())
        return destFormat.format(convertedDate)

    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    fun convertTimestampToDate(timestamp: String): Date? {
//
//        val formatter = DateTimeFormatter.ofPattern(
//            "dd-MMM-yyyy",
//            Locale(PreferenceUtil.getLanguageFromPreference()) ?: Locale.ENGLISH
//        )
//        val ts = convertTimeToEpoch(timestamp)
//        val localDate = LocalDate.parse(ts, formatter)
//
//        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
//
//    }

    fun convertLastModified(timestamp: String): String {

        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone
        val sourceFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val destFormat =
            SimpleDateFormat("dd-MMM-yyyy HH:mm")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(timestamp)!!
        destFormat.timeZone = timeDestinationZone
        Log.e("Timezone", timezone.toString() + timeDestinationZone.toString())
        return destFormat.format(convertedDate)


    }


    fun validateName(string: String): Boolean {
////        Check if the name has only alphabets and not special characters or numbers
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

    fun createToast(value: String, context: Context, container: View) {
        Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
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

    fun convertDateToReqForm(value: String): String {

        val calendar = Calendar.getInstance()
        val timezone = TimeZone.getTimeZone("UTC")
        val timeDestinationZone = calendar.timeZone

        val sourceFormat =
            SimpleDateFormat("dd/MM/yyyy")
        val destFormat =
            SimpleDateFormat("MM-dd-yyyy 00:00:00")

        sourceFormat.timeZone = timezone
        val convertedDate = sourceFormat.parse(value)!!
        destFormat.timeZone = timeDestinationZone
        Log.e("Timezone", timezone.toString() + timeDestinationZone.toString())

        Log.e(
            "ExternalUtil",
            "Source date " + value + " Dest Date" + destFormat.format(convertedDate)
        )
        return destFormat.format(convertedDate)


    }

    fun translateText(text: String?, srcLanguage: Locale, dstLanguage: Locale): String? {

        var translated: String? = null
        try {

            val query: String = URLEncoder.encode(text, "UTF-8")
            val langpair: String = URLEncoder.encode(
                srcLanguage.language.toString() + "|" + dstLanguage.language, "UTF-8"
            )
            val url =
                "http://mymemory.translated.net/api/get?q=$query&langpair=$langpair"

//            val hc: HttpClient = DefaultHttpClient()
//            val hg = HttpGet(url)
//            val hr: HttpResponse = hc.execute(hg)
//            if (hr.getStatusLine().getStatusCode() === HttpStatus.SC_OK) {
//                val response = JSONObject(EntityUtils.toString(hr.getEntity()))
//                translated = response.getJSONObject("responseData").getString("translatedText")
//            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return translated
    }


}