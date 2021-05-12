package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.responses.supply.SupplyHistoryResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import retrofit2.Call
import retrofit2.Response

class FarmerSupplyHistoryViewModel : ViewModel() {

    val TAG = FarmerSupplyHistoryViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val mySupplyService = RetrofitClient.makeCallsForSupplies(context)

    //For getting the details of the supply stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    var supplyHistory: MutableLiveData<SupplyHistoryResponse> = MutableLiveData()

    fun supplyFunction(): MutableLiveData<SupplyHistoryResponse> {

        supplyHistory = supplyStockFunction()
        return supplyHistory
    }


    fun supplyStockFunction(): MutableLiveData<SupplyHistoryResponse> {

        mySupplyService.getFarmerSupplyHistory(
     )
            .enqueue(object : retrofit2.Callback<SupplyHistoryResponse> {
                override fun onFailure(call: Call<SupplyHistoryResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    Log.e(
                        TAG,
                        "Failed in Supply Hsitory because " + t.message + t.cause + " Setting" + message.value
                    )
                }

                override fun onResponse(
                    call: Call<SupplyHistoryResponse>,
                    response: Response<SupplyHistoryResponse>
                ) {

                    Log.e(
                        TAG,
                        " In response " + response.message() + " " + " " + response.body()
                            .toString() + response.code() + " " + response.errorBody()
                    )

                    if (response.isSuccessful) {

                        successful.value = true
                        message.value =
                            context.resources.getString(R.string.supplyHistorySuccess)


                    } else {
                        successful.value = false
                    }


                    supplyHistory.value = response.body()

                }
            })


        return supplyHistory


    }


}