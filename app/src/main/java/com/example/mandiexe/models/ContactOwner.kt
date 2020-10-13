package com.example.mandiexe.models

data class ContactOwner(
    val id: String,
    val name: String,
    val ownerLocation: LocationOrigin,
    val number: String,
    val email: String
)
