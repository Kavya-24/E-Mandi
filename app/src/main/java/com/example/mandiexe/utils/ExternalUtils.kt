package com.example.mandiexe.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.icu.text.Transliterator
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
import com.example.mandiexe.lib.Language
import com.example.mandiexe.lib.TranslateAPI
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.github.wnameless.json.flattener.JsonFlattener
import com.github.wnameless.json.unflattener.JsonUnflattener
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
        return destFormat.format(convertedDate)

    }

    @SuppressLint("SimpleDateFormat")
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
        return destFormat.format(convertedDate)

    }

    @SuppressLint("SimpleDateFormat")
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
        return destFormat.format(convertedDate)


    }

    @SuppressLint("SimpleDateFormat")
    //Convert the calenadr dates
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

        return destFormat.format(convertedDate)


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

    fun setAppLocale(languageFromPreference: String?, context: Context) {

        if (languageFromPreference != null) {

            val resources: Resources = context.resources
            val dm: DisplayMetrics = resources.displayMetrics
            val config: Configuration = resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(Locale(languageFromPreference.toLowerCase(Locale.ROOT)))
            } else {
                config.setLocale(Locale(languageFromPreference.toLowerCase(Locale.ROOT)))
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

//    fun translateTextToEnglish(
//        mText: String,
//        srcLanguage: String,
//        dstLanguage: String
//    ): String? {
//
//        if (PreferenceUtil.getLanguageFromPreference() == "en") {
//            return mText
//        }
//
//        val translateAPI = TranslateAPI(
//            srcLanguage,  //Source Language
//            Language.ENGLISH,  //Target Language
//            mText
//        ) //Query Text
//
//        var q = ""
//
//        translateAPI.setTranslateListener(object : TranslateAPI.TranslateListener {
//            override fun onSuccess(translatedText: String?) {
//                Log.e("EXternal Utils", "In language conversion error " + translatedText)
//                q = translatedText!!
//            }
//
//            override fun onFailure(ErrorText: String?) {
//                Log.e("EXternal Utils", "In language conversion error " + ErrorText)
//
//            }
//        })
//
//        return q
//    }
//
//    fun translateTextToDefault(
//        mText: String,
//        srcLanguage: String,
//        dstLanguage: String
//    ): String {
//
//        if (PreferenceUtil.getLanguageFromPreference() == "en") {
//            return mText
//        }
//
//        val translateAPI = TranslateAPI(
//            Language.ENGLISH,  //Source Language
//            dstLanguage,  //Target Language
//            mText
//        ) //Query Text
//
//        var q = ""
//
//        translateAPI.setTranslateListener(object : TranslateAPI.TranslateListener {
//            override fun onSuccess(translatedText: String?) {
//                Log.e(
//                    "CONVERT DEF",
//                    "Translateing" + mText + "-" + translatedText
//                )
//                q = translatedText!!
//            }
//
//            override fun onFailure(ErrorText: String?) {
//                Log.e("CONVERT DEF", "In language conversion error " + ErrorText)
//
//            }
//        })
//
//        return q
//    }
//
//    //Flatten and unflatten objects
//    private fun flattenJsonString(mJson: String): String {
//
//        //Returns the flattened json structure
//        val jsonStr: String = JsonFlattener.flatten(mJson)
//        println(jsonStr)
//        Log.e("FLATTEN", jsonStr)
//        return jsonStr
//    }
//
//    private fun unflattenJSONString(jsonStr: String): String {
//
//        val nestedJson: String = JsonUnflattener.unflatten(jsonStr)
//        println(nestedJson)
//        Log.e("UNFLATTEN", nestedJson)
//        return nestedJson
//
//    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun transliterateToDefault(latinString: String?): String? {


        //Dont translate if the default lamguage is en
        if (PreferenceUtil.getLanguageFromPreference() == "en") {
            return latinString
        }

        Log.e(
            "Ext",
            "Tranliterator  " + Transliterator.getDisplayName(
                "en-" + PreferenceUtil.getLanguageFromPreference().toString(),
                Locale(PreferenceUtil.getLanguageFromPreference().toString())
            )
        )
        val toDevnagiri = Transliterator.getInstance(
            "en-" + PreferenceUtil.getLanguageFromPreference().toString()
        )
        return toDevnagiri.transliterate(latinString)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun transliterateToEnglish(latinString: String?): String? {

        //Dont translate if the default lamguage is en
        if (PreferenceUtil.getLanguageFromPreference() == "en") {
            return latinString
        }
        Log.e("Ext", "Tranliterator  " + Transliterator.getDisplayName("en-hi", Locale("hi")))

        val toEnglish = Transliterator.getInstance(
            PreferenceUtil.getLanguageFromPreference().toString() + "-en"
        )
        return toEnglish.transliterate(latinString)

    }

    //Date Util
    fun getDateInstanceFromString(mDate: String): Date {
        val myFormat = SimpleDateFormat("dd/MM/yyyy") //In which you need put here
        return myFormat.parse(mDate)!!
    }

    //Get the list of supported language
    fun getSupportedLanguageList(): MutableList<LanguageBody> {
        val mLanguages: MutableList<LanguageBody> = mutableListOf()

        //0
        mLanguages.add(LanguageBody("English", "en"))

        //1
        mLanguages.add(LanguageBody("हिंदी", "hi"))

        //2
        mLanguages.add(LanguageBody("বাংলা", "bn"))

        //3
        mLanguages.add(LanguageBody("मराठी", "mr"))

        //4 Tamil
        mLanguages.add(LanguageBody("தமிழ்", "ta"))

        //5 Telugu
        mLanguages.add(LanguageBody("తెలుగు", "te"))

        return mLanguages
    }

    fun getLocaleFromAdapterIndex(mPosition: Int): String {
        var str = "en"

        when (mPosition) {

            0 -> {
                str = "en"
            }
            1 -> {
                str = "hi"
            }
            2 -> {
                str = "bn"
            }

            3 -> {
                str = "mr"

            }
            4 -> {
                str = "ta"
            }
            5 -> {
                str = "te"
            }


            else -> {
                str = "en"
            }

        }
        return str
    }
}