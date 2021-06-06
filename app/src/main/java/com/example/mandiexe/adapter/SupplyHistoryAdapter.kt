package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.supply.SupplyHistoryResponse
import com.example.mandiexe.utils.usables.OfflineTranslate
import com.example.mandiexe.utils.usables.TimeConversionUtils

class SupplyHistoryAdapter(val itemClick: OnMySupplyHistoryClickListener) :
    RecyclerView.Adapter<SupplyHistoryAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    var lst: List<SupplyHistoryResponse.Supply> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        //Use other items you want the layout to inflate
        val CROP_NAME = itemView.findViewById<TextView>(R.id.tv_stock_crop_name)
        val CROP_TYPE = itemView.findViewById<TextView>(R.id.tv_stock_crop_type)
        val CROP_QUANTITY = itemView.findViewById<TextView>(R.id.ans_crop_quanity)
        val CROP_EXP = itemView.findViewById<TextView>(R.id.ans_crop_exp)
        val CROP_CURRENT_BID = itemView.findViewById<TextView>(R.id.tv_stock_current_bid)
        val CROP_IOP = itemView.findViewById<TextView>(R.id.tv_stock_initial_offer_price)
        val CROP_LAST_UPDATED = itemView.findViewById<TextView>(R.id.tv_stock_last_upadted)
        val CROP_DELTA = itemView.findViewById<TextView>(R.id.tv_my_crop_delta)
        val CROP_CHANGE = itemView.findViewById<ImageView>(R.id.iv_stock_image)
        val CROP_CARD = itemView.findViewById<CardView>(R.id.cv_item_stock)
        val TV_LAST_BIS = itemView.findViewById<TextView>(R.id.mCurrentBid)

        //Bind a single item
        fun bindPost(
            _listItem: SupplyHistoryResponse.Supply,
            itemClick: OnMySupplyHistoryClickListener
        ) {
            with(_listItem) {


                OfflineTranslate.translateToDefault(itemView.context, _listItem.crop, CROP_NAME)
                OfflineTranslate.translateToDefault(itemView.context, _listItem.variety, CROP_TYPE)


                CROP_QUANTITY.text =
                    _listItem.qty.toString() + " " + itemView.context.resources.getString(R.string.kg)
                CROP_EXP.text = TimeConversionUtils.convertTimeToEpoch(_listItem.expiry)

                CROP_CURRENT_BID.text = _listItem.currentBid.toString()
                TV_LAST_BIS.text = itemView.context.resources.getString(R.string.lastPrice)
                CROP_IOP.text = _listItem.askPrice.toString()
                CROP_LAST_UPDATED.text =
                    TimeConversionUtils.convertLastModified(_listItem.lastModified)

                if (_listItem.active) {

                    CROP_DELTA.text = itemView.context.resources.getString(R.string.activeSupply)
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.deltaGreen))
                    //If stock is active show the icon and make it greem
                    if (currentBid != 0) {

                        val currentBid = _listItem.currentBid
                        val askBid = _listItem.askPrice
                        val ans = currentBid - askBid


                        if (ans > 0) {

                            CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.deltaGreen))
                            CROP_CARD.setCardBackgroundColor(itemView.context.resources.getColor(R.color.lightGreenTest))

                        } else if (ans < 0) {

                            CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.deltaRed))
                            CROP_CARD.setCardBackgroundColor(itemView.context.resources.getColor(R.color.lightRedMono))
                            CROP_CURRENT_BID.setTextColor(itemView.context.resources.getColor(R.color.deltaRed))
                            CROP_IOP.setTextColor(itemView.context.resources.getColor(R.color.deltaRed))


                        } else if (ans == 0) {

                            CROP_DELTA.text = ans.toString()
                            CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.wildColor))
                            CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.wildColor))
                            CROP_CARD.setCardBackgroundColor(itemView.context.resources.getColor(R.color.lightGreenTest))

                        }

                    } else {
                        CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.wildColor))
                        CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.wildColor))
                        CROP_CARD.setCardBackgroundColor(itemView.context.resources.getColor(R.color.lightGreenTest))
                    }

                } else {
                    CROP_DELTA.text = itemView.context.resources.getString(R.string.inactiveSupply)
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.deltaRed))
                    CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.wildColor))
                    //rEMOVE THE Image
                   // CROP_CHANGE.visibility = View.GONE
                    CROP_CARD.setCardBackgroundColor(itemView.context.resources.getColor(R.color.cardOffWhite))

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
                .inflate(R.layout.item_supply, parent, false)
        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return lst.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bindPost(lst[position], itemClick)

    }

}


interface OnMySupplyHistoryClickListener {
    fun viewMyStockDetails(_listItem: SupplyHistoryResponse.Supply)
}


