package com.engear.vansales.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.databinding.RowItemCategoryBinding
import com.engear.vansales.interfaces.OnCategoryClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.CategoryData


class CategoryAdapter(
    var context: Context,
    var allList: ArrayList<CategoryData>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var db = DatabaseHandler(context)
    val onCategoryClickListener = context as OnCategoryClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowItemCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.tvCategory.text = mData.name

        if (mData.isClick.equals("1")) {
            holder.binding.tvCategory.setBackgroundResource(R.drawable.rounded_rect_blue_capsule)
            holder.binding.tvCategory.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            holder.binding.tvCategory.setBackgroundResource(R.drawable.rounded_rect_white_capsule)
            holder.binding.tvCategory.setTextColor(Color.parseColor("#20B389"))
        }

        holder.binding.root.setOnClickListener {
            onCategoryClickListener.onCategoryClick(position, "dialog")
        }

        holder.binding.executePendingBindings()
    }


    inner class ViewHolder(val binding: RowItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}