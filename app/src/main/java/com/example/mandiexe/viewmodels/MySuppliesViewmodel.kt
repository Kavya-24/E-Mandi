package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.responses.supply.FarmerSuppliesResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.auth.SessionManager
import retrofit2.Call
import retrofit2.Response

class MySuppliesViewmodel : ViewModel() {

    val TAG = MySuppliesViewmodel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val mySupplyService = RetrofitClient.makeCallsForSupplies(context)

    //For getting the details of the supply stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    private var mSupplies: MutableLiveData<FarmerSuppliesResponse> = MutableLiveData()

    fun supplyFunction(): MutableLiveData<FarmerSuppliesResponse> {

        mSupplies = mSuppliesFunction()
        return mSupplies
    }


    fun mSuppliesFunction(): MutableLiveData<FarmerSuppliesResponse> {


        mySupplyService.getFarmerActiveSupplies(
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}"
        )
            .enqueue(object : retrofit2.Callback<FarmerSuppliesResponse> {
                override fun onFailure(call: Call<FarmerSuppliesResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    Log.e(TAG, "Throwable " + t.message + t.cause)
                }

                override fun onResponse(
                    call: Call<FarmerSuppliesResponse>,
                    response: Response<FarmerSuppliesResponse>
                ) {


                    if (response.isSuccessful) {

                        successful.value = true
                        message.value =
                            context.resources.getString(R.string.suppliesLoaded)
                        mSupplies.value = response.body()!!


                    } else {

                        successful.value = false
                        message.value = context.resources.getString(R.string.couldNotLoad)
                    }

                    mSupplies.value = response.body()

                }
            })

        Log.e(TAG, mSupplies.value.toString())

        return mSupplies


    }

}
