package com.example.mandiexe.models.responses.bids


import androidx.annotation.Keep
import com.squareup.moshi.Json


 @Keep data class UpdateBidResponse(
    @field:Json(name = "msg")
    val msg: String // Bid updated successfully.
)