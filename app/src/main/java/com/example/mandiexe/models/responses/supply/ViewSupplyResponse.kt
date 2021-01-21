package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ViewSupplyResponse(

    @Json(name = "msg")
    val msg: String, // Supply retrieved successfully.

    @Json(name = "supply")
    val supply: Supply
) {
    @Keep data class Supply(

        @Json(name = "active")
        val active: Boolean, // true

        @Json(name = "askPrice")
        val askPrice: Int, // 30000

        @Json(name = "bids")
        val bids: List<Bid>,

        @Json(name = "change")
        val change: Int, // 0

        @Json(name = "crop")
        val crop: String, // Sugarcane

        @Json(name = "currentBid")
        val currentBid: Int, // 100000

        @Json(name = "dateOfHarvest")
        val dateOfHarvest: String, // 2021-02-27T00:00:00.000Z

        @Json(name = "description")
        val description: String, // Sugarcane from Uttar Pradesh

        @Json(name = "expiry")
        val expiry: String, // 2021-03-17T00:00:00.000Z

        @Json(name = "_id")
        val _id: String, // 5fe75f3c709347e81377726b

        @Json(name = "lastBid")
        val lastBid: List<LastBid>,

        @Json(name = "lastModified")
        val lastModified: String, // 2020-12-28T15:19:14.400Z

        @Json(name = "location")
        val location: Location,

        @Json(name = "qty")
        val qty: Int, // 0

        @Json(name = "supplier")
        val supplier: Supplier,

        @Json(name = "supplyCreated")
        val supplyCreated: String, // 2020-12-26T16:05:16.797Z

        @Json(name = "__v")
        val v: Int, // 2

        @Json(name = "variety")
        val variety: String // sweet
    ) {
        @Keep data class Bid(

            @Json(name = "active")
            val active: Boolean, // true

            @Json(name = "bidDate")
            val bidDate: String, // 2020-12-28T15:04:10.004Z

            @Json(name = "bidder")
            val bidder: Bidder,

            @Json(name = "bids")
            val bids: List<BidDetails>,

            @Json(name = "currentBid")
            val currentBid: Int, // 100000

            @Json(name = "_id")
            val _id: String, // 5fe9f3ead87d2a239e7a9a1b

            @Json(name = "lastModified")
            val lastModified: String, // 2020-12-28T15:19:14.400Z

            @Json(name = "supply")
            val supply: String, // 5fe75f3c709347e81377726b

            @Json(name = "__v")
            val v: Int // 0
        ) {
            @Keep data class Bidder(

                @Json(name = "address")
                val address: String, // b381, asia
                @Json(name = "district")
                val district: String, // del
                @Json(name = "_id")
                val _id: String, // 5fe13807e0cdc613d2894706
                @Json(name = "location")
                val location: Location,
                @Json(name = "name")
                val name: String, // kavya vatsal
                @Json(name = "phone")
                val phone: String, // +918585992062
                @Json(name = "state")
                val state: String, // del
                @Json(name = "village")
                val village: String // greno
            ) {
                @Keep data class Location(
                    @Json(name = "coordinates")
                    val coordinates: List<Double>,
                    @Json(name = "type")
                    val type: String // Point
                )
            }

            @Keep data class BidDetails(
                @Json(name = "amount")
                val amount: Int, // 1000
                @Json(name = "_id")
                val _id: String, // 5fe9f3ead87d2a239e7a9a1c
                @Json(name = "timestamp")
                val timestamp: String // 2020-12-28T15:04:10.004Z
            )
        }

        @Keep data class LastBid(
            @Json(name = "amount")
            val amount: Int, // 0
            @Json(name = "_id")
            val _id: String, // 5fe9f3ead87d2a239e7a9a1d
            @Json(name = "timestamp")
            val timestamp: String // 2020-12-26T16:05:16.797Z
        )

        @Keep data class Location(
            @Json(name = "coordinates")
            val coordinates: List<Double>,
            @Json(name = "type")
            val type: String // Point
        )

        @Keep data class Supplier(
            @Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-24T14:12:31.396Z
            @Json(name = "address")
            val address: String, // b381, asia
            @Json(name = "area")
            val area: Area,
            @Json(name = "country")
            val country: String, // ind
            @Json(name = "district")
            val district: String, // del
            @Json(name = "fuid")
            val fuid: String, // dt38CMkohngtNrkysK1Sr73RxLg1
            @Json(name = "_id")
            val _id: String, // 5fe4a1cf999f93ab98ce0549
            @Json(name = "location")
            val location: Location,
            @Json(name = "name")
            val name: String, // kavya vatsal
            @Json(name = "phone")
            val phone: String, // +9196103069
            @Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZTRhMWNmOTk5ZjkzYWI5OGNlMDU0OSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTk2MTAzMDY5IiwibG9jYXRpb24iOnsidHlwZSI6IlBvaW50IiwiY29vcmRpbmF0ZXMiOls5LjQzODkzMywyLjk0MDQzNTNdfSwiaXNGYXJtZXIiOnRydWUsImlhdCI6MTYwOTE3NTY3MX0.kTi8R7874cgTLmFvAMWerrA76frKZUZMMaEExKVGaNM
            @Json(name = "state")
            val state: String, // del
            @Json(name = "__v")
            val v: Int, // 0
            @Json(name = "village")
            val village: String // greno
        ) {
            @Keep data class Area(
                @Json(name = "numerical")
                val numerical: Int, // 100
                @Json(name = "unit")
                val unit: String // acre
            )

            @Keep data class Location(
                @Json(name = "coordinates")
                val coordinates: List<Double>,
                @Json(name = "type")
                val type: String // Point
            )
        }
    }
}