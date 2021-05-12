package com.example.mandiexe.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.bids.BidHistoryResponse
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.TimeConversionUtils


class BidHistoryAdapter(val itemClick: OnMyBidClickListenerGlobal) :
    RecyclerView.Adapter<BidHistoryAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    var lst: List<BidHistoryResponse.Bid> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate
        val CROP_NAME = itemView.findViewById<TextView>(R.id.tv_trader_bid_crop_name)
        val CROP_TYPE = itemView.findViewById<TextView>(R.id.tv_trader_bid_crop_type)
        val CROP_QUANTITY = itemView.findViewById<TextView>(R.id.ans_crop_quanity)
        val CROP_EXP = itemView.findViewById<TextView>(R.id.ans_crop_exp)
        val CROP_CURRENT_BID = itemView.findViewById<TextView>(R.id.tv_trader_bid_current_bid)
        val CROP_IOP = itemView.findViewById<TextView>(R.id.tv_trader_bid_initial_offer_price)
        val MY_BID = itemView.findViewById<TextView>(R.id.tv_trader_bid_my_bid)
        val CROP_LAST_UPDATED = itemView.findViewById<TextView>(R.id.tv_trader_bid_last_upadted)
        val CROP_DELTA = itemView.findViewById<TextView>(R.id.tv_my_crop_delta)
        val CROP_CHANGE = itemView.findViewById<ImageView>(R.id.iv_trader_bid_image)
        val CROP_CARD = itemView.findViewById<CardView>(R.id.cv_item_stock)

        val context = itemView.context
        val pref = PreferenceUtil

        //Bind a single item
        @SuppressLint("SetTextI18n")
        fun bindPost(mItem: BidHistoryResponse.Bid, itemClick: OnMyBidClickListenerGlobal) {
            with(mItem) {

                val _listItem = mItem.demand.get(0)
                //#Translation
                //create tranlation object
                try {
                    Log.e("Adap", "In item")
                    OfflineTranslate.translateToDefault(
                        itemView.context,
                        _listItem.crop,
                        CROP_NAME
                    )
                    OfflineTranslate.translateToDefault(
                        itemView.context,
                        _listItem.variety,
                        CROP_TYPE
                    )


                    //No Translations/Transliterations
                    CROP_QUANTITY.text = _listItem.qty.toString()
                    CROP_EXP.text = TimeConversionUtils.convertTimeToEpoch(_listItem.expiry)
                    CROP_CURRENT_BID.text = _listItem.currentBid.toString()
                    CROP_IOP.text = _listItem.offerPrice.toString()

                    MY_BID.text = _listItem.currentBid.toString()
                    CROP_LAST_UPDATED.text =
                        TimeConversionUtils.convertLastModified(_listItem.lastModified)

                    //If the bid is not active,
                    if (mItem.active) {
                        CROP_DELTA.text = itemView.context.getString(R.string.activeBid)
                        CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.deltaGreen))

                        if (currentBid != 0) {

                            val currentBid = _listItem.currentBid
                            val askBid = _listItem.offerPrice
                            val ans = askBid - currentBid


                            if (ans > 0) {

                                CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.deltaGreen))
                                CROP_CARD.setCardBackgroundColor(
                                    itemView.context.resources.getColor(
                                        R.color.lightGreenTest
                                    )
                                )

                            } else if (ans < 0) {

                                CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.deltaRed))
                                CROP_CARD.setCardBackgroundColor(
                                    itemView.context.resources.getColor(
                                        R.color.lightRedMono
                                    )
                                )
                                CROP_CURRENT_BID.setTextColor(itemView.context.resources.getColor(R.color.deltaRed))
                                CROP_IOP.setTextColor(itemView.context.resources.getColor(R.color.deltaRed))


                            } else if (ans == 0) {

                                CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.wildColor))
                                CROP_CARD.setCardBackgroundColor(
                                    itemView.context.resources.getColor(
                                        R.color.lightGreenTest
                                    )
                                )

                            }

                        } else {

                            CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.wildColor))
                            CROP_CARD.setCardBackgroundColor(itemView.context.resources.getColor(R.color.lightGreenTest))
                        }


                    } else {
                        CROP_DELTA.text = itemView.context.getString(R.string.inactiveBid)
                        CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.deltaRed))
                        CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.deltaRed))
                        CROP_CARD.setCardBackgroundColor(itemView.context.resources.getColor(R.color.cardOffWhite))

                    }


                } catch (e: Exception) {

                }
                itemView.setOnClickListener {
                    itemClick.viewMyBidDetails(mItem)
                }


            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bid, parent, false)
        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return lst.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bindPost(lst[position], itemClick)

    }

}


interface OnMyBidClickListenerGlobal {
    fun viewMyBidDetails(_listItem: BidHistoryResponse.Bid)
}


