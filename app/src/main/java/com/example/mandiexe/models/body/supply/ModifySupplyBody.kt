package com.example.mandiexe.models.body.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ModifySupplyBody(
    @field:Json(name = "supply_id")
    val supply_id: String, // 5fdf9eda9318050bc1d3393f
    @field:Json(name = "update")
    val update: Update
) {
    @Keep
    data class Update(
        @field:Json(name = "askPrice")
        val askPrice: Int, // 12000
        @field:Json(name = "variety")
        val variety: String, // common
        @field:Json(name = "description")
        val description: String,
        @field:Json(name = "expiry")
        val expiry: String,
        @field:Json(name = "dateOfHarvest")
        val dateOfHarvest: String
    )
}