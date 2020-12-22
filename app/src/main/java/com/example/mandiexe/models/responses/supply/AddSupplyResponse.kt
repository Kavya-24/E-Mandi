package com.example.mandiexe.models.responses.supply


import com.squareup.moshi.Json

data class AddSupplyResponse(
    @Json(name = "msg")
    val msg: String // Supply added successfully.
)