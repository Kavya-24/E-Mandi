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
import com.example.mandiexe.models.body.supply.AddGrowthBody
import com.example.mandiexe.models.body.supply.AddSupplyBody
import com.example.mandiexe.models.responses.supply.AddGrowthResponse
import com.example.mandiexe.models.responses.supply.AddSupplyResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.UIUtils
import retrofit2.Call
import retrofit2.Response

@Keep

class AddStockViewModel : ViewModel() {

    val TAG = AddStockViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val mySupplyService = RetrofitClient.makeCallsForSupplies(context)

    //For getting the details of the supply stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    val successfulGrowth: MutableLiveData<Boolean> = MutableLiveData()
    var messageGrowth: MutableLiveData<String> = MutableLiveData()

    var addStock: MutableLiveData<AddSupplyResponse> = MutableLiveData()
    var growthStock: MutableLiveData<AddGrowthResponse> = MutableLiveData()

    fun addFunction(
        body: AddSupplyBody, mSnackbar: CoordinatorLayout,
        pb: ProgressBar
    ): MutableLiveData<AddSupplyResponse> {

        addStock = addStockFunction(body, mSnackbar, pb)
        return addStock
    }


    private fun addStockFunction(
        body: AddSupplyBody, mSnackbar: CoordinatorLayout,
        pb: ProgressBar
    ): MutableLiveData<AddSupplyResponse> {


        mySupplyService.getAddSupply(
            mAddSupply = body,
        )
            .enqueue(object : retrofit2.Callback<AddSupplyResponse> {
                override fun onFailure(call: Call<AddSupplyResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    Log.e(
                        TAG,
                        "Throwable  Supply for adding stock" + t.message + t.cause + message.value
                    )

                    UIUtils.createSnackbar(message.value, context, mSnackbar)
                    pb.visibility = View.GONE

                    //Response is null
                }

                override fun onResponse(
                    call: Call<AddSupplyResponse>,
                    response: Response<AddSupplyResponse>
                ) {

                    Log.e(
                        TAG,
                        " In response " + response.message() + " " + response.body()?.msg + " " + response.body()
                            .toString() + response.code() + " " + response.errorBody()
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
                        if (response.body() != null) {
                            addStock.value = response.body()!!
                        }

                    } else {
                        successful.value = false
                        message.value = response.body()?.msg.toString()
                    }

                    addStock.value = response.body()

                }
            })


        return addStock


    }


    fun growthFunction(
        body: AddGrowthBody,
        mSnackbar: CoordinatorLayout,
        pb: ProgressBar
    ): MutableLiveData<AddGrowthResponse> {

        growthStock = growthStockFunction(body, mSnackbar, pb)
        return growthStock
    }


    private fun growthStockFunction(
        body: AddGrowthBody,
        mSnackbar: CoordinatorLayout,
        pb: ProgressBar
    ): MutableLiveData<AddGrowthResponse> {

        Log.e(TAG, "In add growth internal function")

        mySupplyService.getFarmerGrowthAdd(
            body = body,
        )
            .enqueue(object : retrofit2.Callback<AddGrowthResponse> {
                override fun onFailure(call: Call<AddGrowthResponse>, t: Throwable) {
                    successfulGrowth.value = false
                    messageGrowth.value = ExternalUtils.returnStateMessageForThrowable(t)
                    UIUtils.logThrowables(t, TAG)
                    UIUtils.createSnackbar(messageGrowth.value, context, mSnackbar)
                    pb.visibility = View.GONE

                }

                override fun onResponse(
                    call: Call<AddGrowthResponse>,
                    response: Response<AddGrowthResponse>
                ) {

                    Log.e(
                        TAG,
                        " In response " + response.message() + " " + response.body()?.msg + " " + response.body()
                            .toString() + response.code() + " " + response.errorBody()
                    )

                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Growth added successfully.") {
                            successfulGrowth.value = true
                            messageGrowth.value =
                                context.resources.getString(R.string.GrowthAdded)

                        } else {
                            successfulGrowth.value = false
                            messageGrowth.value = response.body()?.msg.toString()
                        }
                        if (response.body() != null) {
                            growthStock.value = response.body()!!
                        }

                    } else {
                        successfulGrowth.value = false
                        messageGrowth.value = response.body()?.msg.toString()
                    }

                    growthStock.value = response.body()

                }
            })


        return growthStock


    }


}
