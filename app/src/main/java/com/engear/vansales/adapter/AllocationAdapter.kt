package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowAllocationItemBinding
import com.engear.vansales.databinding.RowCategorySpDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.BrandMonitoring
import com.engear.vansales.model.SalesRestrictData

class AllocationAdapter(
    var context: Context,
    var orderList: List<SalesRestrictData.RestrictItem>?
) : RecyclerView.Adapter<AllocationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowAllocationItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
        holder.binding.tvItemName.text = "Item Code: " + mOrderData.item_code
        holder.binding.tvItemPrice.text = "Allocated Qty: " + mOrderData.restrict_qty
        holder.binding.tvSales.text = "Sale Qty: " + mOrderData.sale_qty

        var mPending = mOrderData.restrict_qty.toDouble() - mOrderData.sale_qty.toDouble()
        holder.binding.tvPending.text = "Pending Qty: " + mPending.toString()

        holder.binding.executePendingBindings()

    }


    inner class ViewHolder(val binding: RowAllocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}