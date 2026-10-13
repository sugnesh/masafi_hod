package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowFilterTypeDataItemBinding
import com.engear.vansales.interfaces.OnFilterClickListener

class FilterTypeDataAdapter(
    var context: Context,
    var typeDataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<FilterTypeDataAdapter.ViewHolder>() {

    val onFilterClickListener = context as OnFilterClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowFilterTypeDataItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return typeDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mData = typeDataList[position]
        holder.binding.chkType.text = mData["Name"]
        holder.binding.chkType.isChecked = mData["isSelect"] == "true"
        holder.binding.executePendingBindings()

        holder.binding.chkType.setOnCheckedChangeListener { buttonView, isChecked ->
            onFilterClickListener.onFilterClick(position,mData["Id"]!!, isChecked)
        }
    }


    inner class ViewHolder(val binding: RowFilterTypeDataItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}