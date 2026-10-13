package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowDeliveryItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.DeliveryHeader

class DeliveryItemAdapter(
    var context: Context,
    var deliveryList: MutableList<DeliveryHeader>
) : RecyclerView.Adapter<DeliveryItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener
    private var db: DatabaseHandler = DatabaseHandler(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowDeliveryItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return deliveryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mDeliveryData = deliveryList[position]
        holder.binding.item = mDeliveryData
        holder.binding.executePendingBindings()

        if (mDeliveryData.status == 0) {
            holder.binding.imgVerified.visibility = View.GONE
            holder.binding.lytSTamp!!.visibility = View.GONE
            holder.binding.btnCancel!!.visibility = View.GONE
        } else {
            if (mDeliveryData.isDeleted == "1") {
                holder.binding.imgVerified.setImageResource(com.engear.vansales.R.drawable.ic_icon_not_verified)
            } else {
                holder.binding.imgVerified.setImageResource(com.engear.vansales.R.drawable.ic_icon_verified)
                holder.binding.btnCancel!!.visibility = View.VISIBLE
            }
            holder.binding.imgVerified.visibility = View.VISIBLE
            holder.binding.lytSTamp!!.visibility = View.VISIBLE

            var mInvoice = mDeliveryData.deliveryNumber!!.replace("S1","HH")
            var mStamp = db!!.getInvoiceStamp(mInvoice)
            if (mStamp == "0") {
                holder.binding.tvStamp!!.text = "Stamp: No"
                holder.binding.ivInfoUploadStamp!!.visibility = View.GONE
            } else if (mStamp == "1") {
                holder.binding.tvStamp!!.text = "Stamp: Yes"
                holder.binding.ivInfoUploadStamp!!.visibility = View.GONE
            } else {
                holder.binding.tvStamp!!.text = "Stamp: Pending"
                holder.binding.ivInfoUploadStamp!!.visibility = View.VISIBLE
            }
        }

        holder.binding.ivInfoUploadStamp!!.setOnClickListener {
            onItemClickListener.onItemsClick(position, "StampUpload")
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "click")
        }

        holder.binding.btnCancel!!.setOnClickListener {
            onItemClickListener.onItemsClick(position, "InvoiceCancel")
        }

        holder.itemView.setOnLongClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
            return@setOnLongClickListener true
        }

    }


    inner class ViewHolder(val binding: RowDeliveryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}