package com.example.mandiexe.models.responses.auth


import androidx.annotation.Keep
import com.squareup.moshi.Json


 @Keep data class AccessTokenResponse(
    @field:Json(name = "msg")
    val msg: String, // Token issued successfully
    @field:Json(name = "user")
    val user: User
) {

     @Keep data class User(
        @field:Json(name = "accessToken")
        val accessToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGY2Zjc3MGNkNmNmMGE2MDE3ZjY5YiIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NDc5NTE3LCJleHAiOjE2MDg1NjU5MTd9.DGXsEiih7utgbzhHIRl0octCgZNL9Rw2nDHkEKTTc6c
        @field:Json(name = "id")
        val id: String, // 5fdf6f770cd6cf0a6017f69b
        @field:Json(name = "isFarmer")
        val isFarmer: Boolean, // true
        @field:Json(name = "name")
        val name: String, // kavya vatsal
        @field:Json(name = "phone")
        val phone: String, // +918585992062
        @field:Json(name = "refreshToken")
        val refreshToken: String // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGY2Zjc3MGNkNmNmMGE2MDE3ZjY5YiIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NDc5MjkxfQ.hKC5-DH4AebneH-jc2ue95Bd_95bLspQR1tQF0pWKOw
    )
}