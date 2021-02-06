package com.example.mandiexe.models.responses


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class SearchCropReqResponse(
    @Json(name = "demands")
    val demands: List<Demand>
) {
    data class Demand(
        @Json(name = "active")
        val active: Boolean, // true
        @Json(name = "bids")
        val bids: List<Any>,
        @Json(name = "change")
        val change: Int, // 0
        @Json(name = "crop")
        val crop: String, // Wheat
        @Json(name = "currentBid")
        val currentBid: Int, // 0
        @Json(name = "dateOfRequirement")
        val dateOfRequirement: String, // 2021-03-06T00:00:00.000Z
        @Json(name = "demandCreated")
        val demandCreated: String, // 2021-02-06T16:09:15.869Z
        @Json(name = "demander")
        val demander: List<Demander>,
        @Json(name = "description")
        val description: String, // NA
        @Json(name = "distance")
        val distance: Int, // 0
        @Json(name = "expiry")
        val expiry: String, // 2021-03-01T00:00:00.000Z
        @Json(name = "_id")
        val _id: String, // 601ebf2b5109c7a1f3f0f4ce
        @Json(name = "lastBid")
        val lastBid: List<Any>,
        @Json(name = "lastModified")
        val lastModified: String, // 2021-02-06T16:09:15.869Z
        @Json(name = "location")
        val location: Location,
        @Json(name = "offerPrice")
        val offerPrice: Int, // 2500
        @Json(name = "qty")
        val qty: Int, // 50
        @Json(name = "__v")
        val __v: Int, // 0
        @Json(name = "variety")
        val variety: String // White Wheat
    ) {
        data class Demander(
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
    }
}