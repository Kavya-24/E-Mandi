package com.example.mandiexe.interfaces

import com.example.mandiexe.models.responses.auth.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiRefreshToken {

    companion object {
        private const val REFRESH_TOKEN = "/refreshToken"
    }

    @FormUrlEncoded
    @POST("api/farmer/accesstoken")
    fun refreshToken(@Field("refreshToken") refreshToken: String?): Call<LoginResponse>
}