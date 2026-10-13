package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowOrderRequestAdapterItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.ItemData
import com.engear.vansales.utils.Utils

class UnloadRequestItemAdapter(
    var context: Context,
    var mType: String,
    var itemsList: MutableList<ItemData.Data>
) : RecyclerView.Adapter<UnloadRequestItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowOrderRequestAdapterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mItemData = itemsList[position]
        //holder.binding.item = mItemData
        holder.binding.executePendingBindings()

        holder.binding.tvCode.text = mItemData.itemCode
        holder.binding.tvName.text = mItemData.itemName

        if (mItemData.itemUOM!!.isNotEmpty() && mItemData.itemUOM!![0]!!.uomId != null) {
            holder.binding.tvBaseUomType.text = mItemData.itemUOM[0]!!.uom!!.name
            holder.binding.tvBaseUomPrice.text = "Price: " + mItemData.itemUOM[0]!!.uomPrice
        }

        if (mItemData.itemName != null) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    mItemData.itemName[0].toString().toUpperCase(),
                    Utils.getMaterialColor(context, "300")
                )
            holder.binding.imgCircle.setImageDrawable(drawable)
        }


        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

        if (mItemData.itemSelectUOM != null && mItemData.itemSelectUOM.isNotEmpty()) {
            holder.binding.llBaseUom.visibility = View.GONE
            holder.binding.rvUomList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.binding.rvUomList.adapter = UomListAdapter(
                context,
                UomListAdapter.VIEW_TYPE_ONE,
                mItemData.itemSelectUOM!! as MutableList<Any>,
                "load"
            )
        } else {
            holder.binding.llBaseUom.visibility = View.VISIBLE
        }

    }


    inner class ViewHolder(val binding: RowOrderRequestAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}