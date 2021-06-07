package com.example.mandiexe.models.responses.bids

import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep
data class ViewBidResponse(
    @field:Json(name = "bid")
    val bid: Bid,
    @field:Json(name = "msg")
    val msg: String // Bid retrieved successfully.
) {

    @Keep
    data class Bid(
        @field:Json(name = "active")
        val active: Boolean, // true
        @field:Json(name = "bidDate")
        val bidDate: String, // 2021-02-05T18:10:44.693Z
        @field:Json(name = "bidder")
        val bidder: Bidder,
        @field:Json(name = "bids")
        val bids: List<BidDetails>,
        @field:Json(name = "currentBid")
        val currentBid: Int, // 10000
        @field:Json(name = "demand")
        val demand: Demand,
        @field:Json(name = "_id")
        val _id: String, // 601d8a24253abdfd21015217
        @field:Json(name = "lastModified")
        val lastModified: String, // 2021-02-05T18:10:44.693Z
        @field:Json(name = "__v")
        val __v: Int // 0
    ) {
        @Keep
        data class Bidder(
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
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZTRhMWNmOTk5ZjkzYWI5OGNlMDU0OSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTk2MTAzMDY5IiwibG9jYXRpb24iOnsidHlwZSI6IlBvaW50IiwiY29vcmRpbmF0ZXMiOls5LjQzODkzMywyLjk0MDQzNTNdfSwiaXNGYXJtZXIiOnRydWUsImlhdCI6MTYxMjM0Njc1NX0.zxiNfq4OoMmCobpK4j25CF-mhO9vkiNFmbi3S-X6w2s
            @field:Json(name = "state")
            val state: String, // del
            @field:Json(name = "__v")
            val __v: Int, // 0
            @field:Json(name = "village")
            val village: String // greno
        ) {
            @Keep
            data class Area(
                @field:Json(name = "numerical")
                val numerical: Int, // 100
                @field:Json(name = "unit")
                val unit: String // acre
            )

            @Keep
            data class Location(
                @field:Json(name = "coordinates")
                val coordinates: List<Double>,
                @field:Json(name = "type")
                val type: String // Point
            )
        }

        @Keep
        data class BidDetails(
            @field:Json(name = "amount")
            val amount: Int, // 10000
            @field:Json(name = "_id")
            val _id: String, // 601d8a24253abdfd21015218
            @field:Json(name = "timestamp")
            val timestamp: String // 2021-02-05T18:10:44.693Z
        )

        @Keep
        data class Demand(
            @field:Json(name = "active")
            val active: Boolean, // true
            @field:Json(name = "bids")
            val bids: List<String>,
            @field:Json(name = "change")
            val change: Int, // 0
            @field:Json(name = "crop")
            val crop: String, // Rice
            @field:Json(name = "currentBid")
            val currentBid: Int, // 10000
            @field:Json(name = "dateOfRequirement")
            val dateOfRequirement: String, // 2021-03-21T00:00:00.000Z
            @field:Json(name = "demandCreated")
            val demandCreated: String, // 2021-02-04T18:32:31.389Z
            @field:Json(name = "demander")
            val demander: String, // 5fea3e68b7f8bf2537b194ec
            @field:Json(name = "description")
            val description: String, // I want pure white Basmati rice
            @field:Json(name = "expiry")
            val expiry: String, // 2021-03-14T00:00:00.000Z
            @field:Json(name = "_id")
            val _id: String, // 601c3dbf253abdfd21015214
            @field:Json(name = "lastBid")
            val lastBid: List<LastBid>,
            @field:Json(name = "lastModified")
            val lastModified: String, // 2021-02-05T18:10:44.710Z
            @field:Json(name = "location")
            val location: Location,
            @field:Json(name = "offerPrice")
            val offerPrice: Int, // 20000
            @field:Json(name = "qty")
            val qty: Int, // 100
            @field:Json(name = "__v")
            val __v: Int, // 1
            @field:Json(name = "variety")
            val variety: String // Basmati
        ) {
            @Keep
            data class LastBid(
                @field:Json(name = "amount")
                val amount: Int, // 0
                @field:Json(name = "_id")
                val _id: String, // 601d8a24253abdfd21015219
                @field:Json(name = "timestamp")
                val timestamp: String // 2021-02-04T18:32:31.389Z
            )

            @Keep
            data class Location(
                @field:Json(name = "coordinates")
                val coordinates: List<Double>,
                @field:Json(name = "type")
                val type: String // Point
            )
        }
    }
}

