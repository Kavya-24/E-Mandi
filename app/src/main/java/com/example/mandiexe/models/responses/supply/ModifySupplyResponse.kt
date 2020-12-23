package com.example.mandiexe.models.responses.supply


import com.squareup.moshi.Json

data class ModifySupplyResponse(
    @Json(name = "msg")
    val msg: String, // Supply updated successfully.
    @Json(name = "error")
    val error: String // Supply updated successfully.
)