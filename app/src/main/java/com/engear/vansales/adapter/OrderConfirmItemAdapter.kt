package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowOrderConfirmItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.ItemData

class OrderConfirmItemAdapter(
    var context: Context,
    var itemsList: MutableList<ItemData.Data>,
    var type: String
) : RecyclerView.Adapter<OrderConfirmItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowOrderConfirmItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mItemData = itemsList[position]
        holder.binding.item = mItemData
        if (mItemData.isFree == "" || mItemData.isFree == "0") {
            holder.binding.tvName.text = mItemData.itemName
        } else {
            holder.binding.tvName.text = mItemData.itemName + " (Free)"
        }
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

        if (mItemData.itemSelectUOM != null) {
            holder.binding.rvUomList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.binding.rvUomList.adapter = UomListAdapter(
                context,
                UomListAdapter.VIEW_TYPE_ONE,
                mItemData.itemSelectUOM!! as MutableList<Any>, type
            )
        }
    }


    inner class ViewHolder(val binding: RowOrderConfirmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}