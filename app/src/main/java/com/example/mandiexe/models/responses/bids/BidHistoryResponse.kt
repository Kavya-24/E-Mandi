package com.example.mandiexe.models.responses.bids


import com.squareup.moshi.Json

data class BidHistoryResponse(
    @Json(name = "bids")
    val bids: List<Bid>
) {
    data class Bid(
        @Json(name = "active")
        val active: Boolean, // false
        @Json(name = "bidDate")
        val bidDate: String, // 2020-12-21T15:53:47.017Z
        @Json(name = "bidder")
        val bidder: String, // 5fdfac963f52f60c2356dcd5
        @Json(name = "bids")
        val bids: List<BidDetail>,
        @Json(name = "currentBid")
        val currentBid: Int, // 10000
        @Json(name = "demand")
        val demand: Demand,
        @Json(name = "_id")
        val _id: String, // 5fe0c50b6bf4390fd95a1fc5
        //##JSON NAME FOR SUPPLIER
        @Json(name = "supplier")
        val supplier: Supplier, // 5fe0c50b6bf4390fd95a1fc5

        @Json(name = "lastModified")
        val lastModified: String, // 2020-12-21T15:53:47.017Z
        @Json(name = "qty")
        val qty: Int, // 890
        @Json(name = "__v")
        val v: Int // 0
    ) {
        data class BidDetail(
            @Json(name = "amount")
            val amount: Int, // 10000
            @Json(name = "_id")
            val _id: String, // 5fe0c50b6bf4390fd95a1fc6
            @Json(name = "timestamp")
            val timestamp: String // 2020-12-21T15:53:47.017Z
        )

        data class Supplier(
            @Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-22T00:03:38.927Z
            @Json(name = "address")
            val address: String, // b381, asia
            @Json(name = "area")
            val area: Area,
            @Json(name = "country")
            val country: String, // ind
            @Json(name = "district")
            val district: String, // del
            @Json(name = "fuid")
            val fuid: String, // S2ustzAO4FeFSgCANfZtNRl7iZ42
            @Json(name = "_id")
            val id: String, // 5fe137dbe0cdc613d2894705
            @Json(name = "location")
            val location: Location,
            @Json(name = "name")
            val name: String, // kavya vatsal
            @Json(name = "phone")
            val phone: String, // +918585992062
            @Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZTEzN2RiZTBjZGM2MTNkMjg5NDcwNSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJsb2NhdGlvbiI6eyJ0eXBlIjoiUG9pbnQiLCJjb29yZGluYXRlcyI6WzkuNDM4OTMzLDIuOTQwNDM1M119LCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NTk1NDcyfQ.ZFsx5-9gzKp36bwyonG3eWJjS8afWiMpJGvv0lHX_eQ
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