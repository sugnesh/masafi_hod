package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowOrderAdapterItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Order

class OrderItemAdapter(
    var context: Context,
    var orderList: ArrayList<Order>?
) : RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowOrderAdapterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
        holder.binding.item = mOrderData
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowOrderAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}