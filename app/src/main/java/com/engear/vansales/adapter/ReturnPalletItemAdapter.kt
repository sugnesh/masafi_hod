package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowAllocationItemBinding
import com.engear.vansales.databinding.RowCategorySpDetailBinding
import com.engear.vansales.databinding.RowPalletReturnItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.BrandMonitoring
import com.engear.vansales.model.PalletReturnData
import com.engear.vansales.model.SalesRestrictData

class ReturnPalletItemAdapter(
    var context: Context,
    var orderList: List<PalletReturnData.PalletReturn>?
) : RecyclerView.Adapter<ReturnPalletItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowPalletReturnItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
        holder.binding.tvItemName.text = "Item : " + mOrderData.item_name
        holder.binding.tvItemCode.text = "Item Code: " + mOrderData.item_code
        holder.binding.tvInvoiceNo.text = "Invoice No: " + mOrderData.invoice_number
        holder.binding.tvQty.text = "Sale Qty: " + mOrderData.item_qty

        if (mOrderData.enterQty.isEmpty()) {
            holder.binding.tvEnter.visibility = View.GONE
            holder.binding.tvEnter.text = "Enter Qty: "
        } else {
            holder.binding.tvEnter.visibility = View.VISIBLE
            holder.binding.tvEnter.text = "Enter Qty: " + mOrderData.enterQty
        }

        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowPalletReturnItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}