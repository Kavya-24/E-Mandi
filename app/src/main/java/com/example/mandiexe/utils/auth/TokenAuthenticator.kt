package com.example.mandiexe.utils.auth

import android.content.Context
import com.example.mandiexe.interfaces.RetrofitClient
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.apache.http.HttpHeaders.AUTHORIZATION
import java.io.IOException
import java.lang.reflect.Proxy

//    private fun getNewAaccessToken(): String {
//
//        var res = ""
//        service.getAccessToken(refreshToken = preferenceManager.authToken.toString())
//            .enqueue(object : retrofit2.Callback<LoginResponse> {
//                override fun onResponse(
//                    call: Call<LoginResponse>,
//                    response: retrofit2.Response<LoginResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        res = response.body()?.user?.accessToken.toString()
//                    }
//                }
//
//                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//
//                }
//            })
//
//        return res
//
//    }
//

class TokenAuthenticator(context: Context) : Authenticator {


    val service = RetrofitClient.getAuthInstance()
    val prefManager =
        PreferenceManager()

    @Throws(IOException::class)
    fun authenticateProxy(proxy: Proxy, response: Response): Request? {
        // Null indicates no attempt to authenticate.
        return null;
    }

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.code == 401) {
            val req = service.getAccessToken(refreshToken = prefManager.authToken.toString()).execute()
            if(req.isSuccessful){
                val newAccessToken = req.body()?.user?.accessToken


                    // Add new header to rejected request and retry it
                    return response.request.newBuilder()
                        .header(AUTHORIZATION, "Bearer $newAccessToken")
                        .build();

            }

        }

        return response.request.newBuilder().build()

    }

}