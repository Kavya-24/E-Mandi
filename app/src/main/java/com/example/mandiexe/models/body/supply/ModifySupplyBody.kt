package com.example.mandiexe.models.body.supply


import com.squareup.moshi.Json

data class ModifySupplyBody(
    @Json(name = "supply_id")
    val supply_id: String, // 5fdf9eda9318050bc1d3393f
    @Json(name = "update")
    val update: Update
) {
    data class Update(
        @Json(name = "askPrice")
        val askPrice: Int, // 12000
        @Json(name = "variety")
        val variety: String, // common
        @Json(name = "description")
        val description: String,
        @Json(name = "expiry")
        val expiry: String,
        @Json(name = "dateOfHarvest")
        val dateOfHarvest: String
    )
}