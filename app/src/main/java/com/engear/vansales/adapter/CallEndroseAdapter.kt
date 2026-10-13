package com.engear.vansales.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowEndroseItemBinding
import com.engear.vansales.model.CallEndrose


class CallEndroseAdapter(
    var allList: ArrayList<CallEndrose.Data>
) : RecyclerView.Adapter<CallEndroseAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowEndroseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.e("datasize", allList.size.toString())
        val mData = allList[position]
        holder.binding.tvcutomercode.text = mData.scheduleCall.toString()
        holder.binding.tvCustomerName.text = mData.visitedCount.toString()
        holder.binding.tvInvoceNo.text = mData.unscheduleCall.toString()
        holder.binding.tvNetSale.text = mData.scheduleSale.toString()


        holder.binding.tvTotalsale.text = mData.unscheduleSale
            .toString()


    }


    inner class ViewHolder(val binding: RowEndroseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}