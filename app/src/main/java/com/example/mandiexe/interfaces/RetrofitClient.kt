package com.example.mandiexe.interfaces

import android.content.Context
import android.util.Log
import com.example.mandiexe.utils.LanguageInterceptor
import com.example.mandiexe.utils.auth.AuthInterceptor
import com.example.mandiexe.utils.auth.TokenAuthenticator
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val url = "http://qme.company:8000/"
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
            .addInterceptor(LanguageInterceptor())
            .followRedirects(false)
            .writeTimeout(20, TimeUnit.SECONDS)
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

        //Moshi class
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okhttpClient(context))
            .build().create(myBidsInterface::class.java)

    }


}