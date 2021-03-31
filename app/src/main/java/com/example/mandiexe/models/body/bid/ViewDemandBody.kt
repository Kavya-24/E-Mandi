package com.example.mandiexe.models.body.bid

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ViewDemandBody (
    @field:Json(name = "demand_id")
    val demand_id : String
)