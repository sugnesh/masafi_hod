package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowVisitedCustomerItemBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.utils.Utils

class VisitedCustomerAdapter(
    var context: Context,
    var recentCustomerList: ArrayList<CustomerData.DataCustomer>
) : RecyclerView.Adapter<VisitedCustomerAdapter.ViewHolder>() {

    val onItemClickListener = context as OnShopItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowVisitedCustomerItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recentCustomerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val drawable = TextDrawable.builder()
            .buildRound(
                recentCustomerList[position].userDetail.firstname,
                Utils.getMaterialColor(context, "300")
            )

        holder.binding.imgCircle.setImageDrawable(drawable)
        holder.binding.tvName.text =
            recentCustomerList[position].userDetail.firstname + " " + recentCustomerList[position].userDetail.lastname
        holder.itemView.setOnClickListener {
            onItemClickListener.onShopItemClick(position, "dialog", "")
        }
    }


    inner class ViewHolder(val binding: RowVisitedCustomerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}