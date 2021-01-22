package com.example.mandiexe.models.responses


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class SearchCropReqResponse(
    @field:Json(name = "supplies")
    val supplies: List<Supply>
) {

    @Keep data class Supply(
        @field:Json(name = "active")
        val active: Boolean, // true
        @field:Json(name = "askPrice")
        val askPrice: Int, // 2000
        @field:Json(name = "bids")
        val bids: List<Any>,
        @field:Json(name = "change")
        val change: Int, // 0
        @field:Json(name = "crop")
        val crop: String, // Wheat
        @field:Json(name = "currentBid")
        val currentBid: Int, // 0
        @field:Json(name = "dateOfHarvest")
        val dateOfHarvest: String, // 2021-01-15T18:29:59.000Z
        @field:Json(name = "description")
        val description: String, // NA
        @field:Json(name = "distance")
        val distance: Int, // 0
        @field:Json(name = "expiry")
        val expiry: String, // 2021-01-15T18:29:59.000Z
        @field:Json(name = "_id")
        val _id: String, // 5fe13d964a73f913eb6daf17
        @field:Json(name = "lastBid")
        val lastBid: List<LastBid>,
        @field:Json(name = "lastModified")
        val lastModified: String, // 2020-12-22T01:50:10.406Z
        @field:Json(name = "location")
        val location: Location,
        @field:Json(name = "qty")
        val qty: Int, // 100
        @field:Json(name = "supplier")
        val supplier: List<Supplier>,
        @field:Json(name = "supplyCreated")
        val supplyCreated: String, // 2020-12-22T00:28:06.236Z
        @field:Json(name = "__v")
        val v: Int, // 6
        @field:Json(name = "variety")
        val variety: String // NA
    ) {

        @Keep data class LastBid(
            @field:Json(name = "amount")
            val amount: Int, // 0
            @field:Json(name = "_id")
            val id: String, // 5fe13fb24a73f913eb6daf1e
            @field:Json(name = "timestamp")
            val timestamp: String // 2020-12-22T00:28:06.236Z
        )


        @Keep data class Location(
            @field:Json(name = "coordinates")
            val coordinates: List<Double>,
            @field:Json(name = "type")
            val type: String // Point
        )


        @Keep data class Supplier(
            @field:Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-22T00:03:38.927Z
            @field:Json(name = "address")
            val address: String, // b381, asia
            @field:Json(name = "area")
            val area: Area,
            @field:Json(name = "country")
            val country: String, // ind
            @field:Json(name = "district")
            val district: String, // del
            @field:Json(name = "fuid")
            val fuid: String, // S2ustzAO4FeFSgCANfZtNRl7iZ42
            @field:Json(name = "_id")
            val id: String, // 5fe137dbe0cdc613d2894705
            @field:Json(name = "location")
            val location: Location,
            @field:Json(name = "name")
            val name: String, // kavya vatsal
            @field:Json(name = "phone")
            val phone: String, // +918585992062
            @field:Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZTEzN2RiZTBjZGM2MTNkMjg5NDcwNSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJsb2NhdGlvbiI6eyJ0eXBlIjoiUG9pbnQiLCJjb29yZGluYXRlcyI6WzkuNDM4OTMzLDIuOTQwNDM1M119LCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NTk1NDcyfQ.ZFsx5-9gzKp36bwyonG3eWJjS8afWiMpJGvv0lHX_eQ
            @field:Json(name = "state")
            val state: String, // del
            @field:Json(name = "__v")
            val v: Int, // 0
            @field:Json(name = "village")
            val village: String // greno
        ) {


            @Keep data class Area(
                @field:Json(name = "numerical")
                val numerical: Int, // 100
                @field:Json(name = "unit")
                val unit: String // acre
            )


            @Keep data class Location(
                @field:Json(name = "coordinates")
                val coordinates: List<Double>,
                @field:Json(name = "type")
                val type: String // Point
            )
        }
    }
}