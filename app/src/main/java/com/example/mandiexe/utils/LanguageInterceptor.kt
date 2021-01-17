package com.example.mandiexe.utils

import android.util.Log
import com.example.mandiexe.utils.auth.PreferenceUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class LanguageInterceptor : Interceptor {

    val language = PreferenceUtil.getLanguageFromPreference()
    val TAG = LanguageInterceptor::class.java.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val requestWithHeaders: Request = originalRequest.newBuilder()
            .header("Accept-Language", language ?: "en")
            .build()
        Log.e(TAG, "In intercept for langyage")
        return chain.proceed(requestWithHeaders)
    }


}