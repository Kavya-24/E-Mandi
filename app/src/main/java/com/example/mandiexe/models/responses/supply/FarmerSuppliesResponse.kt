package com.example.mandiexe.models.responses.supply


import com.squareup.moshi.Json

data class FarmerSuppliesResponse(
    @Json(name = "supplies")
    val supplies: List<Supply>
) {
    data class Supply(
        @Json(name = "askPrice")
        val askPrice: Int, // 21000
        @Json(name = "crop")
        val crop: String, // Rice
        @Json(name = "currentBid")
        val currentBid: Int, // 0
        @Json(name = "dateOfHarvest")
        val dateOfHarvest: String, // 2021-12-15T18:29:59.000Z
        @Json(name = "expiry")
        val expiry: String, // 2021-01-15T18:29:59.000Z
        @Json(name = "_id")
        val _id: String, // 5fdfb2c2786e790c4beca341
        @Json(name = "lastModified")
        val lastModified: String, // 2020-12-20T20:23:30.394Z
        @Json(name = "qty")
        val qty: Int, // 1900
        @Json(name = "variety")
        val variety: String // NA
    )
}