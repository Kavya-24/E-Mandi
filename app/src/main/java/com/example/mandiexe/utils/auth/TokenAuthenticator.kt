package com.example.mandiexe.utils.auth


import android.content.Context
import android.util.Log
import com.example.mandiexe.interfaces.RetrofitClient
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

class TokenAuthenticator(context: Context) : Authenticator {


    val service = RetrofitClient.getAuthInstance()
    val sessionManager =
        SessionManager(context)
    val prefManager = PreferenceManager()

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {

        Log.e("AUTH ", "In Authenticate and code is " + response.code)

        val newAccessToken =
            service.getAccessToken(refreshToken = prefManager.authToken.toString())

        Log.e("AUTH", "New at reqesut" + newAccessToken.toString())

        val resp = response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()

        return resp
    }

    //Store this token as the new values of the refresh and access tokens


    @Throws(IOException::class)
    fun authenticatRoute(route: Route?, response: Response?): Request? {
        // Null indicates no attempt to authenticate.
        return null
    }


}