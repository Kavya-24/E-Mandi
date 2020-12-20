package com.example.mandiexe.models.response

data class ProfileResponse(
    val profile: Profile
) {
    data class Profile(
        val name: String,

        )
}
