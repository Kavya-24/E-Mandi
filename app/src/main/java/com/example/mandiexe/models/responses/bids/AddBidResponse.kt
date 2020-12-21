package com.example.mandiexe.models.responses.bids


import com.squareup.moshi.Json

data class AddBidResponse(
    @Json(name = "msg")
    val msg: String // Bid added successfully.
)