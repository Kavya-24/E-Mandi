package com.example.mandiexe.models.responses.supply


import com.squareup.moshi.Json

data class ViewSupplyResponse(
    @Json(name = "msg")
    val msg: String, // Supply retrieved successfully.
    @Json(name = "supply")
    val supply: Supply
) {
    data class Supply(

        @Json(name = "active")
        val active: Boolean, // true

        @Json(name = "askPrice")
        val askPrice: Int, // 2000

        @Json(name = "bids")
        val bids: List<Bid>,

        @Json(name = "change")
        val change: Int, // 0

        @Json(name = "crop")
        val crop: String, // Wheat

        @Json(name = "currentBid")
        val currentBid: Int, // 900

        @Json(name = "dateOfHarvest")
        val dateOfHarvest: String, // 2021-01-15T18:29:59.000Z

        @Json(name = "description")
        val description: String, // NA

        @Json(name = "expiry")
        val expiry: String, // 2021-01-15T18:29:59.000Z

        @Json(name = "_id")
        val _id: String, // 5fe13d964a73f913eb6daf17

        @Json(name = "lastBid")
        val lastBid: List<LastBid>,

        @Json(name = "lastModified")
        val lastModified: String, // 2020-12-22T00:55:23.611Z

        @Json(name = "location")
        val location: Location,

        @Json(name = "qty")
        val qty: Int, // 100

        @Json(name = "supplier")
        val supplier: Supplier,

        @Json(name = "supplyCreated")
        val supplyCreated: String, // 2020-12-22T00:28:06.236Z

        @Json(name = "__v")
        val v: Int, // 5

        @Json(name = "variety")
        val variety: String // NA
    ) {

        data class Bid(

            @Json(name = "active")
            val active: Boolean, // true

            @Json(name = "bidDate")
            val bidDate: String, // 2020-12-22T00:55:23.373Z

            @Json(name = "bidder")
            val bidder: String, // 5fe13807e0cdc613d2894706

            @Json(name = "bids")
            val bids: List<Bid>,

            @Json(name = "currentBid")
            val currentBid: Int, // 900

            @Json(name = "_id")
            val id: String, // 5fe143fbfbb6eb1401bd1080

            @Json(name = "lastModified")
            val lastModified: String, // 2020-12-22T00:55:23.373Z

            @Json(name = "supply")
            val supply: String, // 5fe13d964a73f913eb6daf17

            @Json(name = "__v")
            val v: Int // 0

        ) {
            data class Bid(
                @Json(name = "amount")
                val amount: Int, // 900
                @Json(name = "_id")
                val id: String, // 5fe143fbfbb6eb1401bd1081
                @Json(name = "timestamp")
                val timestamp: String // 2020-12-22T00:55:23.373Z
            )
        }

        data class LastBid(

            @Json(name = "amount")
            val amount: Int, // 0

            @Json(name = "_id")
            val id: String, // 5fe13fb24a73f913eb6daf1e

            @Json(name = "timestamp")
            val timestamp: String // 2020-12-22T00:28:06.236Z
        )

        data class Location(

            @Json(name = "coordinates")
            val coordinates: List<Double>,

            @Json(name = "type")
            val type: String // Point
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
    }
}