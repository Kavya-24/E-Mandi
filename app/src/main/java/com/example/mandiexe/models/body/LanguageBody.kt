package com.example.mandiexe.models.body

import android.graphics.drawable.Drawable
import androidx.annotation.Keep


@Keep
data class LanguageBody(
    val language: String,
    val mLocale: String,
    val mLetter: String

)