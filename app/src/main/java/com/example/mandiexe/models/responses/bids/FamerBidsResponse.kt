package com.example.mandiexe.models.responses.bids


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep
data class FamerBidsResponse(
    @field:Json(name = "bids")
    val bids: List<Bid>
) {
    @Keep
    data class Bid(
        @field:Json(name = "active")
        val active: Boolean, // true
        @field:Json(name = "bidDate")
        val bidDate: String, // 2021-02-05T18:10:44.693Z
        @field:Json(name = "bidder")
        val bidder: String, // 5fe4a1cf999f93ab98ce0549
        @field:Json(name = "bids")
        val bids: List<BidDetails>,
        @field:Json(name = "currentBid")
        val currentBid: Int, // 10000
        @field:Json(name = "demand")
        val demand: List<Demand>,
        @field:Json(name = "demander")
        val demander: List<Demander>,
        @field:Json(name = "_id")
        val _id: String, // 601d8a24253abdfd21015217
        @field:Json(name = "lastModified")
        val lastModified: String, // 2021-02-05T18:10:44.693Z
        @field:Json(name = "__v")
        val v: Int // 0
    ) {
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
            val v: Int, // 1
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

        @Keep
        data class Demander(
            @field:Json(name = "address")
            val address: String, // b381, asia
            @field:Json(name = "country")
            val country: String, // ind
            @field:Json(name = "district")
            val district: String, // del
            @field:Json(name = "_id")
            val _id: String, // 5fea3e68b7f8bf2537b194ec
            @field:Json(name = "location")
            val location: Location,
            @field:Json(name = "name")
            val name: String, // kavya vatsal
            @field:Json(name = "phone")
            val phone: String, // +919610306949
            @field:Json(name = "state")
            val state: String, // del
            @field:Json(name = "__v")
            val v: Int, // 0
            @field:Json(name = "village")
            val village: String // greno
        ) {
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