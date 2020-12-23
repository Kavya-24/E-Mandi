package com.example.mandiexe.models.body.bid


import com.squareup.moshi.Json

data class AddBidBody(
    @Json(name = "bid")
    val bid: String, // 1000000
    @Json(name = "demand_id")
    val demand_id: String, // 5fe0a6e16118640eda871842
    @Json(name = "qty")
    val qty: String // 890
)