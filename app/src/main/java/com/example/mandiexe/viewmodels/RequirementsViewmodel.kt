package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.responses.bids.FarmerBidsResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.auth.SessionManager
import retrofit2.Call
import retrofit2.Response

class RequirementsViewmodel : ViewModel() {
    val TAG = RequirementsViewmodel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val mySupplyService = RetrofitClient.makeCallsForBids(context)

    //For getting the details of the supply stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    private var mReq: MutableLiveData<FarmerBidsResponse> = MutableLiveData()

    fun reqFunction(): MutableLiveData<FarmerBidsResponse> {

        mReq = mReqFunction()
        return mReq
    }


    fun mReqFunction(): MutableLiveData<FarmerBidsResponse> {

        mySupplyService.getFarmerDemands(
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}",
        )
            .enqueue(object : retrofit2.Callback<FarmerBidsResponse> {
                override fun onFailure(call: Call<FarmerBidsResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                }

                override fun onResponse(
                    call: Call<FarmerBidsResponse>,
                    response: Response<FarmerBidsResponse>
                ) {

                    Log.e(
                        TAG,
                        response.message() + response.body().toString()
                    )
                    if (response.isSuccessful) {

                        successful.value = true
                        message.value =
                            context.resources.getString(R.string.demanndLoaded)
                        mReq.value = response.body()!!


                    } else {

                        successful.value = false
                        message.value = context.resources.getString(R.string.couldNotLoad)
                    }

                }
            })


        return mReq


    }

}
