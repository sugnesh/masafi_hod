package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowNotificationListItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.NotificationData
import com.engear.vansales.utils.Utils

class NotificationAdapter(
    var context: Context,
    var orderList: List<NotificationData.NotificationView>?
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowNotificationListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
        holder.binding.item = mOrderData

        var mStringTime = Utils.getTimeDiff(mOrderData.date)
        holder.binding.txtTime.text = mStringTime

        if (mOrderData.type == "Geo Approval" || mOrderData.type == "Over Due"
            || mOrderData.type == "Credit Limit"
        ) {
            if (mOrderData.approval_status == "Pending") {
                holder.binding.lytBtnView!!.visibility = View.VISIBLE
            } else {
                holder.binding.lytBtnView!!.visibility = View.GONE
            }
        } else {
            holder.binding.lytBtnView!!.visibility = View.GONE
        }
        holder.binding.executePendingBindings()

        holder.binding.btnApprove!!.setOnClickListener {
            onItemClickListener.onItemsClick(position, "Approve")
        }

        holder.binding.btnReject!!.setOnClickListener {
            onItemClickListener.onItemsClick(position, "Reject")
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: RowNotificationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}