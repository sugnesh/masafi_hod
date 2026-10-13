package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowEndroseItemBinding
import com.engear.vansales.model.InvoiceEndrose


class InvoiceEndroseAdapter(
    var allList: ArrayList<InvoiceEndrose.Data>
) : RecyclerView.Adapter<InvoiceEndroseAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowEndroseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.tvCustomerName.text = mData.user.firstname + " " + mData.user.lastname
        holder.binding.tvInvoceNo.text = mData.invoiceNumber
        holder.binding.tvNetSale.text = mData.totalNet
        holder.binding.tvTotalsale.text = mData.grandTotal

        if (mData.user.customerInfo != null) {
            holder.binding.tvcutomercode.text = mData.user.customerInfo!!.customer_code
        } else {
            holder.binding.tvcutomercode.text = mData.customerId.toString()
        }

        holder.binding.executePendingBindings()
    }


    inner class ViewHolder(val binding: RowEndroseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}