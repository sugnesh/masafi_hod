package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowCompititorBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CompetitorInfo


class CompetitorInfoAdapter(
    var context: Context,
    var allList: ArrayList<CompetitorInfo>
) : RecyclerView.Adapter<CompetitorInfoAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: ArrayList<CompetitorInfo> = allList

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowCompititorBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = contactListFiltered[position]
        holder.binding.item = mData
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

    }


    inner class ViewHolder(val binding: RowCompititorBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = allList
                } else {
                    val filteredList: ArrayList<CompetitorInfo> =
                        ArrayList<CompetitorInfo>()
                    for (row in allList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.note!!.toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    contactListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                contactListFiltered = filterResults.values as ArrayList<CompetitorInfo>
                notifyDataSetChanged()
            }
        }
    }
}