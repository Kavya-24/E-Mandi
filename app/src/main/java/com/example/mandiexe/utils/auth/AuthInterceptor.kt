package com.example.mandiexe.utils.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.authBody.AccessTokenBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Interceptor to add auth token to requests access tokens
 */

class AuthInterceptor(context: Context) : Interceptor {

    private val sessionManager =
        SessionManager(context)

    val service = RetrofitClient.getAuthInstance()
    val prefManager =
        PreferenceManager()
    val mContext = context

    override fun intercept(chain: Interceptor.Chain): Response {

        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAcessToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val mCall = chain.proceed(requestBuilder.build())
        Log.e("Auth", "In Auth Intercptor and quest code is ${mCall.code} ")

        if (mCall.code == 401 || mCall.code == 403) {

            Log.e("AUTH", "In if and refresh token is ${prefManager.authToken}")

            mCall.close()

            val newAccessTokenResponse =
                service.getAccessToken(AccessTokenBody(prefManager.authToken!!)).execute()
            Log.e(
                "AUTH",
                "The new call is " + newAccessTokenResponse.toString() + " And body is " + newAccessTokenResponse.body()
                    .toString() + " Amd error is " + newAccessTokenResponse.errorBody()
            )

            if (newAccessTokenResponse.code() == 400) {
                //Bad request
                //That is, either the token
                Log.e("AUTH", "In bad request")
                //Go to main login activty
                Toast.makeText(mContext,mContext.resources.getString(R.string.failAccessToken), Toast.LENGTH_SHORT).show()
            }

            //Save the values
            newAccessTokenResponse.body()?.user?.accessToken?.let {
                sessionManager.saveAuth_access_Token(
                    it
                )
            }
            newAccessTokenResponse.body()?.user?.refreshToken?.let { prefManager.putAuthToken(it) }

            //Create a new request
            val newAuthenticationRequest: Request = requestBuilder
                .header(
                    "Authorization",
                    "Bearer ${newAccessTokenResponse.body()?.user?.accessToken}"
                )
                .build()

            Log.e("AUTH", "The new auth req is " + newAuthenticationRequest.toString())
            return chain.proceed(newAuthenticationRequest)

        } else if (mCall.code == 400) {
            //This is when the request is Bad and there is not
            Log.e("AUTH", "In 400 code")
        }

        return mCall
    }


}

