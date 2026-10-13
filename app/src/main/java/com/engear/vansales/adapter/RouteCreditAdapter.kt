package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowCreditSpHeaderBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CreditRouteMonitoring
import com.engear.vansales.model.SaleMonitoring
import com.engear.vansales.model.SaleRouteMonitoring
import com.engear.vansales.utils.Utils

class RouteCreditAdapter(
    var context: Context,
    var orderList: List<SaleMonitoring.Data>?
) : RecyclerView.Adapter<RouteCreditAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowCreditSpHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
        holder.binding.item = mOrderData

        holder.binding.tvAmount.text = "Credit Amount : AED " + Utils.getCurrencyFormate(context, mOrderData.totalAmount.toFloat())

        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowCreditSpHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}