package com.example.mandiexe.models.responses.bids


import com.squareup.moshi.Json

data class DeleteBidResponse(
    @Json(name = "msg")
    val msg: String // Bid deleted successfully.
)