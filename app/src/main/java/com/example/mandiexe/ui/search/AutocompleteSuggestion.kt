package com.example.mandiexe.ui.search

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion

data class AutocompleteSuggestion(
    var mSuggestion: String?,
    var mIsHistory: Boolean

) : SearchSuggestion {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // super.writeToParcel(parcel, flags)
        parcel.writeString(mSuggestion)
        parcel.writeByte(if (mIsHistory) 1 else 0)
    }

    override fun getBody(): String {
        return mSuggestion!!
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AutocompleteSuggestion> {
        override fun createFromParcel(parcel: Parcel): AutocompleteSuggestion {
            return AutocompleteSuggestion(parcel)
        }

        override fun newArray(size: Int): Array<AutocompleteSuggestion?> {
            return arrayOfNulls(size)
        }
    }

}