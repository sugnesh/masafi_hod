package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowItemVanstockBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.Item
import com.engear.vansales.utils.Utils


class VanStockAdapter(
    var context: Context,
    var allItemList: ArrayList<Item>,
    itemClickListener: OnItemClickListener?
) : RecyclerView.Adapter<VanStockAdapter.ViewHolder>(), Filterable {
    val onItemClickListener:OnItemClickListener = itemClickListener!!


    private var contactListFiltered: MutableList<Item> = allItemList
    var db: DatabaseHandler? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemVanstockBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mItem = contactListFiltered[position]
        holder.binding.item = mItem
        db = DatabaseHandler(context!!)

        if (mItem.itemName != null) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    "S",
                    Utils.getMaterialColor(context, "300")
                )

            holder.binding.imgCircle.setImageDrawable(drawable)
        }


        if (mItem.itemUOMId.toString() == mItem.itemDisplayUnitId.toString()) {
            holder.binding.tvDisplayUOM.text =
                mItem.itemDisplayUnitName.toString() + " : " + mItem.itemQty!!
        } else {
            var qty = Utils.getUOMQty(mItem.itemDisplayUPC!!, mItem.itemQty!!).toDouble()

            holder.binding.tvDisplayUOM.text =
                mItem.itemDisplayUnitName.toString() + " : " + qty

//            var displayQty = qty.toInt()
//            var totalBaseQty = displayQty * mItem.itemDisplayUPC!!.toDouble()
//
//            if (totalBaseQty == mItem.itemQty!!.toDouble()) {
//                holder.binding.tvDisplayUOM.text =
//                    mItem.itemDisplayUnitName.toString() + " : " + displayQty
//            } else {
//                var remain = mItem.itemQty!!.toDouble() - totalBaseQty
//                holder.binding.tvDisplayUOM.text =
//                    mItem.itemDisplayUnitName.toString() + " : " + displayQty + " | " + mItem.itemUOMName + " : " + remain
//            }
        }
    /*    System.out.println("Check--> "+db!!.getAllVanBatchItemByID(mItemID = mItem.itemId.toString()))*/


        if (mItem.itemIsReSale == "1") {
            holder.binding.tvBatch.visibility = View.VISIBLE
        } else {
            holder.binding.tvBatch.visibility = View.GONE
        }

      /*  holder.binding.ivInfoBatch.setOnClickListener{
            onItemClickListener!!.onItemsClick(
                contactListFiltered[holder.adapterPosition].itemId!!.toInt(),
                "batchdetails"
            )
        }*/

        holder.binding.executePendingBindings()

    }

    inner class ViewHolder(val binding: RowItemVanstockBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = allItemList
                } else {

                    var mSplit = charString.split(" ")

                    val filteredList: MutableList<Item> =
                        java.util.ArrayList<Item>()
                    for (row in allItemList) {

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
                contactListFiltered = filterResults.values as java.util.ArrayList<Item>
                notifyDataSetChanged()
            }
        }
    }
}