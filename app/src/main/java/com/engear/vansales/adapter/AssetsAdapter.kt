package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.engear.vansales.databinding.RowAssetsListBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Assets


class AssetsAdapter(
    var context: Context,
    var allList: ArrayList<Assets>
) : RecyclerView.Adapter<AssetsAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: MutableList<Assets> = allList

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowAssetsListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = contactListFiltered[position]
        holder.binding.item = mData
        Glide.with(context).load(mData.image)
            .into(holder.binding.profileImage)
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

    }

    inner class ViewHolder(val binding: RowAssetsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = allList
                } else {
                    val filteredList: MutableList<Assets> =
                        java.util.ArrayList<Assets>()
                    for (row in allList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.name!!.toLowerCase().contains(charString.toLowerCase()) ||
                            row.assetsCode!!.toLowerCase().contains(charString.toLowerCase())
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
                contactListFiltered = filterResults.values as ArrayList<Assets>
                notifyDataSetChanged()
            }
        }
    }

}