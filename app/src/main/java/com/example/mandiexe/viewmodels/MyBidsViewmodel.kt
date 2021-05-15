package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.responses.bids.FamerBidsResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.UIUtils
import retrofit2.Call
import retrofit2.Response

class MyBidsViewmodel : ViewModel() {

    val TAG = MyBidsViewmodel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val mySupplyService = RetrofitClient.makeCallsForBids(context)

    //For getting the details of the supply stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    private var mReq: MutableLiveData<FamerBidsResponse> = MutableLiveData()

    fun reqFunction(): MutableLiveData<FamerBidsResponse> {

        mReq = mReqFunction()
        return mReq
    }


    private fun mReqFunction(): MutableLiveData<FamerBidsResponse> {

        mySupplyService.getFarmerDemands(
        )
            .enqueue(object : retrofit2.Callback<FamerBidsResponse> {
                override fun onFailure(call: Call<FamerBidsResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    UIUtils.logThrowables(t, TAG)
                }

                override fun onResponse(
                    call: Call<FamerBidsResponse>,
                    response: Response<FamerBidsResponse>
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
                        message.value = context.resources.getString(R.string.couldNotLoadBids)

                    }

                }
            })

        return mReq


    }

}
