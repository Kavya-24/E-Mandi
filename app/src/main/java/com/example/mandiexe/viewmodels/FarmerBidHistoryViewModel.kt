package com.example.mandiexe.viewmodels

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.Keep
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.responses.bids.BidHistoryResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.UIUtils
import retrofit2.Call
import retrofit2.Response

@Keep

class FarmerBidHistoryViewModel : ViewModel() {


    val TAG = FarmerBidHistoryViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val myBidService = RetrofitClient.makeCallsForBids(context)

    //For getting the details of the Bid stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    var BidHistory: MutableLiveData<BidHistoryResponse> = MutableLiveData()

    fun BidFunction(
        mSnackbarView: ConstraintLayout,
        pb: ProgressBar
    ): MutableLiveData<BidHistoryResponse> {

        BidHistory = BidStockFunction(mSnackbarView, pb)
        return BidHistory
    }


    private fun BidStockFunction(
        mSnackbarView: ConstraintLayout,
        pb: ProgressBar
    ): MutableLiveData<BidHistoryResponse> {

        myBidService.getFarmerBidHistoryGlobal(
        )
            .enqueue(object : retrofit2.Callback<BidHistoryResponse> {
                override fun onFailure(call: Call<BidHistoryResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    UIUtils.logThrowables(t, TAG)
                    UIUtils.createSnackbar(message.value, context, mSnackbarView)
                    pb.visibility = View.GONE


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
                        message.value = context.resources.getString(R.string.couldNotLoadAllBids)
                    }


                    BidHistory.value = response.body()

                }
            })


        return BidHistory


    }

}
