package com.example.mandiexe.interfaces

import com.example.mandiexe.models.body.supply.*
import com.example.mandiexe.models.responses.supply.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface mySupplyInterface {

    //Get the active stocks
    @Headers("Content-Type:application/json")
    @POST("api/farmer/supplies")
    fun getFarmerActiveSupplies(
    ): Call<FarmerSuppliesResponse>


    //Get the active stocks
    @Headers("Content-Type:application/json")
    @POST("api/supply/view")
    fun getViewCurrentSupply(
        @Body body: ViewSupplyBody
    ): Call<ViewSupplyResponse>


    //Get the stocks history
    @Headers("Content-Type:application/json")
    @POST("api/farmer/history")
    fun getFarmerSupplyHistory(
    ): Call<SupplyHistoryResponse>


    //Add a stock
    @Headers("Content-Type:application/json")
    @POST("api/supply/add")
    fun getAddSupply(
        @Body mAddSupply: AddSupplyBody
    ): Call<AddSupplyResponse>

    //Delete a stock
    @Headers("Content-Type:application/json")
    @POST("api/supply/delete")
    fun getDeleteSupply(
        @Body mDeleteSupply: DeleteSupplyBody
    ): Call<DeleteSupplyResponse>

    //Modify a stock
    @Headers("Content-Type:application/json")
    @POST("api/supply/update")
    fun getModifySupply(
        @Body mModifySupply: ModifySupplyBody
    ): Call<ModifySupplyResponse>


    //Search a crop and its details
    @Headers("Content-Type:application/json")
    @POST("api/farmer/crop/search")
    fun getSearchCropGlobally(
        @Body body: SearchGlobalCropBody
    ): Call<SearchGlobalCropResponse>

    //Crop autocomplete
    @Headers("Content-Type:application/json")
    @POST("api/farmer/crop/autocomplete")
    fun getCropAutoComplete(
        @Body body: CropSearchAutoCompleteBody
    ): Call<CropSearchAutocompleteResponse>


    //Add Growth
    @Headers("Content-Type:application/json")
    @POST("api/farmer/growth/add")
    fun getFarmerGrowthAdd(
        @Body body: AddGrowthBody
    ): Call<AddGrowthResponse>


}