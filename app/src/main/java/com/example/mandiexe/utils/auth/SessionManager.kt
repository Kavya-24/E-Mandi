package com.example.mandiexe.utils.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep
import com.example.mandiexe.R
import com.example.mandiexe.utils.ApplicationUtils

@Keep
class SessionManager(context: Context) {


    private val prefs: SharedPreferences =
        context.getSharedPreferences(
            context.getString(R.string.app_name_main),
            Context.MODE_PRIVATE
        )

    private val USER_ACCESS_TOKEN = ""
    private val ctx = ApplicationUtils.getContext()

    private var access: String?
        get() = prefs.getString(USER_ACCESS_TOKEN, "")
        set(value) {
            prefs.edit().putString(USER_ACCESS_TOKEN, value).apply()
        }


    /**
     * Function to save auth token
     */

    fun saveAuth_access_Token(token: String) {

        val p = SessionManager(ctx)
        p.access = token

    }


    /**
     * Function to fetch auth token
     */

    fun fetchAcessToken(): String? {
        val p = SessionManager(ctx)
        return p.access
    }


}