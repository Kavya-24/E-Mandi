package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class SearchGlobalCropResponse(

    @field:Json(name = "country")
    val country: Country,

    @field:Json(name = "district")
    val district: District,

    @field:Json(name = "_id")
    val _id: String, // 5fe1f2984b80e08ad56c0081

    @field:Json(name = "links")
    val links: List<Link>,

    @field:Json(name = "name")
    val name: String, // Wheat

    @field:Json(name = "state")
    val state: State,

    @field:Json(name = "type")
    val type: String, // Grain

    @field:Json(name = "village")
    val village: Village

) {

    @Keep data class Country(
        @field:Json(name = "_id")
        val _id: String, // ind
        @field:Json(name = "total")
        val total: Int // 570
    )


    @Keep data class District(
        @field:Json(name = "_id")
        val _id: String, // del
        @field:Json(name = "total")
        val total: Int // 670
    )


    @Keep data class Link(
        @field:Json(name = "heading")
        val heading: String, // How to grow wheat?
        @field:Json(name = "url")
        val url: String // youtube.com
    )


    @Keep data class State(
        @field:Json(name = "_id")
        val _id: String, // del
        @field:Json(name = "total")
        val total: Int // 670
    )


    @Keep data class Village(
        @field:Json(name = "_id")
        val id: String, // greno
        @field:Json(name = "qty")
        val qty: Int // 670
    )
}