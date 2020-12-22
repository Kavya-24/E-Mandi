package com.example.mandiexe.models.body.supply


import com.squareup.moshi.Json

data class AddSupplyBody(
    @Json(name = "askPrice")
    val askPrice: String, // 2000
    @Json(name = "crop")
    val crop: String, // Wheat
    @Json(name = "dateOfHarvest")
    val dateOfHarvest: String, // 01-15-2020 23:59:59
    @Json(name = "description")
    val description: String, // NA
    @Json(name = "expiry")
    val expiry: String, // 01-15-2020 23:59:59
    @Json(name = "qty")
    val qty: String, // 100
    @Json(name = "variety")
    val variety: String // NA
)