package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.SupervisorNoficationItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.NotificationData
import com.engear.vansales.utils.Utils

class SupervisorNotificationAdapter(
    var context: Context,
    var orderList: List<NotificationData.NotificationView>?
) : RecyclerView.Adapter<SupervisorNotificationAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {



            val binding =
                SupervisorNoficationItemBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)



    }

    override fun getItemCount(): Int {
        return orderList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = orderList!![position]
        holder.binding.item = mOrderData
        holder.binding.executePendingBindings()
        holder.binding.textNotificationDate.text= Utils.getFormatedStringDate(mOrderData.date)

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }
    }


    inner class ViewHolder(val binding: SupervisorNoficationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}