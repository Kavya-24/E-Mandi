package com.example.mandiexe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.body.LanguageBody
import com.example.mandiexe.utils.auth.PreferenceUtil

class LanguagesAdapter(
    val itemClick: OnMyLanguageListener
) :
    RecyclerView.Adapter<LanguagesAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    var lst: List<LanguageBody> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate

        val _language = itemView.findViewById<TextView>(R.id.tvLanguage)
        val _letter = itemView.findViewById<TextView>(R.id.tvLetter)
        val _csl = itemView.findViewById<ConstraintLayout>(R.id.csl_language)


        //The shared prefernece is empty first
        private val pref = PreferenceUtil

        //Bind a single item
        fun bindPost(_listItem: LanguageBody, itemClick: OnMyLanguageListener, position: Int) {
            with(_listItem) {

                _language.text = _listItem.language
                _letter.text = _listItem.mLetter


                if (_listItem.mLocale == pref.getLanguageFromPreference()) {
                    //Then add the button
                    Log.e("In language adapter", "With tick")
                    _csl.setBackgroundResource(R.drawable.card_language_glow)
                }

                //If this is notthe selected lagage
                else {

                    _csl.setBackgroundResource(R.drawable.card_language_neutral)

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
