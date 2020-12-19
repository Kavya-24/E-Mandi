package com.example.mandiexe.adapter

/*
class MyBidHistory(val itemClick: OnBidHistoryClickListener) :
    RecyclerView.Adapter<MyBidHistory.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    cvar lst: List<T> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate
        val RANK = itemView.findViewById<TextView>(R.id.item_history_rank)
        val NAME = itemView.findViewById<TextView>(R.id.item_history_name)
        val TIME = itemView.findViewById<TextView>(R.id.item_history_time)
        val AMOUNT = itemView.findViewById<TextView>(R.id.item_history_amount)


        //Bind a single item
        fun bindPost(_listItem: T, itemClick: OnItemClickListener) {
            with(_listItem) {


                RANK.text =
                    _listItem.(//Add the paramter from list object-data-class you want to add)
                            )
                NAME.text = _listItem.()

                TIME.text = _listItem.()

                AMOUNT.text = _listItem.()


                itemView.setOnClickListener {
                    itemClick.clickThisItem(_listItem)
                }


            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bid_history, parent, false)
        return MyBidHistory.MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return lst.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bindPost(lst[position], itemClick)

    }

}


interface OnBidHistoryClickListener {
    fun viewBidDetails(_listItem: T)
}

*/
