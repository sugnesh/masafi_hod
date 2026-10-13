package com.engear.vansales.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowOrderRequestAdapterItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnUnloadItemClickListener
import com.engear.vansales.model.ItemData
import com.engear.vansales.utils.Utils
import com.google.gson.Gson

class OrderRTCRequestItemAdapter(
    var context: Context,
    var itemsList: MutableList<ItemData.Data>
) : RecyclerView.Adapter<OrderRTCRequestItemAdapter.ViewHolder>(), Filterable {

    val onItemClickListener = context as OnUnloadItemClickListener

    private var contactListFiltered: MutableList<ItemData.Data> = itemsList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowOrderRequestAdapterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //val mItemData = contactListFiltered[holder.adapterPosition]
        //holder.binding.item = mItemData

        if (contactListFiltered[holder.adapterPosition].itemWeight == "1") {
            if (contactListFiltered[holder.adapterPosition].itemShellife == "1") {
                holder.binding.tvReSale!!.visibility = View.GONE
            } else {
                holder.binding.tvReSale!!.visibility = View.VISIBLE
            }
        } else {
            holder.binding.tvReSale!!.visibility = View.GONE
        }
        holder.binding.executePendingBindings()

        holder.binding.tvCode.text = contactListFiltered[holder.adapterPosition].itemCode
        holder.binding.tvName.text = contactListFiltered[holder.adapterPosition].itemName
        if (contactListFiltered[holder.adapterPosition].itemUOM!!.size != 0 && contactListFiltered[holder.adapterPosition].itemUOM!![0]!!.uomId != null) {
            holder.binding.tvBaseUomType.text =
                contactListFiltered[holder.adapterPosition].itemUOM[0]!!.uom!!.name
            holder.binding.tvBaseUomPrice.text =
                "Price: " + Utils.getCurrency(context!!) + " " + Utils.getCurrencyFormate(
                    context!!,
                    contactListFiltered[holder.adapterPosition].itemUOM[0]!!.uomPrice.toFloat()
                )
            holder.binding.llBaseUom.visibility = View.VISIBLE
        } else {
            holder.binding.llBaseUom.visibility = View.GONE
        }

        if (contactListFiltered[holder.adapterPosition].itemName != null) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    contactListFiltered[holder.adapterPosition].itemName[0].toString()
                        .toUpperCase(),
                    Utils.getMaterialColor(context, "300")
                )
            holder.binding.imgCircle.setImageDrawable(drawable)
        }


        holder.itemView.setOnClickListener {
            onItemClickListener.onUnloadItemsClick(
                contactListFiltered[holder.adapterPosition].id,
                "dialog",
                contactListFiltered[holder.adapterPosition].itemWeight
            )
        }

        holder.itemView.setOnLongClickListener {
            onItemClickListener.onUnloadItemsClick(
                contactListFiltered[holder.adapterPosition].id,
                "LongClick",
                contactListFiltered[holder.adapterPosition].itemWeight
            )
            true
        }

        if (contactListFiltered[holder.adapterPosition].itemSelectUOM != null && contactListFiltered[holder.adapterPosition].itemSelectUOM.isNotEmpty()) {
            holder.binding.llBaseUom.visibility = View.GONE
            holder.binding.rvUomList.visibility = View.VISIBLE
            holder.binding.rvUomList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.binding.rvUomList.adapter = UomListAdapter(
                context,
                UomListAdapter.VIEW_TYPE_ONE,
                contactListFiltered[holder.adapterPosition].itemSelectUOM!! as MutableList<Any>,
                ""
            )
        } else {
            holder.binding.llBaseUom.visibility = View.GONE
            holder.binding.rvUomList.visibility = View.GONE
        }

    }


    inner class ViewHolder(val binding: RowOrderRequestAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = itemsList
                } else {

                    var mSplit = charString.split(" ")

                    val filteredList: MutableList<ItemData.Data> = ArrayList()
                    for (row in itemsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
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
                contactListFiltered = filterResults.values as MutableList<ItemData.Data>
                notifyDataSetChanged()
            }
        }
    }
}