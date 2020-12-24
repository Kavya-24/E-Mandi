package com.example.mandiexe.models.responses.auth


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class FarmerProfileResponse(
    @field:Json(name = "profile")
    val profile: Profile
) {
    @Keep
    data class Profile(
        @field:Json(name = "accountCreated")
        val accountCreated: String, // 2020-12-20T19:57:10.845Z
        @field:Json(name = "address")
        val address: String, // b381, asia
        @field:Json(name = "area")
        val area: Area,
        @field:Json(name = "country")
        val country: String, // ind
        @field:Json(name = "district")
        val district: String, // del
        @field:Json(name = "fuid")
        val fuid: String, // S2ustzAO4FeFSgCANfZtNRl7iZ42
        @field:Json(name = "_id")
        val _id: String, // 5fdfac963f52f60c2356dcd5
        @field:Json(name = "location")
        val location: Location,
        @field:Json(name = "name")
        val name: String, // kavya vatsal
        @field:Json(name = "phone")
        val phone: String, // +918585992062
        @field:Json(name = "refreshToken")
        val refreshToken: String, // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGZhYzk2M2Y1MmY2MGMyMzU2ZGNkNSIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NDk0MjU1fQ.meAu7wPNDGN-iEJ_aLtq2fvrRi83PwNWAoIeCHzXVTA
        @field:Json(name = "state")
        val state: String, // del
        @field:Json(name = "__v")
        val v: Int, // 0
        @field:Json(name = "village")
        val village: String // greno
    ) {
        @Keep
        data class Area(
            @field:Json(name = "numerical")
            val numerical: Int, // 100
            @field:Json(name = "unit")
            val unit: String // acre
        )

        @Keep
        data class Location(
            @field:Json(name = "coordinates")
            val coordinates: List<Double>,
            @field:Json(name = "type")
            val type: String // Point
        )
    }
}