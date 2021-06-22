package com.example.mandiexe.models

import androidx.annotation.Keep
import com.example.mandiexe.models.body.AddressBlock


 @Keep data class ProfileObject(
    val name: String,
    val area: String,
    val area_unit: String,
    val addressBlock: AddressBlock
)