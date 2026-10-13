package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowCustomerVisitBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.VisitData


class CustomerVisitAdapter(
    var context: OnShopItemClickListener,
    var allCustomerList: List<VisitData.Data>
) : RecyclerView.Adapter<CustomerVisitAdapter.ViewHolder>() {

    private var contactListFiltered: List<VisitData.Data> = allCustomerList

    val onItemClickListener = context as OnShopItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowCustomerVisitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mCustomerData = contactListFiltered[position]
        holder.binding.item = mCustomerData

        if (mCustomerData.isSequnece == "1") {
            holder.binding.isSeq.text = "Yes"
        } else {
            holder.binding.isSeq.text = "No"
        }

        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onShopItemClick(position, "", "")
        }

    }

    inner class ViewHolder(val binding: RowCustomerVisitBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}