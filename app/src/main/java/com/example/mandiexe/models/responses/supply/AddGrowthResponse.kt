package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class AddGrowthResponse(
    @Json(name = "msg")
    val msg: String // Crop growth added successfully.
)