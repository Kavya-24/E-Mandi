package com.example.mandiexe.models.responses.bids


import androidx.annotation.Keep
import com.squareup.moshi.Json

data class Temp(
    @Json(name = "msg")
    val msg: String, // Supply retrieved successfully.
    @Json(name = "supply")
    val demand: Demand
) {
    data class Demand(

        @Json(name = "active")
        val active: Boolean, // false

        @Json(name = "offerPrice")
        val offerPrice: Int, // 12000

        @Json(name = "change")
        val change: Int, // 0

        @Json(name = "crop")
        val crop: String, // Wheat

        @Json(name = "currentBid")
        val currentBid: Int, // 0

        @Json(name = "dateOfHarvest")
        val dateOfHarvest: String, // 2020-01-15T18:29:59.000Z

        @Json(name = "description")
        val description: String, // NA

        @Json(name = "expiry")
        val expiry: String, // 2020-01-15T18:29:59.000Z

        @Json(name = "_id")
        val _id: String, // 5fdf9eda9318050bc1d3393f

        @Json(name = "lastBid")
        val lastBid: List<LastBid>, // 0

        @Json(name = "lastModified")
        val lastModified: String, // 2020-12-20T18:58:34.069Z

        @Json(name = "qty")
        val qty: Int, // 100

        @Json(name = "supplier")
        val supplier: Supplier,
        @Json(name = "supplyCreated")
        val supplyCreated: String, // 2020-12-20T18:58:34.069Z
        @Json(name = "__v")
        val v: Int, // 0
        @Json(name = "variety")
        val variety: String // common
    ) {
        data class Supplier(
            @Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-20T18:49:30.279Z
            @Json(name = "address")
            val address: String, // b381, asia
            @Json(name = "bids")
            val bids: List<Any>,
            @Json(name = "country")
            val country: String, // ind
            @Json(name = "district")
            val district: String, // del
            @Json(name = "fuid")
            val fuid: String, // S2ustzAO4FeFSgCANfZtNRl7iZ42
            @Json(name = "_id")
            val id: String, // 5fdf9cbbc8189a0ba78c0fa4
            @Json(name = "location")
            val location: Location,
            @Json(name = "name")
            val name: String, // kavya vatsal
            @Json(name = "phone")
            val phone: String, // +918585992062
            @Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGY5Y2JiYzgxODlhMGJhNzhjMGZhNCIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NDkwMTc2fQ.boREbY4D3XwIZ9fUi7tFdagJCHt3IBdlMHvD96poGqo
            @Json(name = "state")
            val state: String, // del
            @Json(name = "supplies")
            val supplies: List<Any>,
            @Json(name = "__v")
            val v: Int, // 0
            @Json(name = "village")
            val village: String // greno
        ) {
            data class Location(
                @Json(name = "coordinates")
                val coordinates: List<Double>,
                @Json(name = "type")
                val type: String // Point
            )


        }

        @Keep
        data class LastBid(
            @field:Json(name = "amount")
            val amount: Int, // 0
            @field:Json(name = "_id")
            val _id: String, // 5fe9f3ead87d2a239e7a9a1d
            @field:Json(name = "timestamp")
            val timestamp: String // 2020-12-26T16:05:16.797Z
        )

    }
}