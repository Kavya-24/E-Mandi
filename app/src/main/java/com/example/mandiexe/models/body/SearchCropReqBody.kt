package com.example.mandiexe.models.body


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class SearchCropReqBody(
    @field:Json(name = "crop")
    val crop: String // Wheat
)