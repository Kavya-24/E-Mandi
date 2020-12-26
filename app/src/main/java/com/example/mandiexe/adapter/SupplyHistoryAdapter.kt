package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.supply.SupplyHistoryResponse
import com.example.mandiexe.utils.ExternalUtils

class SupplyHistoryAdapter(val itemClick: OnMySupplyHistoryClickListener) :
    RecyclerView.Adapter<SupplyHistoryAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    var lst: List<SupplyHistoryResponse.Supply> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        //Use other items you want the layout to inflate
        val CROP_NAME = itemView.findViewById<TextView>(R.id.tv_supply_crop_name)
        val CROP_TYPE = itemView.findViewById<TextView>(R.id.tv_supply_crop_type)
        val CROP_QUANTITY = itemView.findViewById<TextView>(R.id.ans_crop_quanity_supply)
        val CROP_EXP = itemView.findViewById<TextView>(R.id.ans_crop_exp_supply)
        val CROP_CURRENT_BID = itemView.findViewById<TextView>(R.id.tv_supply_current_bid)
        val CROP_IOP = itemView.findViewById<TextView>(R.id.tv_supply_initial_offer_price)
        val CROP_LAST_UPDATED = itemView.findViewById<TextView>(R.id.tv_supply_last_upadted)
        val CROP_DELTA = itemView.findViewById<TextView>(R.id.tv_my_crop_active)
        val CROP_CHANGE = itemView.findViewById<ImageView>(R.id.iv_supply_image)


        //Bind a single item
        fun bindPost(
            _listItem: SupplyHistoryResponse.Supply,
            itemClick: OnMySupplyHistoryClickListener
        ) {
            with(_listItem) {

                CROP_NAME.text = _listItem.crop
                CROP_TYPE.text = _listItem.variety


                CROP_QUANTITY.text = _listItem.qty.toString()
                CROP_EXP.text = ExternalUtils.convertTimeToEpoch(_listItem.expiry)
                CROP_CURRENT_BID.text = _listItem.currentBid.toString()
                CROP_IOP.text = _listItem.askPrice.toString()
                CROP_LAST_UPDATED.text = ExternalUtils.convertTimeToEpoch(_listItem.lastModified)

                if (_listItem.active == true) {
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.green_A700))
                    CROP_DELTA.text = itemView.context.resources.getString(R.string.activeSupply)
                } else {
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.red_A700))
                    CROP_DELTA.text = itemView.context.resources.getString(R.string.inactiveSupply)
                }


                if (currentBid != 0) {

                    val currentBid = _listItem.currentBid
                    val askBid = _listItem.askPrice
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
                .inflate(R.layout.item_supply_history, parent, false)
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

