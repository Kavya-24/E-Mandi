package com.example.mandiexe.utils.auth

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add auth token to requests access tokens
 */

class AuthInterceptor(context: Context) : Interceptor {

    private val sessionManager =
        SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {


        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAcessToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        Log.e("AUTH", "In auth interceptor and access token is" + sessionManager.fetchAcessToken())


        return chain.proceed(requestBuilder.build())
    }
}

