package com.example.mandiexe.models.responses.auth


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class LoginResponse(
    @Json(name = "msg")
    val msg: String,
    @Json(name = "user")
    val user: User
) {
    @Keep
    data class User(
        @Json(name = "accessToken")
        val accessToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGY2Zjc3MGNkNmNmMGE2MDE3ZjY5YiIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NDc4Njg1LCJleHAiOjE2MDg1NjUwODV9.kayKdnpJRBZq4q8UzCHGoAT88MFe5CF6ATVLv03MFq4
        @Json(name = "id")
        val id: String, // 5fdf6f770cd6cf0a6017f69b
        @Json(name = "isFarmer")
        val isFarmer: Boolean, // true
        @Json(name = "name")
        val name: String, // kavya vatsal
        @Json(name = "phone")
        val phone: String, // +918585992062
        @Json(name = "refreshToken")
        val refreshToken: String // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGY2Zjc3MGNkNmNmMGE2MDE3ZjY5YiIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NDc4Njg1fQ._fBk8ak4ITzKvWhxVwQQBK7K47X2AyhVlb9yI31bBUQ
    )
}