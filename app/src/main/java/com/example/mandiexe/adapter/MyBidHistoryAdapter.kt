package com.example.mandiexe.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.body.BidHistoryBody
import com.example.mandiexe.utils.ApplicationUtils


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
        private val context = ApplicationUtils.getContext()
        private val permissionRequestCode = 1234
        private var mNumber = ""
        private lateinit var tempRef: androidx.appcompat.app.AlertDialog


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
                mNumber = _listItem.phone
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

                getPermissions()


            }


            d?.setPositiveButton(context.resources?.getString(R.string.call)) { _, _ ->

                getPermissions()

            }

            d?.create()
            tempRef = d?.create()!!
            d.show()

        }

        private fun makeCall() {

            try {


                val i = Intent(Intent.ACTION_CALL, Uri.parse(mNumber))
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ApplicationUtils.getContext().startActivity(i)

            } catch (e: ActivityNotFoundException) {

            }


        }

        private fun makeRequest() {
            ActivityCompat.requestPermissions(
                itemView.context as Activity,
                arrayOf(android.Manifest.permission.CALL_PHONE),
                permissionRequestCode
            )

        }

        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            // super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                permissionRequestCode ->
                    if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.canNotCall),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                    }

            }


        }

        private fun getPermissions() {

            tempRef.dismiss()

            Log.e("Bid history Adapter", "In get permissions")
            val p =
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)


            if (p == PackageManager.PERMISSION_GRANTED) {
                //Go to it
                makeCall()
            }

            if (p != PackageManager.PERMISSION_GRANTED) {
                //Not permitted
                Toast.makeText(context, "Permissions needed", Toast.LENGTH_SHORT).show()

            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    itemView.context as Activity,
                    android.Manifest.permission.CALL_PHONE
                )
            ) {
                //Tell user what we are going to do with this permission
                val b = AlertDialog.Builder(context)
                b.setMessage(context.resources.getString(R.string.permissionPhone))
                b.setTitle(context.resources.getString(R.string.permissionHead))
                b.setPositiveButton(context.resources.getString(R.string.ok)) { dialog: DialogInterface?, which: Int ->
                    makeRequest()
                }
                val dialog = b.create()
                dialog.show()
            } else {
                makeRequest()
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


