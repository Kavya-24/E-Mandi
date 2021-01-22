package com.example.mandiexe.models.responses.bids


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class ViewBidResponse(

    @field:Json(name = "bid")
    val bid: Bid,

    @field:Json(name = "msg")
    val msg: String // Bid retrieved successfully.
) {

    @Keep data class Bid(

        @field:Json(name = "active")
        val active: Boolean, // true

        @field:Json(name = "bidDate")
        val bidDate: String, // 2020-12-21T15:58:20.851Z

        @field:Json(name = "bidder")
        val bidder: Bidder,

        @field:Json(name = "bids")
        val bids: List<BidDetails>,

        @field:Json(name = "currentBid")
        val currentBid: Int, // 8000

        @field:Json(name = "demand")
        val demand: Demand,

        @field:Json(name = "_id")
        val _id: String, // 5fe0c61e3dde370fe0e57f89

        @field:Json(name = "lastModified")
        val lastModified: String, // 2020-12-21T16:04:07.471Z

        @field:Json(name = "qty")
        val qty: Int, // 890

        @field:Json(name = "__v")
        val v: Int // 0
    ) {


        @Keep data class Bidder(

            @field:Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-20T19:57:10.845Z

            @field:Json(name = "address")
            val address: String, // b381, asia

            @field:Json(name = "area")
            val area: Area,

            @field:Json(name = "bids")
            val bids: List<ViewBidResponse.Bid.BidDetails>,

            @field:Json(name = "country")
            val country: String, // ind

            @field:Json(name = "district")
            val district: String, // del

            @field:Json(name = "fuid")
            val fuid: String, // S2ustzAO4FeFSgCANfZtNRl7iZ42

            @field:Json(name = "_id")
            val _id: String, // 5fdfac963f52f60c2356dcd5

            @field:Json(name = "location")
            val location: Location,

            @field:Json(name = "name")
            val name: String, // kavya vatsal

            @field:Json(name = "phone")
            val phone: String, // +918585992062

            @field:Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGZhYzk2M2Y1MmY2MGMyMzU2ZGNkNSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NTY1NzY2fQ.bb6wiR5fuC3opTzGX7g6V0tBrFrvuR8tC5dE5-rIOec

            @field:Json(name = "state")
            val state: String, // del

            @field:Json(name = "supplies")
            val supplies: List<Any>,

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


        @Keep data class BidDetails(

            @field:Json(name = "amount")
            val amount: Int, // 10000

            @field:Json(name = "_id")
            val _id: String, // 5fe0c61e3dde370fe0e57f8a

            @field:Json(name = "timestamp")
            val timestamp: String // 2020-12-21T15:58:20.851Z
        )


        @Keep data class Demand(

            @field:Json(name = "active")
            val active: Boolean, // true

            @field:Json(name = "bids")
            val bids: List<String>,

            @field:Json(name = "change")
            val change: Int, // 0

            @field:Json(name = "crop")
            val crop: String, // Wheat

            @field:Json(name = "currentBid")
            val currentBid: Int, // 8000

            @field:Json(name = "dateOfRequirement")
            val dateOfRequirement: String, // 2020-01-15T18:29:59.000Z

            @field:Json(name = "demandCreated")
            val demandCreated: String, // 2020-12-20T20:40:27.791Z

            @field:Json(name = "demander")
            val demander: String, // 5fdfadc5a28db10c34dba899

            @field:Json(name = "description")
            val description: String, // NA

            @field:Json(name = "expiry")
            val expiry: String, // 2020-01-15T18:29:59.000Z

            @field:Json(name = "_id")
            val _id: String, // 5fdfb6bbad3ef90c65e9c2c8

            @field:Json(name = "lastBid")
            val lastBid: Int, // 12000

            @field:Json(name = "lastModified")
            val lastModified: String, // 2020-12-21T16:04:07.471Z

            @field:Json(name = "offerPrice")
            val offerPrice: Int, // 2000

            @field:Json(name = "qty")
            val qty: Int, // 100

            @field:Json(name = "__v")
            val v: Int, // 1

            @field:Json(name = "variety")
            val variety: String // NA
        )

    }
}