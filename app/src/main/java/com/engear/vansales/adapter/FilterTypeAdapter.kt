package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.databinding.RowFilterTypeItemBinding
import com.engear.vansales.interfaces.OnItemClickListener


class FilterTypeAdapter(
    var context: Context,
    var typeList: ArrayList<String>,
    var selectedPosition: Int
) : RecyclerView.Adapter<FilterTypeAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowFilterTypeItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return typeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mData = typeList[position]
        holder.binding.item = mData
        holder.binding.executePendingBindings()

        if (position == selectedPosition) {
            holder.binding.lvType.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorWhite
                )
            )
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, mData)
        }

    }


    inner class ViewHolder(val binding: RowFilterTypeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}