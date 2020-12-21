package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.requirements.AddRequirementResponse
import com.example.mandiexe.utils.diffutils.DIffUtilsAddReq

/**
 * This will be a paged adapter
 */
class AddRequirementsAdapter(val itemClickListener: OnItemClickListenerAddReq) :

    PagedListAdapter<AddRequirementResponse, AddRequirementsAdapter.MyViewHolder>(DIffUtilsAddReq()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_requirement, parent, false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        getItem(position)?.let { holder.bindPost(it, itemClickListener) }
        holder.setIsRecyclable(false)

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Bind the fetched items with the view holding classes


        val CROP_NAME = itemView.findViewById<TextView>(R.id.tv_stock_crop_name)
        val CROP_LOCATION = itemView.findViewById<TextView>(R.id.tv_stock_crop_type)
        val CROP_QUANTITY = itemView.findViewById<TextView>(R.id.tv_stock_quantity)
        val CROP_EXP = itemView.findViewById<TextView>(R.id.tv_stock_expiration)
        val CROP_CURRENT_BID = itemView.findViewById<TextView>(R.id.tv_stock_current_bid)
        val CROP_IOP = itemView.findViewById<TextView>(R.id.tv_stock_initial_offer_price)
        val CROP_LAST_UPDATED = itemView.findViewById<TextView>(R.id.tv_stock_last_upadted)
        val CROP_DELTA = itemView.findViewById<TextView>(R.id.tv_my_req_delta)
        val CROP_CHANGE = itemView.findViewById<ImageView>(R.id.iv_req_image)


        fun bindPost(_listItem: AddRequirementResponse, clickListener: OnItemClickListenerAddReq) {


            itemView.setOnClickListener {
                clickListener.onItemClicked(_listItem)
            }
        }


    }


}


interface OnItemClickListenerAddReq {
    fun onItemClicked(_listItem: AddRequirementResponse)
}


