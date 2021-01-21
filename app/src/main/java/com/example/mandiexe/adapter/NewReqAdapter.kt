package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.SearchCropReqResponse
import com.example.mandiexe.utils.usables.TimeConversionUtils

class NewReqAdapter(val itemClick: OnNewReqClockListener) :
    RecyclerView.Adapter<NewReqAdapter.MyViewHolder>() {


    var lst: List<SearchCropReqResponse.Supply> = listOf()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate
        val CROP_NAME = itemView.findViewById<TextView>(R.id.tv_add_requirement_crop_name)
        val CROP_LOCATION = itemView.findViewById<TextView>(R.id.tv_add_requirement_crop_location)
        val CROP_QUANTITY = itemView.findViewById<TextView>(R.id.tv_add_requirement_quantity)
        val CROP_EXP = itemView.findViewById<TextView>(R.id.tv_add_requirement_expiration)
        val CROP_CURRENT_BID = itemView.findViewById<TextView>(R.id.tv_add_requirement_current_bid)
        val CROP_IOP = itemView.findViewById<TextView>(R.id.tv_add_requirement_initial_offer_price)

        val CROP_LAST_UPDATED =
            itemView.findViewById<TextView>(R.id.tv_add_requirement_last_upadted)
        val CROP_DELTA = itemView.findViewById<TextView>(R.id.tv_my_req_delta)
        val CROP_CHANGE = itemView.findViewById<ImageView>(R.id.iv_req_image)


        //Bind a single item
        fun bindPost(
            _listItem: SearchCropReqResponse.Supply,
            itemClick: OnNewReqClockListener,
            position: Int
        ) {
            with(_listItem) {

                CROP_NAME.text = _listItem.crop

                CROP_QUANTITY.text = _listItem.qty.toString()
                CROP_EXP.text = TimeConversionUtils.convertTimeToEpoch(_listItem.expiry)

                CROP_CURRENT_BID.text = _listItem.lastBid.toString()
                CROP_IOP.text = _listItem.askPrice.toString()


                CROP_LAST_UPDATED.text =
                    TimeConversionUtils.convertTimeToEpoch(_listItem.lastModified)


                val currentBid = _listItem.lastBid.get(0).amount
                val askBid = _listItem.askPrice
                val ans = currentBid - askBid

                if (ans > 0) {
                    CROP_DELTA.text = "+" + ans.toString()
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.green_A700))
                    CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.green_A700))
                } else if (ans < 0) {
                    CROP_DELTA.text = ans.toString()
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.red_A700))
                    CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.red_A700))

                } else if (ans == 0) {
                    CROP_DELTA.text = ans.toString()
                    CROP_DELTA.setTextColor(itemView.context.resources.getColor(R.color.blue_A700))
                    CROP_CHANGE.drawable.setTint(itemView.context.resources.getColor(R.color.blue_A700))

                }


                itemView.setOnClickListener {
                    itemClick.viewAddReqDetails(_listItem)
                }


            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewReqAdapter.MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_requirement, parent, false)
        return NewReqAdapter.MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return lst.size

    }

    override fun onBindViewHolder(holder: NewReqAdapter.MyViewHolder, position: Int) {

        holder.bindPost(lst[position], itemClick, position)

    }


}

interface OnNewReqClockListener {
    fun viewAddReqDetails(_listItem: SearchCropReqResponse.Supply)
}
