package com.example.mandiexe.models.body.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class SearchGlobalCropBody(
    @Json(name = "crop")
    val crop: String // Wheat
)