package com.example.mandiexe.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.body.BidHistoryBody


class MyBidHistoryAdapter(val itemClick: OnBidHistoryClickListener) :
    RecyclerView.Adapter<MyBidHistoryAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    //A bid list in ascending order
    var lst: List<BidHistoryBody> = listOf()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate
        val RANK = itemView.findViewById<TextView>(R.id.item_history_rank)
        val NAME = itemView.findViewById<TextView>(R.id.item_history_name)
        val INFO = itemView.findViewById<ImageView>(R.id.item_history_call)
        val AMOUNT = itemView.findViewById<TextView>(R.id.item_history_amount)


        //Bind a single item
        fun bindPost(
            _listItem: BidHistoryBody,
            itemClick: OnBidHistoryClickListener,
            position: Int
        ) {
            with(_listItem) {


                RANK.text = (position + 1).toString() + "."
                NAME.text = _listItem.name
                AMOUNT.text =
                    itemView.context.resources.getString(R.string.rs) + _listItem.amount.toString()

//                TIME.text = ExternalUtils.convertTimeToEpoch(
//                    _listItem.timestamp
//                ).toString()
                INFO.setOnClickListener {
                    createDialog(_listItem, itemView.context)
                }

                itemView.setOnClickListener {
                    itemClick.viewBidDetails(_listItem)
                }


            }
        }

        private fun createDialog(_listItem: BidHistoryBody, context: Context?) {

            val d = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
            val lI = LayoutInflater.from(context)
            val v = lI.inflate(R.layout.layout_bidder_detail, null)
            d?.setView(v)

            val name = v.findViewById<TextView>(R.id.tv_name_complete)
            val address = v.findViewById<TextView>(R.id.tv_address_complete)
            val number = v.findViewById<TextView>(R.id.tv_phone_number_completet)
            val call = v.findViewById<ImageView>(R.id.iv_call_bidder_complete)


            //Set the data
            name.text = _listItem.name
            address.text = _listItem.address
            number.text = _listItem.phone
            call.setOnClickListener {
                makeCall(_listItem.phone)
            }


            d?.setPositiveButton(context.resources?.getString(R.string.call)) { _, _ ->

                makeCall(_listItem.phone)

            }

            d?.create()
            d?.show()

        }

        private fun makeCall(phone: String) {


            try {
                val i = Intent(Intent.ACTION_CALL, Uri.parse(phone))
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
                itemView.context.startActivity(i)

            } catch (e: ActivityNotFoundException) {

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
    fun viewBidDetails(_listItem: BidHistoryBody)
}


