package com.example.mandiexe.adapter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.supply.SearchGlobalCropResponse
import com.example.mandiexe.utils.usables.UIUtils


class YoutubeAdapter(val itemClick: OnYoutubeClickListener) :
    RecyclerView.Adapter<YoutubeAdapter.MyViewHolder>() {


    var lst: List<SearchGlobalCropResponse.Link> = mutableListOf()
    var ctype = ""

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val HEADING = itemView.findViewById<TextView>(R.id.tvHeadingLink)!!
        val URL = itemView.findViewById<TextView>(R.id.tvURLLink)!!
        val iv = itemView.findViewById<ImageView>(R.id.ivyt)!!


        fun bindPost(
            _listItem: SearchGlobalCropResponse.Link,
            position: Int,
            itemclick: OnYoutubeClickListener,
            ctype: String
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


            //Get the image if possible
//            populateImage(ctype)
            iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_launch_black_24dp))

            URL.movementMethod = LinkMovementMethod.getInstance()

            URL.setOnClickListener {
                watchYoutubeVideo(_listItem.url)
            }

            itemView.setOnClickListener {
                watchYoutubeVideo(_listItem.url)
            }

        }

        private fun populateImage(ctype: String) {

            val c_name = ctype

            if (ctype.isNotBlank() && ctype.isNotEmpty()) {
                //Parse the c_name
                val check_name = c_name.toLowerCase().filter { !it.isWhitespace() }


                val check_uri =
                    "@drawable/" + check_name // where myresource (without the extension) is the file

                Log.e("LinkAdapter", "Checking for image name $check_name and URI $check_uri")


                val imageResource =
                    itemView.context.resources.getIdentifier(
                        check_uri,
                        null,
                        itemView.context.packageName
                    )

                val res = itemView.context.resources.getDrawable(imageResource)

                try {

                    Log.e("Link Adapter", "Setting org img")
                    iv.setImageDrawable(res)


                } catch (e: Exception) {
                    UIUtils.logExceptions(e, "LinkAdapter")
                    iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_launch_black_24dp))
                }


            } else {
                iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_launch_black_24dp))
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
        holder.bindPost(lst[position], position, itemClick, ctype)
    }

    override fun getItemCount(): Int {
        return lst.size
    }

}

interface OnYoutubeClickListener
