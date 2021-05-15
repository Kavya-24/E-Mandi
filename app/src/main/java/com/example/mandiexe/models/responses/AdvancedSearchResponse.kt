package com.example.mandiexe.models.responses


import com.squareup.moshi.Json

data class AdvancedSearchResponse(
    @Json(name = "_id")
    val _id: String, // 5fe1f2984b80e08ad56c0081
    @Json(name = "info")
    val info: Info,
    @Json(name = "links")
    val links: List<Link>,
    @Json(name = "name")
    val name: String, // Wheat
    @Json(name = "type")
    val type: String // Grain
) {
    data class Info(
        @Json(name = "_id")
        val _id: String, // 1
        @Json(name = "qty")
        val qty: Int // 670
    )

    data class Link(
        @Json(name = "heading")
        val heading: String, // How to grow wheat?
        @Json(name = "url")
        val url: String // youtube.com
    )
}