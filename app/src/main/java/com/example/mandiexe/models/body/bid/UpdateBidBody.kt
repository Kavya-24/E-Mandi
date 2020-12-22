package com.example.mandiexe.models.body.bid


import com.squareup.moshi.Json

data class UpdateBidBody(
    @Json(name = "bid_id")
    val bid_id: String, // 5fe0a6e16118640eda871842
    @Json(name = "new_price")
    val new_price: String // 1000000
)