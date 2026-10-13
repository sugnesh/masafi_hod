package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowInvoiceSpHeaderBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.SaleMonitoring

class RouteSalesAdapter(
    var context: Context,
    var orderList: List<SaleMonitoring.Data>?
) : RecyclerView.Adapter<RouteSalesAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowInvoiceSpHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
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


    inner class ViewHolder(val binding: RowInvoiceSpHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}