package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowDistributionListBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Distribution


class DistributionAdapter(
    var context: Context,
    var allList: ArrayList<Distribution>
) : RecyclerView.Adapter<DistributionAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: MutableList<Distribution> = allList

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowDistributionListBinding.inflate(LayoutInflater.from(context), parent, false)
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


    inner class ViewHolder(val binding: RowDistributionListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = allList
                } else {
                    val filteredList: MutableList<Distribution> =
                        java.util.ArrayList<Distribution>()
                    for (row in allList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.name!!.toLowerCase().contains(charString.toLowerCase())) {
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
                contactListFiltered = filterResults.values as ArrayList<Distribution>
                notifyDataSetChanged()
            }
        }
    }

}