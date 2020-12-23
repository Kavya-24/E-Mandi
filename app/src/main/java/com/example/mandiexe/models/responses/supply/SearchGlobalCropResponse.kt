package com.example.mandiexe.models.responses.supply


import com.squareup.moshi.Json

data class SearchGlobalCropResponse(
    @Json(name = "country")
    val country: Country,
    @Json(name = "district")
    val district: District,
    @Json(name = "_id")
    val _id: String, // 5fe1f2984b80e08ad56c0081
    @Json(name = "links")
    val links: List<Link>,
    @Json(name = "name")
    val name: String, // Wheat
    @Json(name = "state")
    val state: State,
    @Json(name = "type")
    val type: String, // Grain
    @Json(name = "village")
    val village: Village
) {
    data class Country(
        @Json(name = "_id")
        val _id: String, // ind
        @Json(name = "total")
        val total: Int // 570
    )

    data class District(
        @Json(name = "_id")
        val _id: String, // del
        @Json(name = "total")
        val total: Int // 670
    )

    data class Link(
        @Json(name = "heading")
        val heading: String, // How to grow wheat?
        @Json(name = "url")
        val url: String // youtube.com
    )

    data class State(
        @Json(name = "_id")
        val _id: String, // del
        @Json(name = "total")
        val total: Int // 670
    )

    data class Village(
        @Json(name = "_id")
        val id: String, // greno
        @Json(name = "qty")
        val qty: Int // 670
    )
}