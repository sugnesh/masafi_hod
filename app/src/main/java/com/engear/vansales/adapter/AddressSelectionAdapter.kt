package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowDeliveryAddressBinding
import com.engear.vansales.model.CustomerHODAddress

class AddressSelectionAdapter(
    var context: Context,
    private val list: List<CustomerHODAddress.CustomerInfoAddress.CustomerHIAddress>,
    private val onClick: (CustomerHODAddress.CustomerInfoAddress.CustomerHIAddress) -> Unit
) : RecyclerView.Adapter<AddressSelectionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RowDeliveryAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowDeliveryAddressBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mOrderData = list!![position]
        holder.binding.item = mOrderData
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener {
            onClick(list[position])
        }
    }

    override fun getItemCount() = list.size
}