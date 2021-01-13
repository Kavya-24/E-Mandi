package com.example.mandiexe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.utils.auth.PreferenceUtil
import com.google.android.material.button.MaterialButton

class LanguagesAdapter(
    val itemClick: OnMyLanguageListener
) :
    RecyclerView.Adapter<LanguagesAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    var lst: List<LanguageBody> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate
        val CARD = itemView.findViewById<MaterialButton>(R.id.mtb_item_language)

        //The shared prefernece is empty first
        private val pref = PreferenceUtil

        //Bind a single item
        fun bindPost(_listItem: LanguageBody, itemClick: OnMyLanguageListener, position: Int) {
            with(_listItem) {

                CARD.text = _listItem.language

                Log.e(
                    "LA",
                    "In bind post and langugae us " + _listItem.mLocale + " pref is " + pref.getLanguageFromPreference()
                )
                if (_listItem.mLocale == pref.getLanguageFromPreference()) {
                    //Then add the button
                    Log.e("In language adapter", "With tick")
                    CARD.icon =
                        itemView.context.resources.getDrawable(R.drawable.ic_check_black_24dp)

                }

                itemView.setOnClickListener {


                    itemClick.selectLanguage(_listItem, position)
                }


            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_language, parent, false)
        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return lst.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bindPost(lst[position], itemClick, position)

    }


}


interface OnMyLanguageListener {
    fun selectLanguage(_listItem: LanguageBody, position: Int)


}
