package com.example.mandiexe.models.responses.supply


import com.squareup.moshi.Json

data class DeleteSupplyResponse(
    @Json(name = "msg")
    val msg: String // Supply deleted successfully.
)