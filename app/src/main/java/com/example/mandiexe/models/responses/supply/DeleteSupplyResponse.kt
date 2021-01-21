package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class DeleteSupplyResponse(
    @field:Json(name = "msg")
    val msg: String // Supply deleted successfully.
)