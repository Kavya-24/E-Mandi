package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.bid.DeletBidBody
import com.example.mandiexe.models.body.bid.UpdateBidBody
import com.example.mandiexe.models.body.bid.ViewBidBody
import com.example.mandiexe.models.responses.bids.DeleteBidResponse
import com.example.mandiexe.models.responses.bids.UpdateBidResponse
import com.example.mandiexe.models.responses.bids.ViewBidResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import retrofit2.Call
import retrofit2.Response

class MyRequirementDetailsViewModel : ViewModel() {

    val TAG = MyRequirementDetailsViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val myBidService = RetrofitClient.makeCallsForBids(context)

    //For getting the details of the Bid stock
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
    private var mBid: MutableLiveData<ViewBidResponse> = MutableLiveData()

    fun getFunction(body: ViewBidBody): MutableLiveData<ViewBidResponse> {

        mBid = BidStockFunction(body)
        return mBid
    }

    fun BidStockFunction(body: ViewBidBody): MutableLiveData<ViewBidResponse> {

        myBidService.getFarmerViewParticularBid(

            mViewBidBody = body,
     )
            .enqueue(object : retrofit2.Callback<ViewBidResponse> {
                override fun onFailure(call: Call<ViewBidResponse>, t: Throwable) {
                    successfulBid.value = false
                    messageBid.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    Log.e(TAG, "For the farmer, throawable is ${t.message} ${t.cause}")
                }

                override fun onResponse(
                    call: Call<ViewBidResponse>,
                    response: Response<ViewBidResponse>
                ) {

                    Log.e(
                        TAG,
                        response.message() + response.body()?.msg + response.body().toString()
                    )
                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Bid retrieved successfully.") {
                            successfulBid.value = true
                            messageBid.value =
                                context.resources.getString(R.string.BidDeleted)
                            mBid.value = response.body()!!

                        } else {
                            successfulBid.value = false
                            messageBid.value = response.body()?.msg.toString()
                        }

                    } else {
                        successfulBid.value = false
                        messageBid.value = response.body()?.msg.toString()
                    }

                    mBid.value = response.body()
                }
            })


        return mBid


    }


    fun cancelFunction(body: DeletBidBody): MutableLiveData<DeleteBidResponse> {

        deleteBid = deleteBidFunction(body)
        return deleteBid
    }


    fun deleteBidFunction(body: DeletBidBody): MutableLiveData<DeleteBidResponse> {

        myBidService.getFarmerDeleteBid(
            mDeleteBidBody = body,
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

    fun updateFunction(body: UpdateBidBody): MutableLiveData<UpdateBidResponse> {

        modifyBid = updateStockFunction(body)
        return modifyBid
    }


    fun updateStockFunction(body: UpdateBidBody): MutableLiveData<UpdateBidResponse> {

        myBidService.getFarmerUpdateBid(
            mUpdateBidBody = body,
     )
            .enqueue(object : retrofit2.Callback<UpdateBidResponse> {
                override fun onFailure(call: Call<UpdateBidResponse>, t: Throwable) {
                    successfulUpdate.value = false
                    messageUpdate.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                }

                override fun onResponse(
                    call: Call<UpdateBidResponse>,
                    response: Response<UpdateBidResponse>
                ) {

                    Log.e(
                        TAG,
                        response.message() + response.body()?.msg + response.body().toString()
                    )
                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Bid updated successfully.") {
                            successfulUpdate.value = true
                            messageUpdate.value =
                                context.resources.getString(R.string.bidUpdated)

                        } else {
                            successfulUpdate.value = false
                            messageUpdate.value = response.body()?.msg.toString()
                        }
                        if(response.body() != null){
                            modifyBid.value = response.body()!!

                        }
                    } else {
                        successfulUpdate.value = false
                        messageUpdate.value = response.body()?.msg.toString()
                    }

                }
            })


        return modifyBid


    }




}
