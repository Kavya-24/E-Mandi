package com.example.mandiexe.utils.auth

import android.content.Context
import android.util.Log
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.responses.auth.LoginResponse
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import java.io.IOException


class TokenAuthenticator(val context: Context) : Authenticator {

    val service = RetrofitClient.getAuthInstance()
    val sessionManager =
        SessionManager(context)

    val preferenceManager: PreferenceManager = PreferenceManager()

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {


        val newAccessToken = getNewAaccessToken()
        Log.e(
            "Token Authenitcation",
            newAccessToken.toString() + "Refresh token \n" + preferenceManager.authToken.toString()
                    + " Prev a token" + sessionManager.fetchAcessToken()
        )


        val resp = response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()

        Log.e(
            "Token Authenitcation",
            resp.body.toString() + newAccessToken.toString() + "Refresh token \n" + preferenceManager.authToken.toString()
        )
        return resp


    }

    private fun getNewAaccessToken(): String {

        var res = ""
        service.getAccessToken(refreshToken = preferenceManager.authToken.toString())
            .enqueue(object : retrofit2.Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: retrofit2.Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        res = response.body()?.user?.accessToken.toString()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                }
            })

        return res

    }

    //Store this token as the new values of the refresh and access tokens


    @Throws(IOException::class)
    fun authenticateRoute(route: Route?, response: Response?): Request? {
        // Null indicates no attempt to authenticate.
        return null
    }



}
