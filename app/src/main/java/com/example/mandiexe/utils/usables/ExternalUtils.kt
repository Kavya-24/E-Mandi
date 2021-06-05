package com.example.mandiexe.utils.usables

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.DisplayMetrics
import com.example.mandiexe.R
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.utils.ApplicationUtils
import com.google.android.gms.maps.model.LatLng
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeoutException


object ExternalUtils {


    private val TAG = ExternalUtils::class.java.simpleName
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

    //Get string from int
    fun getStringFromResoucrces(context: Context, mRes: Int): String {
        return context.resources.getString(mRes)
    }

    fun getAddress(context: Context, mLocale: String, latLang: LatLng): Address {

        //##Get location
        val locale = Locale(mLocale)
        var fetchedAddress = Address(locale)

        val geocoder = Geocoder(context, locale)
        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latLang.latitude, latLang.longitude, 1)
            fetchedAddress = addresses.get(0)

        } catch (e: Exception) {

            UIUtils.logExceptions(e, TAG)
        }


        return fetchedAddress
    }


}