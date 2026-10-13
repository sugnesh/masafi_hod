package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.engear.vansales.databinding.RowPlanogramBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Planogram


class PlanogramAdapter(
    var context: Context,
    var allList: ArrayList<Planogram>
) : RecyclerView.Adapter<PlanogramAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowPlanogramBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.item = mData

        holder.binding.txtTitle.text = mData.name
        holder.binding.txtBrand.text = mData.toolName
        Glide.with(context).load(mData.arrImages[0])
            .into(holder.binding.profileImage)
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

    }


    inner class ViewHolder(val binding: RowPlanogramBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}