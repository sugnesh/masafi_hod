package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowCategorySpDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CategoryMonitoring

class CategorySalesAdapter(
    var context: Context,
    var orderList: List<CategoryMonitoring.Data>?
) : RecyclerView.Adapter<CategorySalesAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowCategorySpDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]

        holder.binding.tvOrderDate.text = "Category: " + mOrderData.cat_name
        holder.binding.tvAmount.text = "Total Amount: " + mOrderData.totalAmount + " AED"

        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowCategorySpDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}