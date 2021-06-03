package com.example.mandiexe.interfaces

//import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.example.mandiexe.utils.auth.AuthInterceptor
import com.example.mandiexe.utils.auth.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Keep
object RetrofitClient {

    private val url = "https://e-mandi-app.herokuapp.com/"
    private val TAG = RetrofitClient::class.java.simpleName

    private fun okhttpClient(context: Context): OkHttpClient {

        val tAuthenticator = TokenAuthenticator(context)

        return OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(
                    context
                )
            )
            .authenticator(
                tAuthenticator
            )
            .followRedirects(false)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    //This is the auth token to be used with firebase
    private fun authClient(): OkHttpClient {

        return OkHttpClient.Builder()
            .followRedirects(false)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    fun getAuthInstance(): authInterface {

        Log.e(TAG, "In auth instance")
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(authInterface::class.java)
    }

    fun makeCallsForSupplies(context: Context): mySupplyInterface {

        //Moshi class

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create())
            //  .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okhttpClient(context))
            .build().create(mySupplyInterface::class.java)

    }

    fun makeCallsForBids(context: Context): myBidsInterface {


        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create())
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okhttpClient(context))
            .build().create(myBidsInterface::class.java)

    }

    fun makeCallsForDemands(context: Context): myDemandsInterface {


        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create())
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okhttpClient(context))
            .build().create(myDemandsInterface::class.java)

    }


}