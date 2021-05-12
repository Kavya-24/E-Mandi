package com.example.mandiexe.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mandiexe.R
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.body.authBody.LoginBody
import com.example.mandiexe.models.responses.auth.LoginResponse
import com.example.mandiexe.utils.ApplicationUtils
import com.example.mandiexe.utils.auth.PreferenceManager
import com.example.mandiexe.utils.auth.SessionManager
import com.example.mandiexe.utils.usables.ExternalUtils
import com.example.mandiexe.utils.usables.UIUtils
import retrofit2.Call
import retrofit2.Response

class OTViewModel : ViewModel() {

    val TAG = OTViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val myAuthService = RetrofitClient.getAuthInstance()


    //For getting the details of the Auth stock
    var successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    var mLogin: MutableLiveData<LoginResponse> = MutableLiveData()

    fun lgnFunction(body: LoginBody): MutableLiveData<LoginResponse> {

        Log.e(TAG, "In lgn function")
        mLogin = mLoginFunction(body)
        return mLogin
    }


    private fun mLoginFunction(body: LoginBody): MutableLiveData<LoginResponse> {

        Log.e(TAG, body.toString())


        myAuthService.getLogin(
            mLoginBody = body
        )
            .enqueue(object : retrofit2.Callback<LoginResponse> {

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    UIUtils.logThrowables(t,TAG)
                    mLogin.value = null

                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    Log.e(
                        TAG,
                        " In on response and values  " + response.message() + response.body()?.msg + response.body()
                            .toString() + response.body()?.user.toString()
                    )


                    if (response.isSuccessful) {


                        if (response.body()?.msg == "Login successful.") {

                            Log.e(
                                TAG,
                                " In on response and values  " + response.message() + response.body()?.msg + response.body()
                                    .toString() + response.body()?.user.toString()
                            )

                            successful.value = true
                            message.value =
                                context.resources.getString(R.string.loginSuceed)

                        } else if (response.body()?.msg == "Phone Number not registered.") {
                            successful.value = true
                            message.value = context.resources.getString(R.string.loginNew)

                        } else {
                            successful.value = false
                            message.value = response.body()?.msg.toString()
                        }

                        mLogin.value = response.body()!!

                    } else {
                        successful.value = false
                        message.value = response.body()?.msg.toString()
                    }





                    mLogin.value = response.body()
                }
            })



        return mLogin


    }


}
