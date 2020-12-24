package com.example.mandiexe.models.body.supply


import com.squareup.moshi.Json

data class SearchGlobalCropBody(
    @Json(name = "crop")
    val crop: String // Wheat
)