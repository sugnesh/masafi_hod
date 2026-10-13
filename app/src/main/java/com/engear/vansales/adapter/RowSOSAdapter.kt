package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowInventorySosBinding


class RowSOSAdapter(
    var context: Context,
    var allList: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<RowSOSAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowInventorySosBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.tvName.text = mData["Item"] + "\n" + mData["Score"] + "%"
        holder.binding.tvTotal.text = mData["Total"]
        holder.binding.tvActual.text = mData["Actual"]
        holder.binding.executePendingBindings()

    }

    inner class ViewHolder(val binding: RowInventorySosBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}