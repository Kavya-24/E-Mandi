package com.example.mandiexe.models

import android.graphics.drawable.Drawable
import androidx.annotation.Keep

@Keep
data class DemandSuggestionObject(
    val nameOfCrop: String,
    val drawable: Drawable?
)