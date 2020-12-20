package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.body.LanguageBody

class LanguagesAdapter(
    val itemClick: OnMyLanguageListener
) :
    RecyclerView.Adapter<LanguagesAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    var lst: List<LanguageBody> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate
        val LANGUAGE_NAME = itemView.findViewById<TextView>(R.id.tv_language)
        val CARD = itemView.findViewById<CardView>(R.id.cardLanguage)


        //Bind a single item
        fun bindPost(_listItem: LanguageBody, itemClick: OnMyLanguageListener) {
            with(_listItem) {


                LANGUAGE_NAME.text = _listItem.language

                itemView.setOnClickListener {

                    CARD.setCardBackgroundColor(itemView.context.resources.getColor(R.color.white))
                    itemClick.selectLanguage(_listItem)
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

        holder.bindPost(lst[position], itemClick)

    }


}


interface OnMyLanguageListener {
    fun selectLanguage(_listItem: LanguageBody)
}
