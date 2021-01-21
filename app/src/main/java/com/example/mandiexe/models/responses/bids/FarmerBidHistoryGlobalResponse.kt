package com.example.mandiexe.models.responses.bids


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class FarmerBidHistoryGlobalResponse(
    @field:Json(name = "bids")
    val bids: List<FarmerBidHistoryGlobalResponse.Bid>
) {

    @Keep data class Bid(
        @field:Json(name = "active")
        val active: Boolean, // false
        @field:Json(name = "bidDate")
        val bidDate: String, // 2020-12-21T15:53:47.017Z
        @field:Json(name = "bidder")
        val bidder: String, // 5fdfac963f52f60c2356dcd5
        @field:Json(name = "bids")
        val bids: List<FarmerBidHistoryGlobalResponse.Bid.BidDetail>,
        @field:Json(name = "currentBid")
        val currentBid: Int, // 10000
        @field:Json(name = "demand")
        val demand: Demand,
        @field:Json(name = "_id")
        val _id: String, // 5fe0c50b6bf4390fd95a1fc5
        @field:Json(name = "lastModified")
        val lastModified: String, // 2020-12-21T15:53:47.017Z
        @Json(name = "qty")
        val qty: Int, // 890
        @Json(name = "__v")
        val v: Int // 0
    ) {

        @Keep data class BidDetail(
            @Json(name = "amount")
            val amount: Int, // 10000
            @Json(name = "_id")
            val _id: String, // 5fe0c50b6bf4390fd95a1fc6
            @Json(name = "timestamp")
            val timestamp: String // 2020-12-21T15:53:47.017Z
        )


        @Keep data class Demand(
            @Json(name = "active")
            val active: Boolean, // true
            @Json(name = "bids")
            val bids: List<String>,
            @Json(name = "change")
            val change: Int, // 0
            @Json(name = "crop")
            val crop: String, // Wheat
            @Json(name = "currentBid")
            val currentBid: Int, // 8000
            @Json(name = "dateOfRequirement")
            val dateOfRequirement: String, // 2020-01-15T18:29:59.000Z
            @Json(name = "demandCreated")
            val demandCreated: String, // 2020-12-20T20:40:27.791Z
            @Json(name = "demander")
            val demander: String, // 5fdfadc5a28db10c34dba899
            @Json(name = "description")
            val description: String, // NA
            @Json(name = "expiry")
            val expiry: String, // 2020-01-15T18:29:59.000Z
            @Json(name = "_id")
            val _id: String, // 5fdfb6bbad3ef90c65e9c2c8
            @Json(name = "lastBid")
            val lastBid: Int, // 12000
            @Json(name = "lastModified")
            val lastModified: String, // 2020-12-21T16:04:07.471Z
            @Json(name = "offerPrice")
            val offerPrice: Int, // 2000
            @Json(name = "qty")
            val qty: Int, // 100
            @Json(name = "__v")
            val v: Int, // 1
            @Json(name = "variety")
            val variety: String // NA
        )
    }
}