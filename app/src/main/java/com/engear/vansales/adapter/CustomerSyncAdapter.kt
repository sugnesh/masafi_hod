package com.engear.vansales.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.databinding.NewCustomerSyncItemBinding
import com.engear.vansales.localDB.CustomerSyncEntity

class CustomerSyncAdapter(
    private val customers: List<CustomerSyncEntity>,
    private val onSyncClick: (CustomerSyncEntity) -> Unit
) : RecyclerView.Adapter<CustomerSyncAdapter.CustomerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = NewCustomerSyncItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customers[position]
        holder.binding.customerCodeTextView.text = "Customer Code: ${customer.customerCode}"
        holder.binding.customerNameTextView.text = "Customer Name: ${customer.customerName}"

        if (customer.isSynced) {
            holder.binding.customerImageView.setImageResource(R.drawable.sync_green)
            holder.binding.customerImageView.setOnClickListener {
                Log.d("CustomerSyncAdapter", "Customer ID: ${customer.id}")
            }
        } else {
            holder.binding.customerImageView.setImageResource(R.drawable.sync_black)
            holder.binding.customerImageView.setOnClickListener {
                onSyncClick(customer)
                Log.d("CustomerSyncAdapter", "Customer ID: ${customer.id}")
            }
        }
    }

    override fun getItemCount() = customers.size

    class CustomerViewHolder(val binding: NewCustomerSyncItemBinding) : RecyclerView.ViewHolder(binding.root)
}
