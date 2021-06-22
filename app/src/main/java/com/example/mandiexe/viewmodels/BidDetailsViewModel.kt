package com.example.mandiexe.viewmodels

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.Keep
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.bid.*
import com.example.mandiexe.models.responses.bids.AddBidResponse
import com.example.mandiexe.models.responses.bids.DeleteBidResponse
import com.example.mandiexe.models.responses.bids.UpdateBidResponse
import com.example.mandiexe.models.responses.bids.ViewBidResponse
import com.example.mandiexe.models.responses.demand.ViewDemandResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.UIUtils
import retrofit2.Call
import retrofit2.Response

@Keep

class BidDetailsViewModel : ViewModel() {

    val TAG = BidDetailsViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val myBidService = RetrofitClient.makeCallsForBids(context)
    private val myDemandService = RetrofitClient.makeCallsForDemands(context)

    //For getting the details of the Bid stock
    val successfulBid: MutableLiveData<Boolean> = MutableLiveData()
    var messageBid: MutableLiveData<String> = MutableLiveData()

    //For viewing the demand
    val successfulDemand: MutableLiveData<Boolean> = MutableLiveData()
    var messageDemand: MutableLiveData<String> = MutableLiveData()


    //For cancelling the stock
    val successfulCancel: MutableLiveData<Boolean> = MutableLiveData()
    var messageCancel: MutableLiveData<String> = MutableLiveData()


    //For updating stock details
    val successfulUpdate: MutableLiveData<Boolean> = MutableLiveData()
    var messageUpdate: MutableLiveData<String> = MutableLiveData()


    //For updating stock details
    val successfulAdd: MutableLiveData<Boolean> = MutableLiveData()
    var messageAdd: MutableLiveData<String> = MutableLiveData()

    private var deleteBid: MutableLiveData<DeleteBidResponse> = MutableLiveData()
    private var modifyBid: MutableLiveData<UpdateBidResponse> = MutableLiveData()
    private var mBid: MutableLiveData<ViewBidResponse> = MutableLiveData()
    private var addBid: MutableLiveData<AddBidResponse> = MutableLiveData()
    private var demandStock: MutableLiveData<ViewDemandResponse> = MutableLiveData()


    //For viewing the bid
    fun viewBidFunction(
        body: ViewBidBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<ViewBidResponse> {
        mBid = bidFunction(body, pb, mSnackbarView)
        return mBid
    }

    private fun bidFunction(
        body: ViewBidBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<ViewBidResponse> {

        myBidService.getFarmerViewParticularBid(

            mViewBidBody = body,
        )
            .enqueue(object : retrofit2.Callback<ViewBidResponse> {
                override fun onFailure(call: Call<ViewBidResponse>, t: Throwable) {
                    successfulBid.value = false
                    messageBid.value = ExternalUtils.returnStateMessageForThrowable(t)
                    UIUtils.logThrowables(t, TAG)
                    //Response is null
                    pb.visibility = View.GONE
                    UIUtils.createSnackbar(messageBid.value, context, mSnackbarView)

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


    fun deleteFunction(
        body: DeletBidBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<DeleteBidResponse> {

        deleteBid = deleteBidFunction(body, pb, mSnackbarView)
        return deleteBid
    }

    private fun deleteBidFunction(
        body: DeletBidBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<DeleteBidResponse> {

        myBidService.getFarmerDeleteBid(
            mDeleteBidBody = body,
        )
            .enqueue(object : retrofit2.Callback<DeleteBidResponse> {
                override fun onFailure(call: Call<DeleteBidResponse>, t: Throwable) {
                    successfulCancel.value = false
                    messageCancel.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    pb.visibility = View.GONE
                    UIUtils.createSnackbar(messageCancel.value, context, mSnackbarView)

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


    fun updateFunction(
        body: UpdateBidBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<UpdateBidResponse> {

        modifyBid = updateBidFunction(body, pb, mSnackbarView)
        return modifyBid
    }

    private fun updateBidFunction(
        body: UpdateBidBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<UpdateBidResponse> {

        myBidService.getFarmerUpdateBid(
            mUpdateBidBody = body,
        )
            .enqueue(object : retrofit2.Callback<UpdateBidResponse> {
                override fun onFailure(call: Call<UpdateBidResponse>, t: Throwable) {
                    successfulUpdate.value = false
                    messageUpdate.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    pb.visibility = View.GONE
                    UIUtils.createSnackbar(messageUpdate.value, context, mSnackbarView)


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
                        if (response.body() != null) {
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


    fun addFunction(
        body: AddBidBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<AddBidResponse> {

        addBid = addBidFunction(body, pb, mSnackbarView)
        return addBid
    }

    private fun addBidFunction(
        body: AddBidBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<AddBidResponse> {

        myBidService.getFarmerAddBid(
            mAddBidBody = body,
        )
            .enqueue(object : retrofit2.Callback<AddBidResponse> {
                override fun onFailure(call: Call<AddBidResponse>, t: Throwable) {

                    successfulAdd.value = false
                    messageAdd.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    Log.e(TAG, "Throwanble ${t.cause} ${t.message}")
                    pb.visibility = View.GONE
                    UIUtils.createSnackbar(messageAdd.value, context, mSnackbarView)

                }

                override fun onResponse(
                    call: Call<AddBidResponse>,
                    response: Response<AddBidResponse>
                ) {

                    Log.e(
                        TAG,
                        response.message() + response.body()?.msg + response.body().toString()
                    )
                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Bid added successfully.") {
                            successfulAdd.value = true
                            messageAdd.value =
                                context.resources.getString(R.string.bidAdded)

                        } else {
                            successfulAdd.value = false
                            messageAdd.value = response.body()?.msg.toString()
                        }
                        if (response.body() != null) {
                            addBid.value = response.body()

                        }
                    } else {
                        successfulAdd.value = false
                        messageAdd.value = response.body()?.msg.toString()
                    }

                }
            })


        return addBid


    }


    fun viewDemandFunction(
        body: ViewDemandBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<ViewDemandResponse> {
        demandStock = demandFunction(body, pb, mSnackbarView)
        return demandStock
    }

    private fun demandFunction(
        body: ViewDemandBody,
        pb: ProgressBar,
        mSnackbarView: CoordinatorLayout
    ): MutableLiveData<ViewDemandResponse> {

        myDemandService.getOpenDemand(

            body = body
        )
            .enqueue(object : retrofit2.Callback<ViewDemandResponse> {
                override fun onFailure(call: Call<ViewDemandResponse>, t: Throwable) {
                    successfulDemand.value = false
                    messageDemand.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    Log.e(TAG, "For the farmer, throawable is ${t.message} ${t.cause}")
                    pb.visibility = View.GONE
                    UIUtils.createSnackbar(messageDemand.value, context, mSnackbarView)

                }

                override fun onResponse(
                    call: Call<ViewDemandResponse>,
                    response: Response<ViewDemandResponse>
                ) {

                    Log.e(
                        TAG,
                        response.message() + response.body()?.msg + response.body().toString()
                    )
                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Demand retrieved successfully.") {
                            successfulDemand.value = true
                            messageDemand.value =
                                context.resources.getString(R.string.demanndLoaded)
                            demandStock.value = response.body()!!

                        } else {
                            successfulBid.value = false
                            messageBid.value = response.body()?.msg.toString()
                        }

                    } else {
                        successfulBid.value = false
                        messageBid.value = response.body()?.msg.toString()
                    }

                    demandStock.value = response.body()
                }
            })

        return demandStock


    }

}
