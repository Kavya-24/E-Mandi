package com.example.mandiexe.models.body.supply


import com.squareup.moshi.Json

data class AddGrowthBody(
    @Json(name = "crop")
    val crop: String, // Rice
    @Json(name = "dateOfHarvest")
    val dateOfHarvest: String, // 01-15-2020 23:59:59
    @Json(name = "dateOfSowing")
    val dateOfSowing: String, // 01-15-2020 23:59:59
    @Json(name = "qty")
    val qty: String, // 100
    @Json(name = "variety")
    val variety: String // NA
)