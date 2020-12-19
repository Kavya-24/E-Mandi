package com.example.mandiexe.models.response

import com.example.mandiexe.models.ContactOwner
import com.example.mandiexe.models.LocationObject
import java.util.*

data class StockResponse(
    val crop: String,
    val quantity: String,
    val location: LocationObject,
    val price: String,
    val readyDate: Date,
    val postedDate: Date,
    val contactOwner: ContactOwner
)