package com.example.mandiexe.models.body

import androidx.annotation.Keep

@Keep
data class LanguageBody(
    val language: String,
    val mLocale: String
)