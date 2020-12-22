package com.example.mandiexe.models

import com.example.mandiexe.models.body.AddressBlock

data class ProfileObject(
    val name: String,
    val area: String,
    val area_unit: String,
    val addressBlock: AddressBlock
)