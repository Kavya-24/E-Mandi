package com.example.mandiexe.models.responses.demand


import androidx.annotation.Keep
import com.squareup.moshi.Json


 @Keep data class NewDemandsResponse(
    @field:Json(name = "demands")
    val demands: List<Demand>
) {
    @Keep data class Demand(
        @field:Json(name = "active")
        val active: Boolean, // true
        @field:Json(name = "bids")
        val bids: List<Any>,
        @field:Json(name = "change")
        val change: Int, // 0
        @field:Json(name = "crop")
        val crop: String, // Wheat
        @field:Json(name = "currentBid")
        val currentBid: Int, // 0
        @field:Json(name = "dateOfRequirement")
        val dateOfRequirement: String, // 2021-03-06T00:00:00.000Z
        @field:Json(name = "demandCreated")
        val demandCreated: String, // 2021-02-06T16:09:15.869Z
        @field:Json(name = "demander")
        val demander: List<Demander>,
        @field:Json(name = "description")
        val description: String, // NA
        @field:Json(name = "distance")
        val distance: Int, // 0
        @field:Json(name = "expiry")
        val expiry: String, // 2021-03-01T00:00:00.000Z
        @field:Json(name = "_id")
        val _id: String, // 601ebf2b5109c7a1f3f0f4ce
        @field:Json(name = "lastBid")
        val lastBid: List<Any>,
        @field:Json(name = "lastModified")
        val lastModified: String, // 2021-02-06T16:09:15.869Z
        @field:Json(name = "location")
        val location: Location,
        @field:Json(name = "offerPrice")
        val offerPrice: Int, // 2500
        @field:Json(name = "qty")
        val qty: Int, // 50
        @field:Json(name = "__v")
        val __v: Int, // 0
        @field:Json(name = "variety")
        val variety: String // White Wheat
    ) {
        @Keep data class Demander(
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
    }
}