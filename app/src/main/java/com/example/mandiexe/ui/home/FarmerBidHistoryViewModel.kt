package com.example.mandiexe.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.responses.bids.BidHistoryResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.auth.SessionManager
import retrofit2.Call
import retrofit2.Response

class FarmerBidHistoryViewModel : ViewModel() {


    val TAG = FarmerBidHistoryViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val myBidService = RetrofitClient.makeCallsForBids(context)

    //For getting the details of the Bid stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    var BidHistory: MutableLiveData<BidHistoryResponse> = MutableLiveData()

    fun BidFunction(): MutableLiveData<BidHistoryResponse> {

        BidHistory = BidStockFunction()
        return BidHistory
    }


    fun BidStockFunction(): MutableLiveData<BidHistoryResponse> {

        myBidService.getFarmerBidHistoryGlobal(
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}",
        )
            .enqueue(object : retrofit2.Callback<BidHistoryResponse> {
                override fun onFailure(call: Call<BidHistoryResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                }

                override fun onResponse(
                    call: Call<BidHistoryResponse>,
                    response: Response<BidHistoryResponse>
                ) {

                    Log.e(
                        TAG,
                        " In response " + response.message() + " " + " " + response.body()
                            .toString() + response.code() + " " + response.errorBody()
                    )

                    if (response.isSuccessful) {

                        successful.value = true
                        message.value =
                            context.resources.getString(R.string.bidHistorySucess)


                    } else {
                        successful.value = false
                    }


                    BidHistory.value = response.body()

                }
            })


        return BidHistory


    }

}
