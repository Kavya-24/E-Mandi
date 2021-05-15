package com.example.mandiexe.interfaces

import com.example.mandiexe.models.body.NewDemandSearchBody
import com.example.mandiexe.models.body.bid.*
import com.example.mandiexe.models.responses.demand.NewDemandsResponse
import com.example.mandiexe.models.responses.bids.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface myBidsInterface {

    //All bids and their history
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bidhistory")
    fun getFarmerBidHistoryGlobal(
    ): Call<BidHistoryResponse>


    //Get the demands of the farmer
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bids")
    fun getFarmerDemands(
        
    ): Call<FamerBidsResponse>


    //View a particular bid
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bid/view")
    fun getFarmerViewParticularBid(
        @Body mViewBidBody: ViewBidBody
        
    ): Call<ViewBidResponse>


    //Modify that bid
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bid/update")
    fun getFarmerUpdateBid(
        @Body mUpdateBidBody: UpdateBidBody
        
    ): Call<UpdateBidResponse>


    //Delete the bid
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bid/delete")
    fun getFarmerDeleteBid(
        @Body mDeleteBidBody: DeletBidBody
        
    ): Call<DeleteBidResponse>


    //Add a bid
    @Headers("Content-Type:application/json")
    @POST("api/farmer/bid/add")
    fun getFarmerAddBid(
        @Body mAddBidBody: AddBidBody
        
    ): Call<AddBidResponse>


    //Delete the bid
    @Headers("Content-Type:application/json")
    @POST("api/demand/search")
    fun getSearchReq(
        @Body body: NewDemandSearchBody
    ): Call<NewDemandsResponse>






}