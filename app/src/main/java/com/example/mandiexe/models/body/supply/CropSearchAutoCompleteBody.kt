package com.example.mandiexe.models.body.supply


import com.squareup.moshi.Json

data class CropSearchAutoCompleteBody(
    @Json(name = "search")
    val search: String // e
)