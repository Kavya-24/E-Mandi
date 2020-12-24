package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ModifySupplyResponse(
    @field:Json(name = "msg")
    val msg: String, // Supply updated successfully.
    @field:Json(name = "error")
    val error: String // Supply updated successfully.
)