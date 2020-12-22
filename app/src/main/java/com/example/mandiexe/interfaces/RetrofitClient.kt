package com.example.mandiexe.interfaces

import android.content.Context
import com.example.mandiexe.utils.auth.AuthInterceptor
import com.example.mandiexe.utils.auth.TokenAuthenticator
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val url = "http://qme.company:8000/"
    private val TAG = RetrofitClient::class.java.simpleName

    private fun okhttpClient(context: Context): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY


        return OkHttpClient.Builder()
            // .addInterceptor(interceptor)
            .addInterceptor(
                AuthInterceptor(
                    context
                )
            )
            .followRedirects(false)
            .writeTimeout(20, TimeUnit.SECONDS)
            .authenticator(
                TokenAuthenticator(
                    context
                )
            )
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
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(authClient())
            .build()
            .create(authInterface::class.java)
    }

    fun makeCallsForSupplies(context: Context): mySupplyInterface {

        //Moshi class

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
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