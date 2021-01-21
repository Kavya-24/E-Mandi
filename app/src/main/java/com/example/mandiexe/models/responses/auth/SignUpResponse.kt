package com.example.mandiexe.models.responses.auth


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class SignUpResponse(
    @field:Json(name = "msg")
    val msg: String // Registeration successful.

)