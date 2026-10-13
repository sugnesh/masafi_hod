package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowOrderDetailAdapterItemBinding
import com.engear.vansales.model.UomData
import com.engear.vansales.utils.Utils

class UnloadItemAdapter(
    var context: Context,
    var itemList: MutableList<UomData>?
) : RecyclerView.Adapter<UnloadItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowOrderDetailAdapterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mItemData = itemList!![position]
        holder.binding.item = mItemData
        holder.binding.tvPrice.visibility = View.GONE
        holder.binding.executePendingBindings()

        if (mItemData.itemName != null) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    mItemData.itemName[0].toString().toUpperCase(),
                    Utils.getMaterialColor(context, "300")
                )

            holder.binding.imgCircle.setImageDrawable(drawable)
        }

       /* holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position, "dialog")
        }*/

    }


    inner class ViewHolder(val binding: RowOrderDetailAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}