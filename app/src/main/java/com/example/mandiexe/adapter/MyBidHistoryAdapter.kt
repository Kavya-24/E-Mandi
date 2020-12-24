package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.supply.ViewSupplyResponse
import com.example.mandiexe.utils.ExternalUtils


class MyBidHistoryAdapter(val itemClick: OnBidHistoryClickListener) :
    RecyclerView.Adapter<MyBidHistoryAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    //A bid list in ascending order
    var lst: List<ViewSupplyResponse.Supply.Bid.BidDetails> = listOf()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate
        val RANK = itemView.findViewById<TextView>(R.id.item_history_rank)

        //val NAME = itemView.findViewById<TextView>(R.id.item_history_name)
        val TIME = itemView.findViewById<TextView>(R.id.item_history_time)
        val AMOUNT = itemView.findViewById<TextView>(R.id.item_history_amount)


        //Bind a single item
        fun bindPost(
            _listItem: ViewSupplyResponse.Supply.Bid.BidDetails,
            itemClick: OnBidHistoryClickListener,
            position: Int
        ) {
            with(_listItem) {


                RANK.text = (position + 1).toString()


                TIME.text = _listItem.amount.toString()

                AMOUNT.text = ExternalUtils.convertTimeToEpoch(_listItem.timestamp).toString()


                itemView.setOnClickListener {
                    itemClick.viewBidDetails(_listItem)
                }


            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bid_history, parent, false)
        return MyBidHistoryAdapter.MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return lst.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bindPost(lst[position], itemClick, position)

    }

}


interface OnBidHistoryClickListener {
    fun viewBidDetails(_listItem: ViewSupplyResponse.Supply.Bid.BidDetails)
}


