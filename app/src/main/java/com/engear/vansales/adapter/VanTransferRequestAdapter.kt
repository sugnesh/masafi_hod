package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.RowItemVanDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.VanTransferItemListData
import com.engear.vansales.utils.Utils
import kotlin.collections.ArrayList


class VanTransferRequestAdapter(
    var context: Context,
    var allLoaList: ArrayList<VanTransferItemListData.DataItemVan>,
    var isPeformClick: Boolean

) : RecyclerView.Adapter<VanTransferRequestAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: MutableList<VanTransferItemListData.DataItemVan> =
        allLoaList
    val onItemClickListener = context as OnItemClickListener
    var mPreferenceManager: SharedPreferenceManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemVanDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mVanData = contactListFiltered[position]
        holder.binding.item = mVanData
        holder.binding.executePendingBindings()

        mPreferenceManager = SharedPreferenceManager(context)

        try {
            if (mVanData.item!!.enterQty.isEmpty()) {
                holder.binding.lytDisplay!!.visibility = View.GONE
            } else {
                holder.binding.lytDisplay!!.visibility = View.VISIBLE
            }
        } catch (e: Exception) {

        }


        if (mVanData.item!!.itemCode != null) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    mVanData.item!!.item_name[0].toString().toUpperCase(),
                    Utils.getMaterialColor(context, "300")
                )
            holder.binding.imgCircle.setImageDrawable(drawable)

        }

        holder.itemView.setOnClickListener {
            if (!isPeformClick) {
                onItemClickListener.onItemsClick(position, "dialog")
            }
        }


    }

    inner class ViewHolder(val binding: RowItemVanDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = allLoaList
                } else {

                    var mSplit = charString.split(" ")

                    val filteredList: MutableList<VanTransferItemListData.DataItemVan> =
                        java.util.ArrayList<VanTransferItemListData.DataItemVan>()
                    for (row in allLoaList) {

                        var isContain = true
                        var name = row.item!!.itemCode + " " + row.item!!.item_name
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
                contactListFiltered =
                    filterResults.values as java.util.ArrayList<VanTransferItemListData.DataItemVan>
                notifyDataSetChanged()
            }
        }
    }


}