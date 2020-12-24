package com.example.mandiexe.models.body.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class CropSearchAutoCompleteBody(
    @Json(name = "search")
    val search: String // e
)