package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowUomItemBinding
import com.engear.vansales.model.UomData


class UomAdapter(
    var context: Context,
    var qtyList: MutableList<UomData>
) : RecyclerView.Adapter<UomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowUomItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return qtyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mListData = qtyList[position]
        holder.binding.item = mListData
        holder.binding.executePendingBindings()
    }


    inner class ViewHolder(val binding: RowUomItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}