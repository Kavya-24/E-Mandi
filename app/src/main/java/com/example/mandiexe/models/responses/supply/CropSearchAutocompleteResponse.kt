package com.example.mandiexe.models.responses.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep data class CropSearchAutocompleteResponse(
    @field:Json(name = "suggestions")
    val suggestions: List<Suggestion>
) {

    @Keep data class Suggestion(
        @field:Json(name = "_id")
        val _id: String, // 5fe1f1cb4b80e08ad56c0080
        @field:Json(name = "name")
        val name: String // Rice
    )
}