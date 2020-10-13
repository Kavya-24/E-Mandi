package com.example.mandiexe.models

import android.location.Address

data class MarketLocation(
    val address: Address,
    val market_id: String,
    val market_name: String
)
