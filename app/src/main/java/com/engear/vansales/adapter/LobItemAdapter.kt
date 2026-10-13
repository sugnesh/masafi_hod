package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.activities.supervisor.RouteActivity
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.RowLobItemBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.ItemData


class LobItemAdapter(
    var itemList: ArrayList<ItemData.Data.Lob>,
    var context: RouteActivity

) : RecyclerView.Adapter<LobItemAdapter.ViewHolder>() {


    val onItemClickListener = context as OnShopItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowLobItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mCustomerData = itemList[position]
        holder.binding.item = mCustomerData
        holder.binding.executePendingBindings()


        holder.itemView.setOnClickListener {
            onItemClickListener.onShopItemClick(position,"","")
        }


    }


    inner class ViewHolder(val binding: RowLobItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}