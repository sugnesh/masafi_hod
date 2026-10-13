package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowPromotionalBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Promotional


class PromotionalAdapter(
    var context: Context,
    var allList: ArrayList<Promotional>
) : RecyclerView.Adapter<PromotionalAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowPromotionalBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.item = mData
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

    }


    inner class ViewHolder(val binding: RowPromotionalBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}