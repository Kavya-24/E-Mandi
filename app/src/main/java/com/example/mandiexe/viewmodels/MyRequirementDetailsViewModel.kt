package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.bid.DeletBidBody
import com.example.mandiexe.models.responses.bids.DeleteBidResponse
import com.example.mandiexe.models.responses.bids.UpdateBidResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.auth.SessionManager
import retrofit2.Call
import retrofit2.Response

class MyRequirementDetailsViewModel : ViewModel() {

    val TAG = MyRequirementDetailsViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val mySupplyService = RetrofitClient.makeCallsForBids(context)

    //For getting the details of the supply stock
    val successfulBid: MutableLiveData<Boolean> = MutableLiveData()
    var messageBid: MutableLiveData<String> = MutableLiveData()


    //For cancelling the stock
    val successfulCancel: MutableLiveData<Boolean> = MutableLiveData()
    var messageCancel: MutableLiveData<String> = MutableLiveData()


    //For updating stock details
    val successfulUpdate: MutableLiveData<Boolean> = MutableLiveData()
    var messageUpdate: MutableLiveData<String> = MutableLiveData()


    private var deleteBid: MutableLiveData<DeleteBidResponse> = MutableLiveData()
    private var modifyBid: MutableLiveData<UpdateBidResponse> = MutableLiveData()


    fun cancelFunction(body: DeletBidBody): MutableLiveData<DeleteBidResponse> {

        deleteBid = deleteBidFunction(body)
        return deleteBid
    }


    fun deleteBidFunction(body: DeletBidBody): MutableLiveData<DeleteBidResponse> {

        mySupplyService.getFarmerDeleteBid(
            mDeleteBidBody = body,
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}",
        )
            .enqueue(object : retrofit2.Callback<DeleteBidResponse> {
                override fun onFailure(call: Call<DeleteBidResponse>, t: Throwable) {
                    successfulCancel.value = false
                    messageCancel.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                }

                override fun onResponse(
                    call: Call<DeleteBidResponse>,
                    response: Response<DeleteBidResponse>
                ) {

                    Log.e(
                        TAG,
                        response.message() + response.body()?.msg + response.body().toString()
                    )
                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Bid deleted successfully.") {
                            successfulCancel.value = true
                            messageCancel.value =
                                context.resources.getString(R.string.BidDeleted)
                            deleteBid.value = response.body()!!

                        } else {
                            successfulCancel.value = false
                            messageCancel.value = response.body()?.msg.toString()
                        }

                    } else {
                        successfulCancel.value = false
                        messageCancel.value = response.body()?.msg.toString()
                    }

                }
            })


        return deleteBid


    }


}
