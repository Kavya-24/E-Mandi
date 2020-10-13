package com.example.mandiexe.models.response

import com.example.mandiexe.models.MarketLocation

data class PriceResponse(
    val market: MarketLocation,
    val crop: String,
    val price: String,
    val timestamp: String
)
