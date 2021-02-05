package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.bids.FamerBidsResponse
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.TimeConversionUtils.convertTimeToEpoch

class MyRequirementAdapter(val itemClick: OnMyBidClickListener) :
    RecyclerView.Adapter<MyRequirementAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    var lst: List<FamerBidsResponse.Bid.Demand> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate
        val CROP_NAME = itemView.findViewById<TextView>(R.id.tv_requirement_crop_name)
        val CROP_LOCATION = itemView.findViewById<TextView>(R.id.tv_requirement_crop_location)
        val CROP_QUANTITY = itemView.findViewById<TextView>(R.id.tv_requirement_quantity)
        val CROP_EXP = itemView.findViewById<TextView>(R.id.tv_requirement_expiration)
        val CROP_CURRENT_BID = itemView.findViewById<TextView>(R.id.tv_requirement_current_bid)
        val CROP_IOP = itemView.findViewById<TextView>(R.id.tv_requirement_initial_offer_price)
        val CROP_MY_BID = itemView.findViewById<TextView>(R.id.tv_requirement_my_bid)
        val CROP_LAST_UPDATED = itemView.findViewById<TextView>(R.id.tv_requirement_last_upadted)
        val CROP_DELTA = itemView.findViewById<TextView>(R.id.tv_my_req_delta)
        val CROP_CHANGE = itemView.findViewById<ImageView>(R.id.iv_req_image)


        //Bind a single item
        fun bindPost(_listItem: FamerBidsResponse.Bid.Demand, itemClick: OnMyBidClickListener) {
            with(_listItem) {

                CROP_NAME.text = _listItem.crop

                //CROP_LOCATION = _listItem.demand.demander.

                CROP_QUANTITY.text = _listItem.qty.toString()
                CROP_EXP.text = TimeConversionUtils.convertTimeToEpoch(_listItem.expiry)

                CROP_CURRENT_BID.text = _listItem.lastBid.toString()
                CROP_IOP.text = _listItem.offerPrice.toString()


                CROP_LAST_UPDATED.text = convertTimeToEpoch(_listItem.lastModified)

                CROP_MY_BID.text = _listItem.currentBid.toString()

                val currentBid = _listItem.currentBid
                val askBid = _listItem.offerPrice
                val ans = currentBid - askBid

                if (ans > 0) {
                    CROP_DELTA.text = "+" + ans.toString()
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.deltaGreen))
                    CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.deltaGreen))
                } else if (ans < 0) {
                    CROP_DELTA.text = ans.toString()
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.deltaRed))
                    CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.deltaRed))

                } else if (ans == 0) {
                    CROP_DELTA.text = ans.toString()
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.wildColor))
                    CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.wildColor))

                }




                itemView.setOnClickListener {
                    itemClick.viewMyBidDetails(_listItem)
                }


            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_requirement, parent, false)
        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return lst.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bindPost(lst[position], itemClick)

    }

}


interface OnMyBidClickListener {
    fun viewMyBidDetails(_listItem: FamerBidsResponse.Bid.Demand)
}


