package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowItemDivisionBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.CustomerData


class CustomerDivisionAdapter(
    var context: Context,
    var allCustomerList: ArrayList<CustomerData.DataCustomer.Customerlob>
) : RecyclerView.Adapter<CustomerDivisionAdapter.ViewHolder>() {

    private var contactListFiltered: ArrayList<CustomerData.DataCustomer.Customerlob> = allCustomerList

    val onItemClickListener = context as OnShopItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemDivisionBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allCustomerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mCustomerData = contactListFiltered[position]
        holder.binding.item = mCustomerData
        holder.binding.executePendingBindings()


        holder.itemView.setOnClickListener {
            onItemClickListener.onShopItemClick(position, "dialog", "")
        }



    }


    inner class ViewHolder(val binding: RowItemDivisionBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}