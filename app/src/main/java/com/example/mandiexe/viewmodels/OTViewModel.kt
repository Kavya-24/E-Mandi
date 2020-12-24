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
import com.example.mandiexe.utils.auth.PreferenceManager
import com.example.mandiexe.utils.auth.SessionManager
import retrofit2.Call
import retrofit2.Response

class OTViewModel : ViewModel() {

    val TAG = OTViewModel::class.java.simpleName

    private val context = ApplicationUtils.getContext()
    private val mySupplyService = RetrofitClient.getAuthInstance()


    //For getting the details of the supply stock
    var successful: MutableLiveData<Boolean> = MutableLiveData()
    var message: MutableLiveData<String> = MutableLiveData()

    var mLogin: MutableLiveData<LoginResponse> = MutableLiveData()

    private val sessionManager = SessionManager(ApplicationUtils.getContext())
    private val preferenceManager = PreferenceManager()

    fun lgnFunction(body: LoginBody): MutableLiveData<LoginResponse> {

        Log.e(TAG, "In lgn function")
        mLogin = mLoginFunction(body)
        return mLogin
    }


    fun mLoginFunction(body: LoginBody): MutableLiveData<LoginResponse> {

        Log.e(TAG, body.toString())


        mySupplyService.getLogin(
            mLoginBody = body
        )
            .enqueue(object : retrofit2.Callback<LoginResponse> {

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                    successful.value = false
                    message.value = ExternalUtils.returnStateMessageForThrowable(t)
                    //Response is null
                    Log.e(TAG, "In on Failure")
                    mLogin.value

                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    Log.e(
                        TAG,
                        " In on response " + response.message() + response.body()?.msg + response.body()
                            .toString() + response.body()?.user.toString()
                    )


                    if (response.isSuccessful) {


                        if (response.body()?.msg == "Login successful.") {
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


        Log.e(TAG, "Befre retrurning " + mLogin.value)
        return mLogin


    }


}
