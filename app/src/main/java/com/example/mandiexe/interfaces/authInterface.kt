package com.example.mandiexe.interfaces

import com.example.mandiexe.models.response.authResponses.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface authInterface {


    //Get access token
    @POST("api/user/accessToken")
    fun getAccessToken(
        //USe Refresh token here            //Passed as String
        @Body refreshToken: String
    ): Call<LoginResponse>





}