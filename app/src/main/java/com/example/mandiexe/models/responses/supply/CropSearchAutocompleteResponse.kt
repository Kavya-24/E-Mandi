package com.example.mandiexe.models.responses.supply


import com.squareup.moshi.Json

data class CropSearchAutocompleteResponse(
    @Json(name = "suggestions")
    val suggestions: List<Suggestion>
) {
    data class Suggestion(
        @Json(name = "_id")
        val _id: String, // 5fe1f1cb4b80e08ad56c0080
        @Json(name = "name")
        val name: String // Rice
    )
}