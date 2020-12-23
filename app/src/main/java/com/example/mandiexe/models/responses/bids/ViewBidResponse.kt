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
        val bidDate: String, // 2020-12-21T15:58:20.851Z

        @Json(name = "bidder")
        val bidder: Bidder,

        @Json(name = "bids")
        val bids: List<Bid>,

        @Json(name = "currentBid")
        val currentBid: Int, // 8000

        @Json(name = "demand")
        val demand: Demand,

        @Json(name = "_id")
        val _id: String, // 5fe0c61e3dde370fe0e57f89

        @Json(name = "lastModified")
        val lastModified: String, // 2020-12-21T16:04:07.471Z

        @Json(name = "qty")
        val qty: Int, // 890

        @Json(name = "__v")
        val v: Int // 0
    ) {

        data class Bidder(

            @Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-20T19:57:10.845Z

            @Json(name = "address")
            val address: String, // b381, asia

            @Json(name = "area")
            val area: Area,

            @Json(name = "bids")
            val bids: List<ViewBidResponse.Bid.Bid>,

            @Json(name = "country")
            val country: String, // ind

            @Json(name = "district")
            val district: String, // del

            @Json(name = "fuid")
            val fuid: String, // S2ustzAO4FeFSgCANfZtNRl7iZ42

            @Json(name = "_id")
            val _id: String, // 5fdfac963f52f60c2356dcd5

            @Json(name = "location")
            val location: Location,

            @Json(name = "name")
            val name: String, // kavya vatsal

            @Json(name = "phone")
            val phone: String, // +918585992062

            @Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGZhYzk2M2Y1MmY2MGMyMzU2ZGNkNSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NTY1NzY2fQ.bb6wiR5fuC3opTzGX7g6V0tBrFrvuR8tC5dE5-rIOec

            @Json(name = "state")
            val state: String, // del

            @Json(name = "supplies")
            val supplies: List<Any>,

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

        data class Bid(

            @Json(name = "amount")
            val amount: Int, // 10000

            @Json(name = "_id")
            val id: String, // 5fe0c61e3dde370fe0e57f8a

            @Json(name = "timestamp")
            val timestamp: String // 2020-12-21T15:58:20.851Z
        )

        data class Demand(

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