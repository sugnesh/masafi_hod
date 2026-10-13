package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowItemFreshunloadDetailBinding
import com.engear.vansales.databinding.RowItemLoadDetailBinding
import com.engear.vansales.databinding.RowItemUnloadDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.SalesmanLoadDetail
import com.engear.vansales.utils.Utils
import java.util.*


class LoadULItemNewAdapter(
    var context: Context,
    var loadItemList: MutableList<SalesmanLoadDetail>,
    var loadType: String,
) : RecyclerView.Adapter<LoadULItemNewAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: MutableList<SalesmanLoadDetail> = loadItemList
    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowItemFreshunloadDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mLoadData = contactListFiltered[position]
        holder.binding.item = mLoadData



        if (loadType == "Fresh") {
            if (mLoadData.primary_qty == "1") {
                holder.binding.tvSystemQty!!.text =
                    "System Qty : " + mLoadData.uomQty + " " + mLoadData.uomName
            } else {
                holder.binding.tvSystemQty!!.text =
                    "System Qty : " + mLoadData.uomQty + " " + mLoadData.displayUOMName
            }
            //Enter QTY
            if (mLoadData.enterQty.isEmpty()) {
                holder.binding.lytDisplay!!.visibility = View.GONE
                holder.binding.lytAppview!!.visibility = View.GONE
            } else {
                holder.binding.lytDisplay!!.visibility = View.GONE
                holder.binding.lytAppview!!.visibility = View.VISIBLE
//                if (mLoadData.primary_qty == "1") {
//                    holder.binding.tvapprvQty.text =
//                        "Fresh Unload Qty : " + mLoadData.enterQty + " " + mLoadData.uomName
//                    holder.binding.tvSystemQty!!.text =
//                        "System Qty : " + mLoadData.uomQty + " " + mLoadData.uomName
//                } else {
                    holder.binding.tvapprvQty.text =
                        "Fresh Unload Qty : " + mLoadData.enterQty + " " + mLoadData.displayUOMName
                    holder.binding.tvSystemQty!!.text =
                        "System Qty : " + mLoadData.uomQty + " " + mLoadData.displayUOMName
              //  }

            }
        } else {
            holder.binding.lytDisplay!!.visibility = View.VISIBLE
            holder.binding.lytAppview!!.visibility = View.GONE
            if (mLoadData.primary_qty == "1") {
                holder.binding.tvDisplayUOM.text =
                    "Carryover Qty : " + mLoadData.enterQty + " " + mLoadData.uomName
                holder.binding.tvSystemQty!!.text =
                    "System Qty : " + mLoadData.uomQty + " " + mLoadData.uomName
            } else {
                holder.binding.tvDisplayUOM.text =
                    "Carryover Qty : " + mLoadData.enterQty + " " + mLoadData.displayUOMName
                holder.binding.tvSystemQty!!.text =
                    "System Qty : " + mLoadData.uomQty + " " + mLoadData.displayUOMName
            }
        }


        holder.binding.executePendingBindings()
        if (mLoadData.itemName != null) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    mLoadData.itemName[0].toString().toUpperCase(),
                    Utils.getMaterialColor(context, "300")
                )

            holder.binding.imgCircle.setImageDrawable(drawable)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")

        }

    }

    inner class ViewHolder(val binding: RowItemFreshunloadDetailBinding) :
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