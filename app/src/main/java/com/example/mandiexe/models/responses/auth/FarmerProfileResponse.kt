package com.example.mandiexe.models.responses.auth


import com.squareup.moshi.Json

data class FarmerProfileResponse(
    @Json(name = "profile")
    val profile: Profile
) {
    data class Profile(
        @Json(name = "accountCreated")
        val accountCreated: String, // 2020-12-20T19:57:10.845Z
        @Json(name = "address")
        val address: String, // b381, asia
        @Json(name = "area")
        val area: Area,
        @Json(name = "country")
        val country: String, // ind
        @Json(name = "district")
        val district: String, // del
        @Json(name = "fuid")
        val fuid: String, // S2ustzAO4FeFSgCANfZtNRl7iZ42
        @Json(name = "_id")
        val id: String, // 5fdfac963f52f60c2356dcd5
        @Json(name = "location")
        val location: Location,
        @Json(name = "name")
        val name: String, // kavya vatsal
        @Json(name = "phone")
        val phone: String, // +918585992062
        @Json(name = "refreshToken")
        val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGZhYzk2M2Y1MmY2MGMyMzU2ZGNkNSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NDk0MjU1fQ.meAu7wPNDGN-iEJ_aLtq2fvrRi83PwNWAoIeCHzXVTA
        @Json(name = "state")
        val state: String, // del
        @Json(name = "__v")
        val v: Int, // 0
        @Json(name = "village")
        val village: String // greno
    ) {
        data class Area(
            @Json(name = "numerical")
            val numerical: Int, // 100
            @Json(name = "unit")
            val unit: String // acre
        )

        data class Location(
            @Json(name = "coordinates")
            val coordinates: List<Double>,
            @Json(name = "type")
            val type: String // Point
        )
    }
}