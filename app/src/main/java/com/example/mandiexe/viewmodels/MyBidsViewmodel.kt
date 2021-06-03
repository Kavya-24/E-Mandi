package com.example.mandiexe.viewmodels

import android.view.View
import android.widget.ProgressBar
import androidx.annotation.Keep
import androidx.constraintlayout.widget.ConstraintLayout
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

@Keep
class MyBidsViewmodel : ViewModel() {

    val TAG = MyBidsViewmodel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val mySupplyService = RetrofitClient.makeCallsForBids(context)

    //For getting the details of the supply stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    private var mReq: MutableLiveData<FamerBidsResponse> = MutableLiveData()

    fun reqFunction(
        container_my_bids: ConstraintLayout,
        pb: ProgressBar
    ): MutableLiveData<FamerBidsResponse> {

        mReq = mReqFunction(container_my_bids, pb)
        return mReq
    }


    private fun mReqFunction(
        container_my_bids: ConstraintLayout,
        pb: ProgressBar
    ): MutableLiveData<FamerBidsResponse> {

        mySupplyService.getFarmerDemands(
        )
            .enqueue(object : retrofit2.Callback<FamerBidsResponse> {
                override fun onFailure(call: Call<FamerBidsResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    UIUtils.logThrowables(t, TAG)
                    UIUtils.createSnackbar(message.value, context, container_my_bids)
                    pb.visibility = View.GONE

                }

                override fun onResponse(
                    call: Call<FamerBidsResponse>,
                    response: Response<FamerBidsResponse>
                ) {

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
