package com.example.mandiexe.models.responses.bids

import com.squareup.moshi.Json


data class ViewBidResponse(
    @Json(name = "bid")
    val bid: Bid,
    @Json(name = "msg")
    val msg: String // Bid retrieved successfully.
) {
    data class Bid(
        @Json(name = "active")
        val active: Boolean, // true
        @Json(name = "bidDate")
        val bidDate: String, // 2021-02-05T18:10:44.693Z
        @Json(name = "bidder")
        val bidder: Bidder,
        @Json(name = "bids")
        val bids: List<BidDetails>,
        @Json(name = "currentBid")
        val currentBid: Int, // 10000
        @Json(name = "demand")
        val demand: Demand,
        @Json(name = "_id")
        val _id: String, // 601d8a24253abdfd21015217
        @Json(name = "lastModified")
        val lastModified: String, // 2021-02-05T18:10:44.693Z
        @Json(name = "__v")
        val v: Int // 0
    ) {
        data class Bidder(
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
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZTRhMWNmOTk5ZjkzYWI5OGNlMDU0OSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTk2MTAzMDY5IiwibG9jYXRpb24iOnsidHlwZSI6IlBvaW50IiwiY29vcmRpbmF0ZXMiOls5LjQzODkzMywyLjk0MDQzNTNdfSwiaXNGYXJtZXIiOnRydWUsImlhdCI6MTYxMjM0Njc1NX0.zxiNfq4OoMmCobpK4j25CF-mhO9vkiNFmbi3S-X6w2s
            @Json(name = "state")
            val state: String, // del
            @Json(name = "__v")
            val v: Int, // 0
            @Json(name = "village")
            val village: String // greno
        ) {
            data class Area(
                @Json(name = "numerical")
                val numerical: Int, // 100
                @Json(name = "unit")
                val unit: String // acre
            )

            data class Location(
                @Json(name = "coordinates")
                val coordinates: List<Double>,
                @Json(name = "type")
                val type: String // Point
            )
        }

        data class BidDetails(
            @Json(name = "amount")
            val amount: Int, // 10000
            @Json(name = "_id")
            val _id: String, // 601d8a24253abdfd21015218
            @Json(name = "timestamp")
            val timestamp: String // 2021-02-05T18:10:44.693Z
        )

        data class Demand(
            @Json(name = "active")
            val active: Boolean, // true
            @Json(name = "bids")
            val bids: List<String>,
            @Json(name = "change")
            val change: Int, // 0
            @Json(name = "crop")
            val crop: String, // Rice
            @Json(name = "currentBid")
            val currentBid: Int, // 10000
            @Json(name = "dateOfRequirement")
            val dateOfRequirement: String, // 2021-03-21T00:00:00.000Z
            @Json(name = "demandCreated")
            val demandCreated: String, // 2021-02-04T18:32:31.389Z
            @Json(name = "demander")
            val demander: String, // 5fea3e68b7f8bf2537b194ec
            @Json(name = "description")
            val description: String, // I want pure white Basmati rice
            @Json(name = "expiry")
            val expiry: String, // 2021-03-14T00:00:00.000Z
            @Json(name = "_id")
            val _id: String, // 601c3dbf253abdfd21015214
            @Json(name = "lastBid")
            val lastBid: List<LastBid>,
            @Json(name = "lastModified")
            val lastModified: String, // 2021-02-05T18:10:44.710Z
            @Json(name = "location")
            val location: Location,
            @Json(name = "offerPrice")
            val offerPrice: Int, // 20000
            @Json(name = "qty")
            val qty: Int, // 100
            @Json(name = "__v")
            val v: Int, // 1
            @Json(name = "variety")
            val variety: String // Basmati
        ) {
            data class LastBid(
                @Json(name = "amount")
                val amount: Int, // 0
                @Json(name = "_id")
                val _id: String, // 601d8a24253abdfd21015219
                @Json(name = "timestamp")
                val timestamp: String // 2021-02-04T18:32:31.389Z
            )

            data class Location(
                @Json(name = "coordinates")
                val coordinates: List<Double>,
                @Json(name = "type")
                val type: String // Point
            )
        }
    }
}