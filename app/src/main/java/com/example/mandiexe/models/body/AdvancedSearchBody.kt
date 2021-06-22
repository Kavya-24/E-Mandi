package com.example.mandiexe.models.body


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class AdvancedSearchBody(

    @field:Json(name = "crop")
    val crop: String, // Wheat
    @field:Json(name = "days")
    val days: Int, // 30
    @field:Json(name = "distance")
    val distance: Int // 30
)