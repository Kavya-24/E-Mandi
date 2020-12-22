package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.authBody.LoginBody
import com.example.mandiexe.models.responses.auth.LoginResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.ExternalUtils
import retrofit2.Call
import retrofit2.Response

class OTViewModel : ViewModel() {

    val TAG = OTViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val mySupplyService = RetrofitClient.getAuthInstance()


    //For getting the details of the supply stock
    val successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    private var mLogin: MutableLiveData<LoginResponse> = MutableLiveData()

    fun lgnFunction(body: LoginBody): MutableLiveData<LoginResponse> {

        mLogin = mLoginFunction(body)
        return mLogin
    }


    fun mLoginFunction(body: LoginBody): MutableLiveData<LoginResponse> {

        mySupplyService.getLogin(
            mLoginBody = body
        )
            .enqueue(object : retrofit2.Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    Log.e(
                        TAG,
                        response.message() + response.body()?.msg + response.body().toString()
                    )


                    if (response.isSuccessful) {
                        if (response.body()?.msg == "Login successful.") {
                            successful.value = true
                            message.value =
                                context.resources.getString(R.string.loginSuceed)
                            mLogin.value = response.body()!!

                        } else if (response.body()?.msg == "Phone Number not registered.") {
                            successful.value = true
                            message.value = context.resources.getString(R.string.loginNew)
                        } else {
                            successful.value = false
                            message.value = response.body()?.msg.toString()
                        }

                    } else {
                        successful.value = false
                        message.value = response.body()?.msg.toString()
                    }

                }
            })


        return mLogin


    }


}
