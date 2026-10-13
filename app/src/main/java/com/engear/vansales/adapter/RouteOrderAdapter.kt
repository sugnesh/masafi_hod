package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowInvoiceSpHeaderBinding
import com.engear.vansales.databinding.RowOrderAdapterItemBinding
import com.engear.vansales.databinding.RowOrderSpHeaderBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Order
import com.engear.vansales.model.OrderRouteMonitoring
import com.engear.vansales.model.SaleRouteMonitoring
import com.engear.vansales.utils.Utils

class RouteOrderAdapter(
    var context: Context,
    var orderList: List<OrderRouteMonitoring.Data>
) : RecyclerView.Adapter<RouteOrderAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowOrderSpHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
        holder.binding.item = mOrderData

        holder.binding.tvAmount.text = "Order Amount : AED " + Utils.getCurrencyFormate(context, mOrderData.grandTotal.toFloat())

        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowOrderSpHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}