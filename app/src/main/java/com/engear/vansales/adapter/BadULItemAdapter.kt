package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowItemBadUnloadBinding
import com.engear.vansales.databinding.RowOrderDetailAdapterItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.UomData
import com.engear.vansales.utils.Utils

class BadULItemAdapter(
    var context: Context,
    var itemList: MutableList<UomData>?
) : RecyclerView.Adapter<BadULItemAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowItemBadUnloadBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mItemData = itemList!![position]
        holder.binding.item = mItemData
        holder.binding.executePendingBindings()

        if (mItemData.itemName != null) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    mItemData.itemName[0].toString().toUpperCase(),
                    Utils.getMaterialColor(context, "300")
                )

            holder.binding.imgCircle.setImageDrawable(drawable)
            holder.binding.tvSystemQty!!.text =
                "System Qty : " + mItemData.uomQty + " " + mItemData.uomType

            if (mItemData.enterQty.isEmpty()) {
                holder.binding.lytDisplay!!.visibility = View.GONE
                holder.binding.lytAppview!!.visibility = View.GONE
            } else {
                holder.binding.lytDisplay!!.visibility = View.GONE
                holder.binding.lytAppview!!.visibility = View.VISIBLE
                holder.binding.tvapprvQty.text =
                    "Enter Qty : " + mItemData.enterQty + " " + mItemData.uomType
            }

        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

    }


    inner class ViewHolder(val binding: RowItemBadUnloadBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}