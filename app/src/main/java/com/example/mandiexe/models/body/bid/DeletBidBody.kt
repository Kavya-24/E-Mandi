package com.example.mandiexe.models.body.bid


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class DeletBidBody(
    @field:Json(name = "bid_id")
    val bid_id: String // 5fe0a6e16118640eda871842
)