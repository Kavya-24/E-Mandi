package com.example.mandiexe.adapter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.supply.SearchGlobalCropResponse


class YoutubeAdapter(val itemClick: OnYoutubeClickListener) :
    RecyclerView.Adapter<YoutubeAdapter.MyViewHolder>() {


    var lst: List<SearchGlobalCropResponse.Link> = mutableListOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val HEADING = itemView.findViewById<TextView>(R.id.tvHeadingLink)!!
        val URL = itemView.findViewById<TextView>(R.id.tvURLLink)!!

        fun bindPost(
            _listItem: SearchGlobalCropResponse.Link,
            position: Int,
            itemclick: OnYoutubeClickListener
        ) {

            Log.e("YT", "In adapter set")
            com.example.mandiexe.utils.usables.OfflineTranslate.translateToDefault(
                itemView.context,
                _listItem.heading,
                HEADING
            )

            //The link will not be transklated
            URL.text = _listItem.url
            //Set the text movement here


            URL.movementMethod = LinkMovementMethod.getInstance()

            URL.setOnClickListener {
                watchYoutubeVideo(_listItem.url)
            }

            itemView.setOnClickListener {
                watchYoutubeVideo(_listItem.url)
            }

        }

        private fun watchYoutubeVideo(id: String) {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://$id")
            )
            try {
                itemView.context.startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                itemView.context.startActivity(webIntent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_link_crop_file, parent, false)

        return YoutubeAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //Call from here
        holder.bindPost(lst[position], position, itemClick)
    }

    override fun getItemCount(): Int {
        return lst.size
    }

}

interface OnYoutubeClickListener {

}
