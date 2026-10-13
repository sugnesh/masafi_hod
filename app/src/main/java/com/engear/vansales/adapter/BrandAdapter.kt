package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowBrandListBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Brand


class BrandAdapter(
    var context: Context,
    var allList: ArrayList<Brand>
) : RecyclerView.Adapter<BrandAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowBrandListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.item = mData

        if (mData.isPosted) {
            holder.binding.ivCheckDone.visibility = View.VISIBLE
        } else {
            holder.binding.ivCheckDone.visibility = View.GONE
        }

        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

    }


    inner class ViewHolder(val binding: RowBrandListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}