package com.example.mandiexe.models.body.bid

import com.squareup.moshi.Json

data class ViewDemandBody (
    @field:Json(name = "demand_id")
    val demand_id : String
)