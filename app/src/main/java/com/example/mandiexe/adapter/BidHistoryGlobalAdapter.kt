package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.bids.BidHistoryResponse
import com.example.mandiexe.utils.usables.TimeConversionUtils
import com.example.mandiexe.utils.usables.TimeConversionUtils.convertTimeToEpoch


class BidHistoryAdapter(val itemClick: OnMyBidHistoryGlobalClickListener) :
    RecyclerView.Adapter<BidHistoryAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    var lst: List<BidHistoryResponse.Bid> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        //Use other items you want the layout to inflate
        val CROP_NAME = itemView.findViewById<TextView>(R.id.tv_Bid_crop_name)
        val CROP_LOCATION = itemView.findViewById<TextView>(R.id.tv_Bid_crop_location_history)
        val CROP_QUANTITY = itemView.findViewById<TextView>(R.id.ans_crop_quanity_bid_history)
        val CROP_EXP = itemView.findViewById<TextView>(R.id.ans_crop_exp_bid_history)
        val CROP_CURRENT_BID = itemView.findViewById<TextView>(R.id.tv_Bid_current_bid)
        val CROP_IOP = itemView.findViewById<TextView>(R.id.tv_Bid_initial_offer_price)
        val CROP_LAST_UPDATED = itemView.findViewById<TextView>(R.id.tv_Bid_last_upadted)
        val CROP_DELTA = itemView.findViewById<TextView>(R.id.tv_my_crop_active)
        val CROP_CHANGE = itemView.findViewById<ImageView>(R.id.iv_bid_history_image)


        //Bind a single item
        fun bindPost(
            _listItem: BidHistoryResponse.Bid,
            itemClick: OnMyBidHistoryGlobalClickListener
        ) {
            with(_listItem) {

                CROP_NAME.text = _listItem.demand.crop
                CROP_LOCATION.text = _listItem.supplier.address


                CROP_QUANTITY.text = _listItem.qty.toString()
                CROP_EXP.text = TimeConversionUtils.convertTimeToEpoch(_listItem.demand.expiry)
                CROP_CURRENT_BID.text = _listItem.currentBid.toString()
                CROP_IOP.text = _listItem.demand.offerPrice.toString()
                CROP_LAST_UPDATED.text = convertTimeToEpoch(_listItem.lastModified)


                if (_listItem.active == true) {

                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.green_A700))
                    CROP_DELTA.text = itemView.context.resources.getString(R.string.activeBid)
                } else {

                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.red_A700))
                    CROP_DELTA.text = itemView.context.resources.getString(R.string.inactiveBid)

                }


                if (currentBid != 0) {

                    val currentBid = _listItem.currentBid
                    val askBid = _listItem.demand.offerPrice
                    val ans = currentBid - askBid


                    if (ans > 0) {

                        CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.green_A700))

                    } else if (ans < 0) {
                        CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.red_A700))

                    } else if (ans == 0) {
                        CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.blue_A700))
                        CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.blue_A700))

                    }

                }


                itemView.setOnClickListener {
                    itemClick.viewMyStockDetails(_listItem)
                }


            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bid_history_global, parent, false)
        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return lst.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bindPost(lst[position], itemClick)

    }

}


interface OnMyBidHistoryGlobalClickListener {
    fun viewMyStockDetails(_listItem: BidHistoryResponse.Bid)
}


