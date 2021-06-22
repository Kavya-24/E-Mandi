package com.example.mandiexe.interfaces

import androidx.annotation.Keep
import com.example.mandiexe.models.body.bid.ViewDemandBody
import com.example.mandiexe.models.responses.demand.ViewDemandResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@Keep
interface myDemandsInterface{

    @Headers("Content-Type:application/json")
    @POST("api/demand/view")
    fun getOpenDemand(
        @Body body: ViewDemandBody
    ): Call<ViewDemandResponse>


}