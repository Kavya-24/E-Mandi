package com.example.mandiexe.utils

import com.example.mandiexe.utils.auth.PreferenceUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class LanguageInterceptor : Interceptor {

    val language = PreferenceUtil.getLanguageFromPreference()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val requestWithHeaders: Request = originalRequest.newBuilder()
            .header("Accept-Language", language ?: "en")
            .build()
        return chain.proceed(requestWithHeaders)
    }

}