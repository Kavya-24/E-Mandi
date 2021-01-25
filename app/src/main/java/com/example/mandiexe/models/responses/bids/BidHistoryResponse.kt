package com.example.mandiexe.models.responses.bids


import androidx.annotation.Keep
import com.squareup.moshi.Json

 @Keep data class BidHistoryResponse(
    @field:Json(name = "bids")
    val bids: List<Bid>
) {
     @Keep
     data class Bid(
        @field:Json(name = "active")
        val active: Boolean, // false
        @field:Json(name = "bidDate")
        val bidDate: String, // 2020-12-21T15:53:47.017Z
        @field:Json(name = "bidder")
        val bidder: String, // 5fdfac963f52f60c2356dcd5
        @field:Json(name = "bids")
        val bids: List<BidDetail>,
        @field:Json(name = "currentBid")
        val currentBid: Int, // 10000
        @field:Json(name = "demand")
        val demand: Demand,
        @field:Json(name = "_id")
        val _id: String, // 5fe0c50b6bf4390fd95a1fc5
        //##JSON NAME FOR SUPPLIER
        @field:Json(name = "supplier")
        val supplier: Supplier, // 5fe0c50b6bf4390fd95a1fc5

        @field:Json(name = "lastModified")
        val lastModified: String, // 2020-12-21T15:53:47.017Z
        @field:Json(name = "qty")
        val qty: Int, // 890
        @field:Json(name = "__v")
        val v: Int // 0
    ) {
         @Keep data class BidDetail(
            @field:Json(name = "amount")
            val amount: Int, // 10000
            @field:Json(name = "_id")
            val _id: String, // 5fe0c50b6bf4390fd95a1fc6
            @field:Json(name = "timestamp")
            val timestamp: String // 2020-12-21T15:53:47.017Z
        )

         @Keep data class Supplier(
            @field:Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-22T00:03:38.927Z
            @field:Json(name = "address")
            val address: String, // b381, asia
            @field:Json(name = "area")
            val area: Area,
            @field:Json(name = "country")
            val country: String, // ind
            @field:Json(name = "district")
            val district: String, // del
            @field:Json(name = "fuid")
            val fuid: String, // S2ustzAO4FeFSgCANfZtNRl7iZ42
            @field:Json(name = "_id")
            val id: String, // 5fe137dbe0cdc613d2894705
            @field:Json(name = "location")
            val location: Location,
            @field:Json(name = "name")
            val name: String, // kavya vatsal
            @field:Json(name = "phone")
            val phone: String, // +918585992062
            @field:Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZTEzN2RiZTBjZGM2MTNkMjg5NDcwNSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJsb2NhdGlvbiI6eyJ0eXBlIjoiUG9pbnQiLCJjb29yZGluYXRlcyI6WzkuNDM4OTMzLDIuOTQwNDM1M119LCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NTk1NDcyfQ.ZFsx5-9gzKp36bwyonG3eWJjS8afWiMpJGvv0lHX_eQ
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