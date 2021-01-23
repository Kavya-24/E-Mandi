package com.example.mandiexe.ui.search

import android.content.Context
import com.example.mandiexe.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.StandardCharsets


object MyDataHelper {


    private const val AC_FILE_NAME = "ac.json"
    private var sAutocompleteWrappers = mutableListOf<AutocompleteWrapper>()
    private val sAutocompleteSuggestion: MutableList<AutocompleteSuggestion> = mutableListOf()


    fun getHistory(context: Context, count: Int): MutableList<AutocompleteSuggestion> {

        fillHistoryList(context)
        val suggestionList = mutableListOf<AutocompleteSuggestion>()

        for (i in sAutocompleteSuggestion.indices) {

            val mObject = sAutocompleteSuggestion[i]
            suggestionList.add(AutocompleteSuggestion(mObject.mSuggestion, mObject.mIsHistory))
            if (suggestionList.size == count) {
                break
            }
        }
        return suggestionList
    }

    private fun fillHistoryList(context: Context) {

        val cropNames: Array<String> = context.resources.getStringArray(R.array.arr_crop_names)
        for (i in cropNames.indices) {
            sAutocompleteSuggestion.add(AutocompleteSuggestion(cropNames[i], false))
        }

    }

    fun resetSuggestionsHistory() {

        //Reset history
        for (i in sAutocompleteSuggestion) {
            i.mIsHistory = false
        }
    }

    private fun initAutocompleteWrapperList(context: Context) {
        if (sAutocompleteWrappers.isEmpty()) {
            val jsonString = loadJson(context)
            sAutocompleteWrappers =
                deserializeColors(jsonString) as MutableList<AutocompleteWrapper>
        }
    }

    private fun loadJson(context: Context): String? {
        val jsonString: String
        jsonString = try {
            val `is` = context.assets.open(AC_FILE_NAME)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return jsonString
    }

    private fun deserializeColors(jsonString: String?): MutableList<AutocompleteWrapper> {
        val gson = Gson()
        val collectionType = object : TypeToken<List<AutocompleteWrapper?>?>() {}.type
        return gson.fromJson(jsonString, collectionType)
    }

}