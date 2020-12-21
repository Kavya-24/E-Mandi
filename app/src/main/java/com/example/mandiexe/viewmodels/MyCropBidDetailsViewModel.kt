package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.supply.DeleteSupplyBody
import com.example.mandiexe.models.body.supply.ModifySupplyBody
import com.example.mandiexe.models.responses.supply.DeleteSupplyResponse
import com.example.mandiexe.models.responses.supply.ModifySupplyResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import com.example.mandiexe.utils.auth.SessionManager
import retrofit2.Call
import retrofit2.Response

class MyCropBidDetailsViewModel : ViewModel() {

    val TAG = MyCropBidDetailsViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val sessionManager = SessionManager(context)
    private val mySupplyService = RetrofitClient.makeCallsForSupplies(context)

    //For getting the details of the supply stock
    val successfulSupply: MutableLiveData<Boolean> = MutableLiveData()
    var messageSupply: MutableLiveData<String> = MutableLiveData()


    //For cancelling the stock
    val successfulCancel: MutableLiveData<Boolean> = MutableLiveData()
    var messageCancel: MutableLiveData<String> = MutableLiveData()


    //For updating stock details
    val successfulUpdate: MutableLiveData<Boolean> = MutableLiveData()
    var messageUpdate: MutableLiveData<String> = MutableLiveData()


    private var deleteStock: MutableLiveData<DeleteSupplyResponse> = MutableLiveData()
    private var modifyStock: MutableLiveData<ModifySupplyResponse> = MutableLiveData()


    fun cancelFunction(body: DeleteSupplyBody): MutableLiveData<DeleteSupplyResponse> {

        deleteStock = deleteStockFunction(body)
        return deleteStock
    }


    fun deleteStockFunction(body: DeleteSupplyBody): MutableLiveData<DeleteSupplyResponse> {

        mySupplyService.getDeleteSupply(
            mDeleteSupply = body,
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}",
        )
            .enqueue(object : retrofit2.Callback<DeleteSupplyResponse> {
                override fun onFailure(call: Call<DeleteSupplyResponse>, t: Throwable) {
                    successfulCancel.value = false
                    messageCancel.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                }

                override fun onResponse(
                    call: Call<DeleteSupplyResponse>,
                    response: Response<DeleteSupplyResponse>
                ) {

                    Log.e(
                        TAG,
                        response.message() + response.body()?.msg + response.body().toString()
                    )
                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Supply deleted successfully.") {
                            successfulCancel.value = true
                            messageCancel.value =
                                context.resources.getString(R.string.supplyDeleted)
                            deleteStock.value = response.body()!!

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


        return deleteStock


    }

    fun updateFunction(body: ModifySupplyBody): MutableLiveData<ModifySupplyResponse> {

        modifyStock = updateStockFunction(body)
        return modifyStock
    }


    fun updateStockFunction(body: ModifySupplyBody): MutableLiveData<ModifySupplyResponse> {

        mySupplyService.getModifySupply(
            mModifySupply = body,
            accessToken = "Bearer ${sessionManager.fetchAcessToken()}",
        )
            .enqueue(object : retrofit2.Callback<ModifySupplyResponse> {
                override fun onFailure(call: Call<ModifySupplyResponse>, t: Throwable) {
                    successfulUpdate.value = false
                    messageUpdate.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                }

                override fun onResponse(
                    call: Call<ModifySupplyResponse>,
                    response: Response<ModifySupplyResponse>
                ) {

                    Log.e(
                        TAG,
                        response.message() + response.body()?.msg + response.body().toString()
                    )
                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Supply updated successfully.") {
                            successfulUpdate.value = true
                            messageUpdate.value =
                                context.resources.getString(R.string.supplyUpdated)
                            modifyStock.value = response.body()!!

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


        return modifyStock


    }


}
