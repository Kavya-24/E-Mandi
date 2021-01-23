package com.example.mandiexe.ui.search

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AutocompleteWrapper private constructor(`in`: Parcel) : Parcelable {

    @SerializedName("mSuggestion")
    @Expose
    var mSuggestion: String? = `in`.readString()

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(mSuggestion)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<AutocompleteWrapper> {
        override fun createFromParcel(parcel: Parcel): AutocompleteWrapper {
            return AutocompleteWrapper(parcel)
        }

        override fun newArray(size: Int): Array<AutocompleteWrapper?> {
            return arrayOfNulls(size)
        }
    }

}