package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowCustomerDailyFieldBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.CustomerData


class DailyFeildCustomerAdapter(
    var context: OnShopItemClickListener,
    var allCustomerList: ArrayList<CustomerData.DataCustomer>,
    var db: DatabaseHandler?
) : RecyclerView.Adapter<DailyFeildCustomerAdapter.ViewHolder>() {

    private var contactListFiltered: ArrayList<CustomerData.DataCustomer> = allCustomerList

    val onItemClickListener = context as OnShopItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowCustomerDailyFieldBinding.inflate(LayoutInflater.from(parent.context)
            , parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mCustomerData = contactListFiltered[position]
        holder.binding.item = mCustomerData
        holder.binding.executePendingBindings()


        if (mCustomerData.isCheck){
            holder.binding.ischeckview.visibility = View.VISIBLE
        }else{
            holder.binding.ischeckview.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            onItemClickListener.onShopItemClick(position,"","")
        }


    }


    inner class ViewHolder(val binding: RowCustomerDailyFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}