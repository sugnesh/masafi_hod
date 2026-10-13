package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowReturnAdapterItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Order

class ReturnItemAdapter(
    var context: Context,
    var returnList: ArrayList<Order>?
) : RecyclerView.Adapter<ReturnItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowReturnAdapterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return returnList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mReturnData = returnList!![position]
        holder.binding.item = mReturnData
        holder.binding.executePendingBindings()

        if (mReturnData.orderType == "goodReturn")
            holder.binding.tvReturnType.text = "Return Type : Good Return"
        else if (mReturnData.orderType == "badReturn")
            holder.binding.tvReturnType.text = "Return Type : Bad Return"

        if (mReturnData.status == "") {
            holder.binding.imgVerified.visibility = View.GONE
        } else {
            if (mReturnData.status == "2") {
                holder.binding.imgVerified.setImageResource(com.engear.vansales.R.drawable.ic_icon_not_verified)
            } else {
                holder.binding.imgVerified.setImageResource(com.engear.vansales.R.drawable.ic_icon_verified)
            }
            holder.binding.imgVerified.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

        holder.itemView.setOnLongClickListener {
            onItemClickListener.onItemsClick(position, "click")
            return@setOnLongClickListener true
        }
    }


    inner class ViewHolder(val binding: RowReturnAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}