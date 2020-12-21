package com.example.mandiexe.interfaces

import com.example.mandiexe.models.body.supply.AddSupplyBody
import com.example.mandiexe.models.body.supply.DeleteSupplyBody
import com.example.mandiexe.models.body.supply.ModifySupplyBody
import com.example.mandiexe.models.responses.supply.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface mySupplyInterface {

    //Get the active stocks
    @Headers("Content-Type:application/json")
    @POST("/api/farmer/supplies")
    fun getFarmerActiveSupplies(
    ): Call<FarmerSuppliesResponse>


    //Get the stocks history
    @Headers("Content-Type:application/json")
    @POST("/api/farmer/history")
    fun getFarmerSupplyHistory(
    ): Call<FarmerHistoryResponse>


    //Add a stock
    @Headers("Content-Type:application/json")
    @POST("/api/supply/add")
    fun getAddSupply(
        @Body mAddSupply: AddSupplyBody
    ): Call<AddSupplyResponse>

    //Delete a stock
    @Headers("Content-Type:application/json")
    @POST("/api/supply/delete")
    fun getDeleteSupply(
        @Body mDeleteSupply: DeleteSupplyBody
    ): Call<DeleteSupplyResponse>

    //Modify a stock
    @Headers("Content-Type:application/json")
    @POST("/api/supply/update")
    fun getModifySupply(
        @Body mModifySupply: ModifySupplyBody
    ): Call<ModifySupplyResponse>


}