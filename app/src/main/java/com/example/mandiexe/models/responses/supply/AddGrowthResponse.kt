package com.example.mandiexe.models.responses.supply


import com.squareup.moshi.Json

data class AddGrowthResponse(
    @Json(name = "msg")
    val msg: String // Crop growth added successfully.
)