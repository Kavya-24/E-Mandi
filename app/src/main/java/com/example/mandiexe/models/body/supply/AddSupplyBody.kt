package com.example.mandiexe.models.body.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class AddSupplyBody(
    @field:Json(name = "askPrice")
    val askPrice: String, // 2000
    @field:Json(name = "crop")
    val crop: String, // Wheat
    @field:Json(name = "dateOfHarvest")
    val dateOfHarvest: String, // 01-15-2020 23:59:59
    @field:Json(name = "description")
    val description: String, // NA
    @field:Json(name = "expiry")
    val expiry: String, // 01-15-2020 23:59:59
    @field:Json(name = "qty")
    val qty: String, // 100
    @field:Json(name = "variety")
    val variety: String // NA
)