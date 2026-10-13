package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.engear.vansales.R
import com.engear.vansales.databinding.RowProductListBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.ProductCatelog


class ProductAdapter(
    var context: Context,
    var allList: ArrayList<ProductCatelog>
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowProductListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.item = mData

        if (mData.isCart == "1") {
            holder.binding.btnCart.setImageResource(R.drawable.ic_baseline_shopping_cart_fill)
        } else {
            holder.binding.btnCart.setImageResource(R.drawable.ic_baseline_add_shopping_cart)
        }

        Glide.with(context).load(mData.image_string)
            .into(holder.binding.ivImage)
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

        holder.binding.btnCart.setOnClickListener {
            onItemClickListener.onItemsClick(position, "cart")
        }

    }


    inner class ViewHolder(val binding: RowProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}