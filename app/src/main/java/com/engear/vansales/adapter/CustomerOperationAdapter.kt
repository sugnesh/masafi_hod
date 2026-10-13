package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.databinding.RowOperationBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.ActivityProfile


class CustomerOperationAdapter(
    var context: Context,
    var allList: ArrayList<ActivityProfile>
) : RecyclerView.Adapter<CustomerOperationAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowOperationBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.item = mData

        if (mData.name == "Shelf Display") {
            holder.binding.ivImage.setImageResource(R.drawable.officer)
        } else if (mData.name == "Sotck In Store") {
            holder.binding.ivImage.setImageResource(R.drawable.order)
        } else if (mData.name == "Complain Feedback") {
            holder.binding.ivImage.setImageResource(R.drawable.good_feedback)
        } else if (mData.name == "Planogram") {
            holder.binding.ivImage.setImageResource(R.drawable.cupboard)
        } else if (mData.name == "Campaign Picture") {
            holder.binding.ivImage.setImageResource(R.drawable.camera)
        } else if (mData.name == "Consumer Survey") {
            holder.binding.ivImage.setImageResource(R.drawable.checklist_flo)
        } else if (mData.name == "Asset Tracking") {
            holder.binding.ivImage.setImageResource(R.drawable.gps)
        }

        if (position != 0) {
            if (allList[(position - 1)].isCompleted == "1") {
                holder.binding.lytView.isEnabled = true
                holder.binding.lytView.isClickable = true
                holder.binding.lytView.alpha = 1.0f
            } else {
                holder.binding.lytView.isEnabled = false
                holder.binding.lytView.isClickable = false
                holder.binding.lytView.alpha = 0.5f
            }
        }
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

    }


    inner class ViewHolder(val binding: RowOperationBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}