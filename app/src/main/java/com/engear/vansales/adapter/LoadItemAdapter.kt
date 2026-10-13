package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowItemLoadDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.SalesmanLoadDetail
import com.engear.vansales.utils.Utils
import java.util.*


class LoadItemAdapter(
    var context: Context,
    var loadItemList: MutableList<SalesmanLoadDetail>,
    var loadType: String,
    var isPerform: Boolean
) : RecyclerView.Adapter<LoadItemAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: MutableList<SalesmanLoadDetail> = loadItemList
    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemLoadDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mLoadData = contactListFiltered[position]
        holder.binding.item = mLoadData

//        if (mLoadData.enterQty.isEmpty()) {
//            holder.binding.lytDisplay!!.visibility = View.GONE
//            holder.binding.lytAppview!!.visibility = View.GONE
//        } else {
        holder.binding.lytDisplay!!.visibility = View.VISIBLE
        if (isPerform) {
            holder.binding.lytAppview!!.visibility = View.VISIBLE
        } else {
            holder.binding.lytAppview!!.visibility = View.GONE
        }
        // }

        if (loadType == "CarryOver Load") {
            holder.binding.tvreqstQty.visibility = View.GONE
            holder.binding.lytAppview.visibility = View.VISIBLE
        } else if (loadType == "Delivery Load") {
            holder.binding.tvreqstQty.visibility = View.GONE
            holder.binding.lytAppview.visibility = View.VISIBLE
        } else if (loadType == "Unload") {
            holder.binding.lytAppview.visibility = View.GONE
        }

        holder.binding.executePendingBindings()
        if (mLoadData.itemName != null) {
            if (mLoadData.itemName.isNotEmpty()) {
                val drawable = TextDrawable.builder()
                    .buildRound(
                        "S",
                        Utils.getMaterialColor(context, "300")
                    )

                holder.binding.imgCircle.setImageDrawable(drawable)
            } else {
                val drawable = TextDrawable.builder()
                    .buildRound(
                        "S",
                        Utils.getMaterialColor(context, "300")
                    )

                holder.binding.imgCircle.setImageDrawable(drawable)
            }

        }

        holder.itemView.setOnClickListener {
            if (loadType == "Van Load" || loadType == "Unload") {
                if (!isPerform) {
                    onItemClickListener.onItemsClick(position, "dialog")
                }
            }

        }

    }

    inner class ViewHolder(val binding: RowItemLoadDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = loadItemList
                } else {

                    var mSplit = charString.split(" ")

                    val filteredList: MutableList<SalesmanLoadDetail> =
                        ArrayList<SalesmanLoadDetail>()
                    for (row in loadItemList) {

                        var isContain = true
                        var name = row.itemCode + " " + row.itemName
                        for (mSpW in mSplit) {
                            if (!name.contains(mSpW, true)) {
                                isContain = false
                            }
                        }

                        if (isContain) {
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
                contactListFiltered = filterResults.values as ArrayList<SalesmanLoadDetail>
                notifyDataSetChanged()
            }
        }
    }
}