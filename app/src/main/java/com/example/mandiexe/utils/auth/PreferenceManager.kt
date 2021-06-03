package com.example.mandiexe.utils.auth

/**
 * Shared Pref for key and value
 */
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep
import com.example.mandiexe.utils.ApplicationUtils

@Keep
class PreferenceManager {


    companion object {
        const val APPLICATION_PREFERENCE = "app-preferences"
        const val AUTH_TOKEN = "auth-token"
    }

    private val context: Context = ApplicationUtils.getContext()

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        APPLICATION_PREFERENCE, Context.MODE_PRIVATE
    )


    @SuppressLint("ApplySharedPref")
    //Cannot use .apply(), it will take time to save the token. We need token ASAP
    fun putAuthToken(authToken: String) {
        sharedPreferences.edit().putString(AUTH_TOKEN, authToken).commit()
    }

    val authToken: String?
        get() = sharedPreferences.getString(AUTH_TOKEN, "")

    /**
     * Clears all the data that has been saved in the preferences file.
     */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}