package com.example.mandiexe.interfaces

import com.example.mandiexe.models.body.authBody.AccessTokenBody
import com.example.mandiexe.models.body.authBody.LoginBody
import com.example.mandiexe.models.body.authBody.SignUpBody
import com.example.mandiexe.models.responses.auth.AccessTokenResponse
import com.example.mandiexe.models.responses.auth.FarmerProfileResponse
import com.example.mandiexe.models.responses.auth.LoginResponse
import com.example.mandiexe.models.responses.auth.SignUpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface authInterface {


    //Get access token
    @POST("/api/farmer/accesstoken")
    @Headers("Content-Type:application/json")
    fun getAccessToken(
        //USe Refresh token here            //Passed as String
        @Body mAccessTokenBody: AccessTokenBody
    ): Call<AccessTokenResponse>


    //Farmer number login
    //Takes the firebase token
    @Headers("Content-Type:application/json")
    @POST("/api/farmer/login")
    fun getLogin(
        @Body mLoginBody: LoginBody
    ): Call<LoginResponse>


    //Farmer SignUp
    @Headers("Content-Type:application/json")
    @POST("/api/farmer/signup")
    fun getSignUp(
        @Body mSignUpBody: SignUpBody
    ): Call<SignUpResponse>


    //Farmer Profile
    @Headers("Content-Type:application/json")
    @POST("/api/farmer/signup")
    fun getFarmerProfile(
    ): Call<FarmerProfileResponse>



}