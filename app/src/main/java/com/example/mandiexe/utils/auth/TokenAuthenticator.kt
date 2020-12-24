package com.example.mandiexe.utils.auth

import android.content.Context
import android.util.Log
import com.example.mandiexe.interfaces.RetrofitClient
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

class TokenAuthenticator(val context: Context) : Authenticator {

    val service = RetrofitClient.getAuthInstance()
    val sessionManager =
        SessionManager(context)

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {


        val newAccessToken =
            service.getAccessToken(refreshToken = sessionManager.fetchRefreshToken().toString())


        val resp = response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()

        Log.e("Token Authenitcation", resp.body.toString() + newAccessToken.toString())
        return resp
    }

    //Store this token as the new values of the refresh and access tokens


    @Throws(IOException::class)
    fun authenticateRoute(route: Route?, response: Response?): Request? {
        // Null indicates no attempt to authenticate.
        return null
    }


}