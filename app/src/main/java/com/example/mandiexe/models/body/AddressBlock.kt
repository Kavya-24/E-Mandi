package com.example.mandiexe.models.body

import androidx.annotation.Keep

@Keep
data class AddressBlock(
    val district: String,
    val village: String,
    val state: String,
    val country: String,
    val address: String,
    val latitude: String,
    val longitude: String
)