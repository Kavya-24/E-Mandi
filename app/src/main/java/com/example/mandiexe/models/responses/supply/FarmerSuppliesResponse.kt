package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class FarmerSuppliesResponse(
    @field:Json(name = "supplies")
    val supplies: List<Supply>
) {
    @Keep
    data class Supply(
        @field:Json(name = "askPrice")
        val askPrice: Int, // 21000
        @field:Json(name = "crop")
        val crop: String, // Rice
        @field:Json(name = "currentBid")
        val currentBid: Int, // 0
        @field:Json(name = "dateOfHarvest")
        val dateOfHarvest: String, // 2021-12-15T18:29:59.000Z
        @field:Json(name = "expiry")
        val expiry: String, // 2021-01-15T18:29:59.000Z
        @field:Json(name = "_id")
        val _id: String, // 5fdfb2c2786e790c4beca341
        @field:Json(name = "lastModified")
        val lastModified: String, // 2020-12-20T20:23:30.394Z
        @field:Json(name = "qty")
        val qty: Int, // 1900
        @field:Json(name = "variety")
        val variety: String // NA
    )
}