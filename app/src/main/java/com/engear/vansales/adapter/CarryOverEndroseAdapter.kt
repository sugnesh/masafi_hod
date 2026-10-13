package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowEndroseItemBinding
import com.engear.vansales.model.CarryOverEndrose


class CarryOverEndroseAdapter(
    var allList: ArrayList<CarryOverEndrose.Data.Transactiondetail>
) : RecyclerView.Adapter<CarryOverEndroseAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowEndroseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.tvcutomercode.text = mData.item.itemCode
        holder.binding.tvCustomerName.text = mData.item.itemName
        holder.binding.tvInvoceNo.text = mData.item_uom.name
        holder.binding.tvNetSale.text = mData.closingQty
        holder.binding.tvTotalsale.text = mData.code

        holder.binding.executePendingBindings()
    }


    inner class ViewHolder(val binding: RowEndroseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}