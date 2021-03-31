package com.example.mandiexe.models.responses.demand


import androidx.annotation.Keep
import com.squareup.moshi.Json

//This is the requirement

@Keep data class ViewDemandResponse(
    @field:Json(name = "demand")
    val demand: Demand,
    @field:Json(name = "msg")
    val msg: String // Demand retrieved successfully.
) {
    @Keep
    data class Demand(
        @field:Json(name = "active")
        val active: Boolean, // true
        @field:Json(name = "bids")
        val bids: List<BidDetails>,
        @field:Json(name = "change")
        val change: Int, // 0
        @field:Json(name = "crop")
        val crop: String, // Rice
        @field:Json(name = "currentBid")
        val currentBid: Int, // 0
        @field:Json(name = "dateOfRequirement")
        val dateOfRequirement: String, // 2021-03-06T00:00:00.000Z
        @field:Json(name = "demandCreated")
        val demandCreated: String, // 2021-02-06T16:27:01.646Z
        @field:Json(name = "demander")
        val demander: Demander,
        @field:Json(name = "description")
        val description: String, // Test
        @field:Json(name = "expiry")
        val expiry: String, // 2021-03-02T00:00:00.000Z
        @field:Json(name = "_id")
        val _id: String, // 601ec3555109c7a1f3f0f4d2
        @field:Json(name = "lastBid")
        val lastBid: List<BidDetails>,
        @field:Json(name = "lastModified")
        val lastModified: String, // 2021-02-06T16:27:01.646Z
        @field:Json(name = "location")
        val location: Location,
        @field:Json(name = "offerPrice")
        val offerPrice: Int, // 2000
        @field:Json(name = "qty")
        val qty: Int, // 50
        @field:Json(name = "__v")
        val __v: Int, // 0
        @field:Json(name = "variety")
        val variety: String // Test
    ) {
        @Keep data class Demander(
            @field:Json(name = "accountCreated")
            val accountCreated: String, // 2020-12-28T20:22:00.537Z
            @field:Json(name = "address")
            val address: String, // b381, asia
            @field:Json(name = "country")
            val country: String, // ind
            @field:Json(name = "district")
            val district: String, // del
            @field:Json(name = "fuid")
            val fuid: String, // dt38CMkohngtNrkysK1Sr73RxLg1
            @field:Json(name = "_id")
            val _id: String, // 5fea3e68b7f8bf2537b194ec
            @field:Json(name = "location")
            val location: Location,
            @field:Json(name = "name")
            val name: String, // kavya vatsal
            @field:Json(name = "phone")
            val phone: String, // +919610306949
            @field:Json(name = "refreshToken")
            val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZWEzZTY4YjdmOGJmMjUzN2IxOTRlYyIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTk2MTAzMDY5NDkiLCJsb2NhdGlvbiI6eyJ0eXBlIjoiUG9pbnQiLCJjb29yZGluYXRlcyI6WzkuNDM4OTMzLDIuOTQwNDM1M119LCJpc1RyYWRlciI6dHJ1ZSwiaWF0IjoxNjEyNjI3NzEyfQ.WM1kDXGXnslraxNSpN0CQnAn4lS0mtmniULrPryxVho
            @field:Json(name = "state")
            val state: String, // del
            @field:Json(name = "__v")
            val __v: Int, // 0
            @field:Json(name = "village")
            val village: String // greno
        ) {
            @Keep data class Location(
                @field:Json(name = "coordinates")
                val coordinates: List<Double>,
                @field:Json(name = "type")
                val type: String // Point
            )
        }

        @Keep data class Location(
            @field:Json(name = "coordinates")
            val coordinates: List<Double>,
            @field:Json(name = "type")
            val type: String // Point
        )
        @Keep data class BidDetails(
            @field:Json(name = "amount")
            val amount: Int, // 10000
            @field:Json(name = "_id")
            val _id: String, // 601d8a24253abdfd21015218
            @field:Json(name = "timestamp")
            val timestamp: String // 2021-02-05T18:10:44.693Z
        )
    }
}