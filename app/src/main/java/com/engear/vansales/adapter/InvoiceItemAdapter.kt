package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowInvoiceAdapterItemBinding
import com.engear.vansales.databinding.RowOrderAdapterItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Invoice
import com.engear.vansales.model.Order

class InvoiceItemAdapter(
    var context: Context,
    var orderList: ArrayList<Invoice>?
) : RecyclerView.Adapter<InvoiceItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowInvoiceAdapterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
        holder.binding.item = mOrderData
        holder.binding.executePendingBindings()

        if (mOrderData!!.isStamp == "0") {
            holder.binding.tvStamp.text = "Stamp: No"
            holder.binding.ivInfoUploadStamp.visibility = View.GONE
        } else if (mOrderData!!.isStamp == "1") {
            holder.binding.tvStamp.text = "Stamp: Yes"
            holder.binding.ivInfoUploadStamp.visibility = View.GONE
        } else {
            holder.binding.tvStamp.text = "Stamp: Pending"
            holder.binding.ivInfoUploadStamp.visibility = View.VISIBLE
        }

        if (mOrderData!!.isInvoiceCancel == "0") {
            holder.binding.tvCancel.visibility = View.GONE
            holder.binding.imgCancel.visibility = View.VISIBLE
        } else {
            holder.binding.tvCancel.visibility = View.VISIBLE
            holder.binding.imgCancel.visibility = View.GONE
        }

        holder.binding.ivInfoUploadStamp.setOnClickListener {
            onItemClickListener.onItemsClick(position, "StampUpload")
        }

        holder.binding.imgCancel.setOnClickListener {
            onItemClickListener.onItemsClick(position, "InvoiceCancel")
        }
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
        holder.itemView.setOnLongClickListener {
            onItemClickListener.onItemsClick(position, "LongClick")
            true
        }

    }


    inner class ViewHolder(val binding: RowInvoiceAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}