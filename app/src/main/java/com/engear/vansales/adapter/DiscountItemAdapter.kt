package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.databinding.RowEndroseItemBinding
import com.engear.vansales.databinding.RowPricingItemBinding
import com.engear.vansales.databinding.RowPromotionItemBinding
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.PDPData
import com.engear.vansales.model.PromoOfferData


class DiscountItemAdapter(
    var allList: ArrayList<PromoOfferData>
) : RecyclerView.Adapter<DiscountItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowPricingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.tvName.text = "Plan Name : "+mData.promotionName
        holder.binding.tvCode.text ="Apply : "+ mData.promotionSequace








        holder.binding.executePendingBindings()
    }


    inner class ViewHolder(val binding: RowPricingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}