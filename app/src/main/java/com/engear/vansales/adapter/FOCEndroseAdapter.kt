package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowEndroseItemBinding
import com.engear.vansales.model.FocEndrose


class FOCEndroseAdapter(
    var allList: ArrayList<FocEndrose.Data>
) : RecyclerView.Adapter<FOCEndroseAdapter.ViewHolder>() {


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
        holder.binding.tvcutomercode.text = mData.id.toString()
        holder.binding.tvCustomerName.text = mData.id.toString()
        holder.binding.tvInvoceNo.text = mData.id.toString()
        holder.binding.tvNetSale.text = mData.id.toString()


        holder.binding.tvTotalsale.visibility = View.GONE




        holder.binding.executePendingBindings()
    }


    inner class ViewHolder(val binding: RowEndroseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}