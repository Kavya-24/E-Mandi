package com.example.mandiexe.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.supply.CropSearchAutoCompleteBody
import com.example.mandiexe.models.responses.supply.CropSearchAutocompleteResponse
import com.example.mandiexe.models.responses.supply.SearchGlobalCropResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.SessionManager

class MainActivtyViewmodel : ViewModel() {

    val TAG = MainActivtyViewmodel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val mySupplyService = RetrofitClient.makeCallsForSupplies(context)

    //For getting the details of the supply stock
    val successfulSuggestion: MutableLiveData<Boolean> = MutableLiveData()
    var messageSuggestion: MutableLiveData<String> = MutableLiveData()

    val successfulSearch: MutableLiveData<Boolean> = MutableLiveData()
    var messageGSearcg: MutableLiveData<String> = MutableLiveData()

    var mSuggestion: MutableLiveData<CropSearchAutocompleteResponse> = MutableLiveData()
    var mSearch: MutableLiveData<SearchGlobalCropResponse> = MutableLiveData()


    fun suggestionFucntion(body: CropSearchAutoCompleteBody): MutableLiveData<CropSearchAutocompleteResponse> {
        mSuggestion = getSuggestions(body)
        return mSuggestion
    }

    private fun getSuggestions(body: CropSearchAutoCompleteBody): MutableLiveData<CropSearchAutocompleteResponse> {
        return mSuggestion
    }


}