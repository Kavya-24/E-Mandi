package com.example.mandiexe.models.body.bid


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class AddBidBody(

    @field:Json(name = "bid")
    var bid: String, // 1000000
    @field:Json(name = "demand_id")
    val demand_id: String, // 5fe0a6e16118640eda871842
    @field:Json(name = "qty")
    val qty: String // 890
)