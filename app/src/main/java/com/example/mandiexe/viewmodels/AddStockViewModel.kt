package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.supply.AddGrowthBody
import com.example.mandiexe.models.body.supply.AddSupplyBody
import com.example.mandiexe.models.responses.supply.AddGrowthResponse
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import retrofit2.Call
import retrofit2.Response

class AddStockViewModel : ViewModel() {

    val TAG = AddStockViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val mySupplyService = RetrofitClient.makeCallsForSupplies(context)

    //For getting the details of the supply stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    val successfulGrowth: MutableLiveData<Boolean> = MutableLiveData()
    var messageGrowth: MutableLiveData<String> = MutableLiveData()

    var addStock: MutableLiveData<AddSupplyResponse> = MutableLiveData()
    var growthStock: MutableLiveData<AddGrowthResponse> = MutableLiveData()

    fun addFunction(body: AddSupplyBody): MutableLiveData<AddSupplyResponse> {

        addStock = addStockFunction(body)
        return addStock
    }


    fun addStockFunction(body: AddSupplyBody): MutableLiveData<AddSupplyResponse> {

        Log.e(TAG, "In add stpck")

        mySupplyService.getAddSupply(
            mAddSupply = body,
     )
            .enqueue(object : retrofit2.Callback<AddSupplyResponse> {
                override fun onFailure(call: Call<AddSupplyResponse>, t: Throwable) {
                    successfulGrowth.value = false
                    messageGrowth.value = ExternalUtils.returnStateMessageForThrowable(t)
                    Log.e(TAG, "Throwable  Supply" + t.message + t.cause)

                    //Response is null
                }

                override fun onResponse(
                    call: Call<AddSupplyResponse>,
                    response: Response<AddSupplyResponse>
                ) {

                    Log.e(
                        TAG,
                        " In response " + response.message() + " " + response.body()?.msg + " " + response.body()
                            .toString() + response.code() + " " + response.errorBody()
                    )

                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Crop growth added successfully.") {
                            successfulGrowth.value = true
                            messageGrowth.value =
                                context.resources.getString(R.string.supplyAdded)

                        } else {
                            successfulGrowth.value = false
                            messageGrowth.value = response.body()?.msg.toString()
                        }

                        addStock.value = response.body()!!

                    } else {
                        successfulGrowth.value = false
                        messageGrowth.value = response.body()?.msg.toString()
                    }
                    addStock.value = response.body()!!

                }
            })


        return addStock


    }


    fun growthFunction(body: AddGrowthBody): MutableLiveData<AddGrowthResponse> {

        growthStock = growthStockFunction(body)
        return growthStock
    }


    fun growthStockFunction(body: AddGrowthBody): MutableLiveData<AddGrowthResponse> {

        Log.e(TAG, "In add stpck")

        mySupplyService.getFarmerGrowthAdd(
            body = body,
     )
            .enqueue(object : retrofit2.Callback<AddGrowthResponse> {
                override fun onFailure(call: Call<AddGrowthResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    Log.e(TAG, "Throwable " + t.message + t.cause)
                    //Response is null
                }

                override fun onResponse(
                    call: Call<AddGrowthResponse>,
                    response: Response<AddGrowthResponse>
                ) {

                    Log.e(
                        TAG,
                        " In response " + response.message() + " " + response.body()?.msg + " " + response.body()
                            .toString() + response.code() + " " + response.errorBody()
                    )

                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Growth added successfully.") {
                            successful.value = true
                            message.value =
                                context.resources.getString(R.string.GrowthAdded)

                        } else {
                            successful.value = false
                            message.value = response.body()?.msg.toString()
                        }

                        growthStock.value = response.body()!!

                    } else {
                        successful.value = false
                        message.value = response.body()?.msg.toString()
                    }

                    growthStock.value = response.body()!!

                }
            })


        return growthStock


    }


}
