package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.DemandSuggestionObject

class DemandsSuggestionAdapterClass(val itemClick: mDemandSuggestionClickListener) :
    RecyclerView.Adapter<DemandsSuggestionAdapterClass.MyViewHolder>() {


    var lst: MutableList<DemandSuggestionObject> = mutableListOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // val NAME = itemView.findViewById<TextView>(R.id.tvSearch)
        val nCrop = itemView.findViewById<TextView>(R.id.tvItemDemandSuggestion)
        val iCrop = itemView.findViewById<ImageView>(R.id.ivItemDemandSuggestion)

        fun bindPost(
            _listItem: DemandSuggestionObject,
            position: Int,
            itemclick: mDemandSuggestionClickListener
        ) {

            nCrop.text = _listItem.nameOfCrop

            if (_listItem.drawable != null) {
                iCrop.setImageDrawable(_listItem.drawable)
            } else {
                iCrop.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.farmerx))
            }

            itemView.setOnClickListener {
                itemclick.clickDemandSuggestion(_listItem)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_suggestion_crop, parent, false)

        return DemandsSuggestionAdapterClass.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //Call from here
        holder.bindPost(lst[position], position, itemClick)
    }

    override fun getItemCount(): Int {
        return lst.size
    }


}

interface mDemandSuggestionClickListener {
    fun clickDemandSuggestion(_listItem: DemandSuggestionObject)
}


