package com.example.mandiexe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.supply.CropSearchAutocompleteResponse

class SuggestionsAdapter(val itemClick: mSearchViewOnSuggestionClick) :
    RecyclerView.Adapter<SuggestionsAdapter.MyViewHolder>() {


    val lst: MutableList<CropSearchAutocompleteResponse.Suggestion> = mutableListOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // val NAME = itemView.findViewById<TextView>(R.id.tvSearch)
        val DEFAULT = itemView.findViewById<TextView>(R.id.tvSearchDefault)

        fun bindPost(
            _listItem: CropSearchAutocompleteResponse.Suggestion,
            position: Int,
            itemclick: mSearchViewOnSuggestionClick
        ) {

            com.example.mandiexe.utils.usables.OfflineTranslate.translateToDefault(
                itemView.context,
                _listItem.name,
                DEFAULT
            )
            // NAME.text = _listItem.name


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search, parent, false)

        return SuggestionsAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //Call from here
        holder.bindPost(lst[position], position, itemClick)
    }

    override fun getItemCount(): Int {
        return lst.size
    }

}

interface mSearchViewOnSuggestionClick {

}
