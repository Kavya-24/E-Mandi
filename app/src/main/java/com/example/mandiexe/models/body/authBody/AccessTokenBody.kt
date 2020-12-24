package com.example.mandiexe.models.body.authBody


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class AccessTokenBody(
    @field:Json(name = "refreshToken")
    val refreshToken: String // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmZGY2Zjc3MGNkNmNmMGE2MDE3ZjY5YiIsIm5hbWUiOiJrYXZ5YSB2YXRzYWwiLCJwaG9uZSI6Iis5MTg1ODU5OTIwNjIiLCJpc0Zhcm1lciI6dHJ1ZSwiaWF0IjoxNjA4NDc5MjkxfQ.hKC5-DH4AebneH-jc2ue95Bd_95bLspQR1tQF0pWKOw
)