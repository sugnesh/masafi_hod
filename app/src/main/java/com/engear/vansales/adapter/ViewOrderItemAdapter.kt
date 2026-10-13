package com.engear.vansales.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.databinding.RowDeliveryItemBinding
import com.engear.vansales.databinding.RowViewOrderItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.DeliveryHeader

class ViewOrderItemAdapter(
    var context: Context,
    var deliveryList: MutableList<DeliveryHeader>
) : RecyclerView.Adapter<ViewOrderItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowViewOrderItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return deliveryList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mDeliveryData = deliveryList[position]
        holder.binding.item = mDeliveryData

        holder.binding.tvAddres!!.text =
            "Address: " + mDeliveryData.mOrder.mAddress.address_1 + " " + mDeliveryData.mOrder.mAddress.address_2
        holder.binding.tvPhone!!.text =
            "Phone: " + mDeliveryData.mOrder.mAddress.phone_number

        holder.binding.executePendingBindings()


        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "click")
        }

        holder.binding.btnMap!!.setOnClickListener {
            onItemClickListener.onItemsClick(position, "onMap")
        }


    }


    inner class ViewHolder(val binding: RowViewOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}