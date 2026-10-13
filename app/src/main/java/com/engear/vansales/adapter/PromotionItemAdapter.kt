package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowPromotionItemBinding
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.ItemData


class PromotionItemAdapter(
    var allList: ArrayList<ItemData.Data>,
    var db: DatabaseHandler
) : RecyclerView.Adapter<PromotionItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowPromotionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.tvCode.text = mData.itemCode.toString()
        holder.binding.tvName.text = mData.itemName.toString()


            holder.binding.tvQty.text = mData.itemSelectUOM[0].uomQty.toString()








        holder.binding.executePendingBindings()
    }


    inner class ViewHolder(val binding: RowPromotionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}