package com.example.mandiexe.interfaces

import com.example.mandiexe.models.body.SearchCropReqBody
import com.example.mandiexe.models.body.bid.AddBidBody
import com.example.mandiexe.models.body.bid.DeletBidBody
import com.example.mandiexe.models.body.bid.UpdateBidBody
import com.example.mandiexe.models.body.bid.ViewBidBody
import com.example.mandiexe.models.responses.SearchCropReqResponse
import com.example.mandiexe.models.responses.bids.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface myBidsInterface {

    //All bids and their history
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bidhistory")
    fun getFarmerBidHistoryGlobal(
        @Header("Authorization") accessToken: String?
    ): Call<FarmerBidHistoryGlobalResponse>


    //Get the demands of the farmer
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bids")
    fun getFarmerDemands(
        @Header("Authorization") accessToken: String?
    ): Call<FamerBidsResponse>


    //View a particular bid
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bid/view")
    fun getFarmerViewParticularBid(
        @Body mViewBidBody: ViewBidBody,
        @Header("Authorization") accessToken: String?
    ): Call<ViewBidResponse>


    //Modify that bid
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bid/update")
    fun getFarmerUpdateBid(
        @Body mUpdateBidBody: UpdateBidBody,
        @Header("Authorization") accessToken: String?
    ): Call<UpdateBidResponse>


    //Delete the bid
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bid/delete")
    fun getFarmerDeleteBid(
        @Body mDeleteBidBody: DeletBidBody,
        @Header("Authorization") accessToken: String?
    ): Call<DeleteBidResponse>


    //Add a bid
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bid/add")
    fun getFarmerAddBid(
        @Body mAddBidBody: AddBidBody,
        @Header("Authorization") accessToken: String?
    ): Call<AddBidResponse>


    //Delete the bid
    @Headers("Content-Type:application/json")
    @POST("api/supply/search")
    fun getSearchReq(
        @Body body: SearchCropReqBody,
        @Header("Authorization") accessToken: String?
    ): Call<SearchCropReqResponse>



}