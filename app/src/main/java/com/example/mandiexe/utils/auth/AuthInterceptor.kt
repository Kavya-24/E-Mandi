package com.example.mandiexe.utils.auth

import android.content.Context
import android.util.Log
import com.example.mandiexe.interfaces.RetrofitClient
import com.example.mandiexe.models.responses.auth.LoginResponse
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call

/**
 * Interceptor to add auth token to requests access tokens
 */

class AuthInterceptor(context: Context) : Interceptor {

    private val sessionManager =
        SessionManager(context)

    val service = RetrofitClient.getAuthInstance()
    val prefManager =
        PreferenceManager()

    override fun intercept(chain: Interceptor.Chain): Response {


        val requestBuilder = chain.request().newBuilder()

        // If token has been saved, add it to the request
        sessionManager.fetchAcessToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        Log.e("AUTH", "In auth interceptor and access token is " + sessionManager.fetchAcessToken())

        val mResponse = chain.proceed(requestBuilder.build())

        if (mResponse.code == 403) {
            Log.e("AUTH", "In no access token")
            val req = getNewLoginResponse()


            Log.e("AUth", "Request response is req" + req.toString())
            //Save this
            req.user?.accessToken?.let { sessionManager.saveAuth_access_Token(it) }
            req.user?.refreshToken?.let { prefManager.putAuthToken(it) }
            val newAccessToken = sessionManager.fetchAcessToken()
            //Create a new request
            val newAuthenticationRequest: Request = requestBuilder
                .header("Authorization", "Bearer $newAccessToken")
                .build()

            //Close previous response
            mResponse.close()
            val newResponse = chain.proceed(newAuthenticationRequest)

            return newResponse

        }

        mResponse.close()
        return chain.proceed(requestBuilder.build())        //This is mResponse
    }

    private fun getNewLoginResponse(): LoginResponse {

        var mRespone = LoginResponse("", LoginResponse.User("", "", false, "", "", ""), "")

        service.getAccessToken(prefManager.authToken!!)
            .enqueue(object : retrofit2.Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: retrofit2.Response<LoginResponse>
                ) {
                        Log.e("AUTH", "In response is " + response.toString())
                    if(response.isSuccessful){
                        mRespone = response.body()!!
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("AUTH", "In failed to get new access token " + t.cause + t.message)
                }
            })


        return mRespone

    }
}

