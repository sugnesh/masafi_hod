package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowInventoryExpiryBinding


class RowInventoryExpiryAdapter(
    var context: Context,
    var allList: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<RowInventoryExpiryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowInventoryExpiryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.tvUOM.text = mData["UOM"]
        holder.binding.tvQty.text = mData["Qty"]
        holder.binding.tvExpiry.text = mData["Expiry"]
        holder.binding.executePendingBindings()

    }

    inner class ViewHolder(val binding: RowInventoryExpiryBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}