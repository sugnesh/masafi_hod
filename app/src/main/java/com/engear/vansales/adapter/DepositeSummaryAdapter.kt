package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.engear.vansales.databinding.RowAssetsListBinding
import com.engear.vansales.databinding.RowDepositeSummaryBinding
import com.engear.vansales.databinding.RowSalesSummaryBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Assets
import com.engear.vansales.model.DepositeSummary
import com.engear.vansales.model.Invoice


class DepositeSummaryAdapter(
    var context: Context,
    var allList: ArrayList<DepositeSummary>
) : RecyclerView.Adapter<DepositeSummaryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowDepositeSummaryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]

        holder.binding.txtCustomer.setText("(" + mData.customerCode!! + ") " + mData.customerName!!)
        holder.binding.txtInvNo.text = mData.collectionNo!!

        if (mData.collectionType!! == "1") {
            holder.binding.txtNet.text = "Cash"
        } else {
            holder.binding.txtNet.text = "Cheque"
        }

        holder.binding.txtGross.text = mData.amountPaid!!

        holder.binding.executePendingBindings()


    }

    inner class ViewHolder(val binding: RowDepositeSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}