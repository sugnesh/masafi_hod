package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowItemSpDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.ItemMonitoring

class ItemSalesAdapter(
    var context: Context,
    var orderList: List<ItemMonitoring.Data>?,
    var mType: String
) : RecyclerView.Adapter<ItemSalesAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowItemSpDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
        holder.binding.item = mOrderData

        if (mType == "itemwise") {
            holder.binding.tvOrderNo.text = "Item Code: " + mOrderData.item_id
            holder.binding.tvOrderDate.text = "Item Name: " + mOrderData.item_name
            holder.binding.tvAmount.text = "Total Amount: " + mOrderData.totalAmount + " AED"
        } else if (mType == "slow" || mType == "fast") {
            holder.binding.tvOrderNo.text = "Item Code: " + mOrderData.item_id
            holder.binding.tvOrderDate.text = "Item Name: " + mOrderData.item.item_name
            holder.binding.tvAmount.text = "Total Amount: " + mOrderData.itemTotal + " AED"
        }

        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowItemSpDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}