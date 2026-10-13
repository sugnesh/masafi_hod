package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowItemSpLoadDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.ItemSPMonitoring

class ItemUnLoadAdapter(
    var context: Context,
    var orderList: List<ItemSPMonitoring.Data>?,
    var mType: String
) : RecyclerView.Adapter<ItemUnLoadAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowItemSpLoadDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]

        holder.binding.tvOrderNo.text = "Item Code: " + mOrderData.item.itemCode
        holder.binding.tvOrderDate.text = "Item Name: " + mOrderData.item.item_name
        holder.binding.tvAmount.text =
            "Qty: " + mOrderData.unload_qty + " " + mOrderData.item_uom.name

        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowItemSpLoadDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}