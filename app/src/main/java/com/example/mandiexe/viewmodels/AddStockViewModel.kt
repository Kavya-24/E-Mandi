package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.supply.AddSupplyBody
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.auth.SessionManager
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

    var addStock: MutableLiveData<AddSupplyResponse> = MutableLiveData()

    fun addFunction(body: AddSupplyBody): MutableLiveData<AddSupplyResponse> {

        addStock = addStockFunction(body)
        return addStock
    }


    fun addStockFunction(body: AddSupplyBody): MutableLiveData<AddSupplyResponse> {

        mySupplyService.getAddSupply(
            mAddSupply = body,
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}",
        )
            .enqueue(object : retrofit2.Callback<AddSupplyResponse> {
                override fun onFailure(call: Call<AddSupplyResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                }

                override fun onResponse(
                    call: Call<AddSupplyResponse>,
                    response: Response<AddSupplyResponse>
                ) {

                    Log.e(
                        TAG,
                        " In response " + response.message() + response.body()?.msg + response.body()
                            .toString() + response.code()
                    )
                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Supply added successfully.") {
                            successful.value = true
                            message.value =
                                context.resources.getString(R.string.supplyAdded)

                        } else {
                            successful.value = false
                            message.value = response.body()?.msg.toString()
                        }

                        addStock.value = response.body()!!

                    } else {
                        successful.value = false
                        message.value = response.body()?.msg.toString()
                    }

                }
            })


        return addStock


    }

}
