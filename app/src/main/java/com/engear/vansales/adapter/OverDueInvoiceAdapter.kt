package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowOverdueInvoiceBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.OverDueInvoice
import com.engear.vansales.utils.Utils


class OverDueInvoiceAdapter(
    var context: Context,
    var allList: ArrayList<OverDueInvoice>
) : RecyclerView.Adapter<OverDueInvoiceAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowOverdueInvoiceBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.item = mData
        holder.binding.tvAmtDue.text =
            "Amount Due: " + Utils.getCurrency(context!!) + Utils.getCurrencyFormate(
                context!!,
                mData.amountDue!!.toFloat()
            )
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

    }


    inner class ViewHolder(val binding: RowOverdueInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}