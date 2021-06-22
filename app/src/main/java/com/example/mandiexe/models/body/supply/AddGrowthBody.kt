package com.example.mandiexe.models.body.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json


 @Keep data class AddGrowthBody(
    @field:Json(name = "crop")
    val crop: String, // Rice
    @field:Json(name = "dateOfHarvest")
    val dateOfHarvest: String, // 01-15-2020 23:59:59
    @field:Json(name = "dateOfSowing")
    val dateOfSowing: String, // 01-15-2020 23:59:59
    @field:Json(name = "qty")
    val qty: String, // 100
    @field:Json(name = "variety")
    val variety: String // NA
)