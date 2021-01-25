package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ViewSupplyResponse(

    @field:Json(name = "msg")
    val msg: String, // Supply retrieved successfully.

    @field:Json(name = "supply")
    val supply: Supply
) {
    @Keep data class Supply(

        @field:Json(name = "active")
        val active: Boolean, // true

        @field:Json(name = "askPrice")
        val askPrice: Int, // 30000

        @field:Json(name = "bids")
        val bids: List<Bid>,

        @field:Json(name = "change")
        val change: Int, // 0

        @field:Json(name = "crop")
        val crop: String, // Sugarcane

        @field:Json(name = "currentBid")
        val currentBid: Int, // 100000

        @field:Json(name = "dateOfHarvest")
        val dateOfHarvest: String, // 2021-02-27T00:00:00.000Z

        @field:Json(name = "description")
        val description: String, // Sugarcane from Uttar Pradesh

        @field:Json(name = "expiry")
        val expiry: String, // 2021-03-17T00:00:00.000Z

        @field:Json(name = "_id")
        val _id: String, // 5fe75f3c709347e81377726b

        @field:Json(name = "lastBid")
        val lastBid: List<LastBid>,

        @field:Json(name = "lastModified")
        val lastModified: String, // 2020-12-28T15:19:14.400Z

        @field:Json(name = "location")
        val location: Location,

        @field:Json(name = "qty")
        val qty: Int, // 0

        @field:Json(name = "supplier")
        val supplier: Supplier,

        @field:Json(name = "supplyCreated")
        val supplyCreated: String, // 2020-12-26T16:05:16.797Z

        @field:Json(name = "__v")
        val v: Int, // 2

        @field:Json(name = "variety")
        val variety: String // sweet
    ) {
        @Keep data class Bid(

            @field:Json(name = "active")
            val active: Boolean, // true

            @field:Json(name = "bidDate")
            val bidDate: String, // 2020-12-28T15:04:10.004Z

            @field:Json(name = "bidder")
            val bidder: Bidder,

            @field:Json(name = "bids")
            val bids: List<BidDetails>,

            @field:Json(name = "currentBid")
            val currentBid: Int, // 100000

            @field:Json(name = "_id")
            val _id: String, // 5fe9f3ead87d2a239e7a9a1b

            @field:Json(name = "lastModified")
            val lastModified: String, // 2020-12-28T15:19:14.400Z

            @field:Json(name = "supply")
            val supply: String, // 5fe75f3c709347e81377726b

            @field:Json(name = "__v")
            val v: Int // 0
        ) {
            @Keep data class Bidder(

                @field:Json(name = "address")
                val address: String, // b381, asia
                @field:Json(name = "district")
                val district: String, // del
                @field:Json(name = "_id")
                val _id: String, // 5fe13807e0cdc613d2894706
                @field:Json(name = "location")
                val location: Location,
                @field:Json(name = "name")
                val name: String, // kavya vatsal
                @field:Json(name = "phone")
                val phone: String, // +918585992062
                @field:Json(name = "state")
                val state: String, // del
                @field:Json(name = "village")
                val village: String // greno
            ) {
                @Keep data class Location(
                    @field:Json(name = "coordinates")
                    val coordinates: List<Double>,
                    @field:Json(name = "type")
                    val type: String // Point
                )
            }

            @Keep data class BidDetails(
                @field:Json(name = "amount")
                val amount: Int, // 1000
                @field:Json(name = "_id")
                val _id: String, // 5fe9f3ead87d2a239e7a9a1c
                @field:Json(name = "timestamp")
                val timestamp: String // 2020-12-28T15:04:10.004Z
            )
        }

        @Keep data class LastBid(
            @field:Json(name = "amount")
            val amount: Int, // 0
            @field:Json(name = "_id")
            val _id: String, // 5fe9f3ead87d2a239e7a9a1d
            @field:Json(name = "timestamp")
            val timestamp: String // 2020-12-26T16:05:16.797Z
        )

        @Keep data class Location(
            @field:Json(name = "coordinates")
            val coordinates: List<Double>,
            @field:Json(name = "type")
            val type: String // Point
        )

        @Keep data class Supplier(
            @field:Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-24T14:12:31.396Z
            @field:Json(name = "address")
            val address: String, // b381, asia
            @field:Json(name = "area")
            val area: Area,
            @field:Json(name = "country")
            val country: String, // ind
            @field:Json(name = "district")
            val district: String, // del
            @field:Json(name = "fuid")
            val fuid: String, // dt38CMkohngtNrkysK1Sr73RxLg1
            @field:Json(name = "_id")
            val _id: String, // 5fe4a1cf999f93ab98ce0549
            @field:Json(name = "location")
            val location: Location,
            @field:Json(name = "name")
            val name: String, // kavya vatsal
            @field:Json(name = "phone")
            val phone: String, // +9196103069
            @field:Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZTRhMWNmOTk5ZjkzYWI5OGNlMDU0OSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTk2MTAzMDY5IiwibG9jYXRpb24iOnsidHlwZSI6IlBvaW50IiwiY29vcmRpbmF0ZXMiOls5LjQzODkzMywyLjk0MDQzNTNdfSwiaXNGYXJtZXIiOnRydWUsImlhdCI6MTYwOTE3NTY3MX0.kTi8R7874cgTLmFvAMWerrA76frKZUZMMaEExKVGaNM
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