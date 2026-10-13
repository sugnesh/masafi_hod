package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowUomDialogItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.UomData


class InputDialogUomAdapter(
    var context: Context,
    var qtyList: MutableList<UomData>?
) : RecyclerView.Adapter<InputDialogUomAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowUomDialogItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return qtyList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mQtyData = qtyList!![position]
        holder.binding.item = mQtyData
        holder.binding.executePendingBindings()

        holder.binding.btnRemove.setOnClickListener {
            onItemClickListener.onItemsClick(position, "remove")
        }

        holder.binding.btnEdit.visibility = View.GONE
    }


    inner class ViewHolder(val binding: RowUomDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}