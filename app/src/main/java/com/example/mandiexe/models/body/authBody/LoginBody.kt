package com.example.mandiexe.models.body.authBody


import androidx.annotation.Keep
import com.squareup.moshi.Json

//Replace Json with @field:Json
@Keep
data class LoginBody(

    @Json(name = "token")
    val token: String
)