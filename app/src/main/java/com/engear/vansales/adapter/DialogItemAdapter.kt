package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowDialogItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.UomData

class DialogItemAdapter(
    var context: Context,
    var orderList: MutableList<UomData>?
) : RecyclerView.Adapter<DialogItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowDialogItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mItemData = orderList!![position]
        holder.binding.item = mItemData
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}