package com.example.mandiexe.models.responses.bids


import com.squareup.moshi.Json

data class UpdateBidResponse(
    @Json(name = "msg")
    val msg: String // Bid updated successfully.
)