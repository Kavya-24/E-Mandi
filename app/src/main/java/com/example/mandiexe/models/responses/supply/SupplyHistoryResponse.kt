package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep data class SupplyHistoryResponse(
    @Json(name = "supplies")
    val supplies: List<Supply>
) {
    @Keep
    data class Supply(
        @Json(name = "active")
        val active: Boolean, // true
        @Json(name = "askPrice")
        val askPrice: Int, // 2000
        @Json(name = "crop")
        val crop: String, // Barley
        @Json(name = "currentBid")
        val currentBid: Int, // 0
        @Json(name = "dateOfHarvest")
        val dateOfHarvest: String, // 2021-01-15T18:29:59.000Z
        @Json(name = "expiry")
        val expiry: String, // 2021-01-15T18:29:59.000Z
        @Json(name = "_id")
        val _id: String, // 5fe13d8d4a73f913eb6daf16
        @Json(name = "lastModified")
        val lastModified: String, // 2020-12-22T00:27:57.564Z
        @Json(name = "qty")
        val qty: Int, // 100
        @Json(name = "variety")
        val variety: String // NA
    )
}