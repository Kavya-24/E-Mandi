package com.example.mandiexe.utils.auth


import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.authBody.AccessTokenBody
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

@Keep
class TokenAuthenticator(context: Context) : Authenticator {


    val service = RetrofitClient.getAuthInstance()
    val sessionManager =
        SessionManager(context)
    val prefManager = PreferenceManager()

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {

        Log.e("AUTH ", "In  token authenticator with response code"  + response.code + " and refresh tomen i s" + prefManager.authToken)

        val newAccessToken =
            service.getAccessToken(AccessTokenBody( refreshToken = prefManager.authToken.toString()))

        Log.e("AUTH", "New at reqesut$newAccessToken")

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