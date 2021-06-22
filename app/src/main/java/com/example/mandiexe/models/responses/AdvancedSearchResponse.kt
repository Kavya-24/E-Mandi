package com.example.mandiexe.models.responses


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class AdvancedSearchResponse(
    @field:Json(name = "_id")
    val _id: String, // 5fe1f2984b80e08ad56c0081
    @field:Json(name = "info")
    val info: Info,
    @field:Json(name = "links")
    val links: List<Link>,
    @field:Json(name = "name")
    val name: String, // Wheat
    @field:Json(name = "type")
    val type: String // Grain
) {
    @Keep
    data class Info(
        @field:Json(name = "_id")
        val _id: String, // 1
        @field:Json(name = "qty")
        val qty: Int // 670
    )

    @Keep
    data class Link(
        @field:Json(name = "heading")
        val heading: String, // How to grow wheat?
        @field:Json(name = "url")
        val url: String // youtube.com
    )
}