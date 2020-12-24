package com.example.mandiexe.models.body.supply


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ViewSupplyBody(
    @field:Json(name = "supply_id")
    val supply_id: String // 5fdf9eda9318050bc1d3393f
)