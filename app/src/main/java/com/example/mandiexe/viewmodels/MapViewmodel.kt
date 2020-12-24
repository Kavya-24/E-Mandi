package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.authBody.SignUpBody
import com.example.mandiexe.models.responses.auth.SignUpResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import retrofit2.Call
import retrofit2.Response

class MapViewmodel : ViewModel() {

    val TAG = OTViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val mySupplyService = RetrofitClient.getAuthInstance()

    //For getting the details of the supply stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    var mSignUp: MutableLiveData<SignUpResponse> = MutableLiveData()

    fun signFunction(body: SignUpBody): MutableLiveData<SignUpResponse> {

        Log.e(TAG, "In sign up function")
        mSignUp = mSignUpFunction(body)
        return mSignUp
    }


    fun mSignUpFunction(body: SignUpBody): MutableLiveData<SignUpResponse> {

        Log.e(TAG, body.toString())
        mySupplyService.getSignUp(
            mSignUpBody = body
        )
            .enqueue(object : retrofit2.Callback<SignUpResponse> {

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    Log.e(TAG, "In on failed and message {${message.value}} and call is ")
                }

                override fun onResponse(
                    call: Call<SignUpResponse>,
                    response: Response<SignUpResponse>
                ) {

                    Log.e(
                        TAG,
                        " In on response " + response.message() + response.body()?.msg + response.body()
                            .toString()
                    )


                    if (response.isSuccessful) {


                        if (response.body()?.msg == "Registeration successful.") {
                            successful.value = true
                            message.value =
                                context.resources.getString(R.string.signUpSuccess)

                        } else {
                            successful.value = false
                            message.value = response.body()?.msg.toString()
                        }

                        mSignUp.value = response.body()!!

                    } else {
                        successful.value = false
                        message.value = response.body()?.msg.toString()
                    }

                }
            })

        Log.e(TAG, " Outside retrofit call and mSignUp is " + mSignUp.value.toString())
        return mSignUp


    }


}