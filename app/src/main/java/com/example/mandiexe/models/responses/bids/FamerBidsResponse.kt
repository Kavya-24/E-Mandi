package com.example.mandiexe.models.responses.bids


import androidx.annotation.Keep
import com.squareup.moshi.Json


 @Keep data class FamerBidsResponse(
    @Json(name = "bids")
    val bids: List<Bid>
) {
    @Keep data class Bid(
        @Json(name = "active")
        val active: Boolean, // true
        @Json(name = "bidDate")
        val bidDate: String, // 2021-02-05T18:10:44.693Z
        @Json(name = "bidder")
        val bidder: String, // 5fe4a1cf999f93ab98ce0549
        @Json(name = "bids")
        val bids: List<BidDetails>,
        @Json(name = "currentBid")
        val currentBid: Int, // 10000
        @Json(name = "demand")
        val demand: List<Demand>,
        @Json(name = "demander")
        val demander: List<Demander>,
        @Json(name = "_id")
        val _id: String, // 601d8a24253abdfd21015217
        @Json(name = "lastModified")
        val lastModified: String, // 2021-02-05T18:10:44.693Z
        @Json(name = "__v")
        val v: Int // 0
    ) {
        @Keep data class BidDetails(
            @Json(name = "amount")
            val amount: Int, // 10000
            @Json(name = "_id")
            val _id: String, // 601d8a24253abdfd21015218
            @Json(name = "timestamp")
            val timestamp: String // 2021-02-05T18:10:44.693Z
        )

        @Keep data class Demand(
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
            @Keep data class LastBid(
                @Json(name = "amount")
                val amount: Int, // 0
                @Json(name = "_id")
                val _id: String, // 601d8a24253abdfd21015219
                @Json(name = "timestamp")
                val timestamp: String // 2021-02-04T18:32:31.389Z
            )

            @Keep data class Location(
                @Json(name = "coordinates")
                val coordinates: List<Double>,
                @Json(name = "type")
                val type: String // Point
            )
        }

        @Keep data class Demander(
            @Json(name = "address")
            val address: String, // b381, asia
            @Json(name = "country")
            val country: String, // ind
            @Json(name = "district")
            val district: String, // del
            @Json(name = "_id")
            val _id: String, // 5fea3e68b7f8bf2537b194ec
            @Json(name = "location")
            val location: Location,
            @Json(name = "name")
            val name: String, // kavya vatsal
            @Json(name = "phone")
            val phone: String, // +919610306949
            @Json(name = "state")
            val state: String, // del
            @Json(name = "__v")
            val v: Int, // 0
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
    }
}