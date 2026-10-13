package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowInvoiceItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CreditRouteMonitoring

class SupervisorReturnItemAdapter(
    var context: Context,
    var orderList: List<CreditRouteMonitoring.Data.CreditNoteDetail>
) : RecyclerView.Adapter<SupervisorReturnItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowInvoiceItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
//        holder.binding.item = mOrderData
//        holder.binding.executePendingBindings()
        holder.binding.tvItemName.text  = ""+orderList[position].item.itemName
        holder.binding.tvItemPrice.text  = "Qty:  "+orderList[position].itemQty

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowInvoiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}