package com.example.mandiexe.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mandiexe.R
import com.example.mandiexe.models.responses.supply.CropSearchAutocompleteResponse
import java.util.*

class SuggestionsAdapter(val itemClick: mSearchViewOnSuggestionClick) :
    RecyclerView.Adapter<SuggestionsAdapter.MyViewHolder>(), Filterable {


    val lst: MutableList<CropSearchAutocompleteResponse.Suggestion> = mutableListOf()
    var filteredData: MutableList<String> = mutableListOf()

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

    //Add filter
    override fun getFilter(): Filter? {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResults = FilterResults()
                if (!TextUtils.isEmpty(constraint)) {

                    // Retrieve the autocomplete results.
                    val searchData: MutableList<String> = mutableListOf()
                    for (string in lst) {
                        if (string.name.toLowerCase(Locale.ROOT).startsWith(
                                constraint.toString().toLowerCase(
                                    Locale.ROOT
                                )
                            )
                        ) {
                            searchData.add(string.name)
                        }
                    }

                    // Assign the data to the FilterResults
                    filterResults.values = searchData
                    filterResults.count = searchData.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (results.values != null) {
                    filteredData = results.values as MutableList<String>
                    notifyDataSetChanged()
                }
            }
        }
    }


}

interface mSearchViewOnSuggestionClick {

}
