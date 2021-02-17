package com.example.mandiexe.models.responses.bids


import com.squareup.moshi.Json

data class ViewDemandResponse(
    @Json(name = "demand")
    val demand: Demand,
    @Json(name = "msg")
    val msg: String // Demand retrieved successfully.
) {
    data class Demand(
        @Json(name = "active")
        val active: Boolean, // true
        @Json(name = "bids")
        val bids: List<BidDetails>,
        @Json(name = "change")
        val change: Int, // 0
        @Json(name = "crop")
        val crop: String, // Rice
        @Json(name = "currentBid")
        val currentBid: Int, // 0
        @Json(name = "dateOfRequirement")
        val dateOfRequirement: String, // 2021-03-06T00:00:00.000Z
        @Json(name = "demandCreated")
        val demandCreated: String, // 2021-02-06T16:27:01.646Z
        @Json(name = "demander")
        val demander: Demander,
        @Json(name = "description")
        val description: String, // Test
        @Json(name = "expiry")
        val expiry: String, // 2021-03-02T00:00:00.000Z
        @Json(name = "_id")
        val _id: String, // 601ec3555109c7a1f3f0f4d2
        @Json(name = "lastBid")
        val lastBid: List<BidDetails>,
        @Json(name = "lastModified")
        val lastModified: String, // 2021-02-06T16:27:01.646Z
        @Json(name = "location")
        val location: Location,
        @Json(name = "offerPrice")
        val offerPrice: Int, // 2000
        @Json(name = "qty")
        val qty: Int, // 50
        @Json(name = "__v")
        val __v: Int, // 0
        @Json(name = "variety")
        val variety: String // Test
    ) {
        data class Demander(
            @Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-28T20:22:00.537Z
            @Json(name = "address")
            val address: String, // b381, asia
            @Json(name = "country")
            val country: String, // ind
            @Json(name = "district")
            val district: String, // del
            @Json(name = "fuid")
            val fuid: String, // dt38CMkohngtNrkysK1Sr73RxLg1
            @Json(name = "_id")
            val _id: String, // 5fea3e68b7f8bf2537b194ec
            @Json(name = "location")
            val location: Location,
            @Json(name = "name")
            val name: String, // kavya vatsal
            @Json(name = "phone")
            val phone: String, // +919610306949
            @Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZWEzZTY4YjdmOGJmMjUzN2IxOTRlYyIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTk2MTAzMDY5NDkiLCJsb2NhdGlvbiI6eyJ0eXBlIjoiUG9pbnQiLCJjb29yZGluYXRlcyI6WzkuNDM4OTMzLDIuOTQwNDM1M119LCJpc1RyYWRlciI6dHJ1ZSwiaWF0IjoxNjEyNjI3NzEyfQ.WM1kDXGXnslraxNSpN0CQnAn4lS0mtmniULrPryxVho
            @Json(name = "state")
            val state: String, // del
            @Json(name = "__v")
            val __v: Int, // 0
            @Json(name = "village")
            val village: String // greno
        ) {
            data class Location(
                @Json(name = "coordinates")
                val coordinates: List<Double>,
                @Json(name = "type")
                val type: String // Point
            )
        }

        data class Location(
            @Json(name = "coordinates")
            val coordinates: List<Double>,
            @Json(name = "type")
            val type: String // Point
        )
        data class BidDetails(
            @Json(name = "amount")
            val amount: Int, // 10000
            @Json(name = "_id")
            val _id: String, // 601d8a24253abdfd21015218
            @Json(name = "timestamp")
            val timestamp: String // 2021-02-05T18:10:44.693Z
        )
    }
}