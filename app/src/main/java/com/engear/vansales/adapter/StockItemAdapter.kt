package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowStockAdapterItemBinding
import com.engear.vansales.model.StockData
import com.engear.vansales.utils.Utils

class StockItemAdapter(
    var context: Context,
    var itemsList: List<StockData.Data.StockList>
) : RecyclerView.Adapter<StockItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowStockAdapterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mItemData = itemsList[position]
        holder.binding.itemData = mItemData
        holder.binding.executePendingBindings()

        if (mItemData.itemDetail.name != null) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    mItemData.itemDetail.name[0].toString().toUpperCase(),
                    Utils.getMaterialColor(context, "300")
                )
            holder.binding.imgCircle.setImageDrawable(drawable)
        }

    }


    inner class ViewHolder(val binding: RowStockAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}