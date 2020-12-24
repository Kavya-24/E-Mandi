package com.example.mandiexe.models.responses.auth


import com.squareup.moshi.Json

data class SignUpResponse(
    @Json(name = "msg")
    val msg: String // Registeration successful.

)