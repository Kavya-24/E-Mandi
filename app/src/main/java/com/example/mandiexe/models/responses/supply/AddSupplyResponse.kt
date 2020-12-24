package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class AddSupplyResponse(
    @field:Json(name = "msg")
    val msg: String // Supply added successfully.
)