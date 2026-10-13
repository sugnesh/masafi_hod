package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowLoadOrderAdapterItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Order

class LoadOrderItemAdapter(
    var context: Context,
    var orderList: ArrayList<Order>?
) : RecyclerView.Adapter<LoadOrderItemAdapter.ViewHolder>(), Filterable {

    private var orderListFiltered: ArrayList<Order>? = orderList
    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowLoadOrderAdapterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderListFiltered!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderListFiltered!![position]
        holder.binding.item = mOrderData
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowLoadOrderAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    orderListFiltered = orderList
                } else {
                    val filteredList: ArrayList<Order> =
                        ArrayList<Order>()
                    for (row in orderList!!) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.orderNo!!.toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    orderListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = orderListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                orderListFiltered = filterResults.values as ArrayList<Order>
                notifyDataSetChanged()
            }
        }
    }
}