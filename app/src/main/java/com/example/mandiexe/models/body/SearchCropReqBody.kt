package com.example.mandiexe.models.body


import com.squareup.moshi.Json

data class SearchCropReqBody(
    @Json(name = "crop")
    val crop: String // Wheat
)