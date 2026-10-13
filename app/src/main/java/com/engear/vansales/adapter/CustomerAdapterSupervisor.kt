package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.supervisor.view.interfaces.NewCustomerOnClickListener
import com.engear.vansales.R
import com.engear.vansales.databinding.RowCustomerItemBinding
import com.engear.vansales.databinding.RowNewCustomerBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.NewCustomer
import com.engear.vansales.utils.Utils


class CustomerAdapterSupervisor(
    var allCustomerList: ArrayList<NewCustomer.Data>,
    var context : NewCustomerOnClickListener
) : RecyclerView.Adapter<CustomerAdapterSupervisor.ViewHolder>() {

    private var contactListFiltered: ArrayList<NewCustomer.Data> = allCustomerList

    val onItemClickListener = context as NewCustomerOnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowNewCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val customer = contactListFiltered[position]

        holder.binding.textCustomerCode.text = "Customer Code: "+customer.customerCode
        holder.binding.textCustomerName.text= "Name: "+customer.user.firstname+ " "+ customer.user.lastname
        holder.binding.textCustomerRoute.text = "Route: "+customer.routeId
        holder.binding.textCustomerMobile.text = "Mobile: "+customer!!.user.mobile

        holder.itemView.setOnClickListener {
            context.onNewCustomerOnClickListener(customer)
        }
    }


    inner class ViewHolder(val binding: RowNewCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}