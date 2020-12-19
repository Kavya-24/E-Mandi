package com.example.mandiexe.adapter


/*
class MyBidsAdapter(val itemClick: OnItemClickListener) :
    RecyclerView.Adapter<MyBidsAdapter.MyViewHolder>() {


    //Initialize an empty list of the dataclass T
    //var lst: List<T> = listOf()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Use other items you want the layout to inflate
        val CROP_NAME = itemView.findViewById<TextView>(R.id.tv_stock_crop_name)
        val CROP_TYPE = itemView.findViewById<TextView>(R.id.tv_stock_crop_type)
        val CROP_QUANTITY = itemView.findViewById<TextView>(R.id.tv_stock_quantity)
        val CROP_EXP = itemView.findViewById<TextView>(R.id.tv_stock_expiration)
        val CROP_CURRENT_BID = itemView.findViewById<TextView>(R.id.tv_stock_current_bid)
        val CROP_IOP = itemView.findViewById<TextView>(R.id.tv_stock_initial_offer_price)
        val CROP_LAST_UPDATED = itemView.findViewById<TextView>(R.id.tv_stock_last_upadted)


        //Bind a single item
        fun bindPost(_listItem: T, itemClick: OnItemClickListener) {
            with(_listItem) {


                NAME.text =
                    _listItem.(//Add the paramter from list object-data-class you want to add)
                            )

                itemView.setOnClickListener {
                    itemClick.clickThisItem(_listItem)
                }


            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stock, parent, false)
        return MyBidsAdapter.MyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return lst.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bindPost(lst[position], itemClick)

    }

}


interface OnItemClickListener {
    fun clickThisItem(_listItem: T)
}

*/
