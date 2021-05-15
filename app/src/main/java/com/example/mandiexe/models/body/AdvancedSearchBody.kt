package com.example.mandiexe.models.body


import com.squareup.moshi.Json

data class AdvancedSearchBody(

    @Json(name = "crop")
    val crop: String, // Wheat
    @Json(name = "days")
    val days: Int, // 30
    @Json(name = "distance")
    val distance: Int // 30
)