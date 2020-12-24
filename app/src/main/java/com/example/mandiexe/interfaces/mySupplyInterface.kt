package com.example.mandiexe.interfaces

import com.example.mandiexe.models.body.supply.*
import com.example.mandiexe.models.responses.supply.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface mySupplyInterface {

    //Get the active stocks
    @Headers("Content-Type:application/json")
    @POST("api/farmer/supplies")
    fun getFarmerActiveSupplies(
        @Header("Authorization") accessToken: String?
    ): Call<FarmerSuppliesResponse>


    //Get the active stocks
    @Headers("Content-Type:application/json")
    @POST("api/supply/view")
    fun getViewCurrentSupply(
        @Body body: ViewSupplyBody,
        @Header("Authorization") accessToken: String?
    ): Call<ViewSupplyResponse>


    //Get the stocks history
    @Headers("Content-Type:application/json")
    @POST("api/farmer/history")
    fun getFarmerSupplyHistory(
        @Header("Authorization") accessToken: String?
    ): Call<FarmerHistoryResponse>


    //Add a stock
    @Headers("Content-Type:application/json")
    @POST("api/supply/add")
    fun getAddSupply(
        @Body mAddSupply: AddSupplyBody,
        @Header("Authorization") accessToken: String?
    ): Call<AddSupplyResponse>

    //Delete a stock
    @Headers("Content-Type:application/json")
    @POST("api/supply/delete")
    fun getDeleteSupply(
        @Body mDeleteSupply: DeleteSupplyBody,
        @Header("Authorization") accessToken: String?
    ): Call<DeleteSupplyResponse>

    //Modify a stock
    @Headers("Content-Type:application/json")
    @POST("api/supply/update")
    fun getModifySupply(
        @Body mModifySupply: ModifySupplyBody,
        @Header("Authorization") accessToken: String?
    ): Call<ModifySupplyResponse>


    //Search a crop and its details
    @Headers("Content-Type:application/json")
    @POST("api/farmer/crop/search")
    fun getSearchCropGlobally(
        @Body body: SearchGlobalCropBody,
        @Header("Authorization") accessToken: String?
    ): Call<SearchGlobalCropResponse>

    //Crop autocomplete
    @Headers("Content-Type:application/json")
    @POST("api/farmer/crop/autocomplete")
    fun getCropAutoComplete(
        @Body body: CropSearchAutoCompleteBody,
        @Header("Authorization") accessToken: String?
    ): Call<CropSearchAutocompleteResponse>


}