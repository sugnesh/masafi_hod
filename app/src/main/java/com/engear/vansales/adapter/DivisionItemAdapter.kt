package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.databinding.RowDeivisionItemBinding
import com.engear.vansales.databinding.RowOrderConfirmItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.SupervisorCategory

class DivisionItemAdapter(
    var context: Context,
    var itemsList: MutableList<SupervisorCategory>,
    var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<DivisionItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowDeivisionItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mItemData = itemsList[position]
        holder.binding.tvName.text = mItemData.category

        holder.binding.executePendingBindings()

        holder.binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbYes -> {
                    onItemClickListener.onItemsClick(position, "1")
                }
                R.id.rbNo -> {
                    onItemClickListener.onItemsClick(position, "0")
                }
                else -> {

                }
            }
        }
    }


    inner class ViewHolder(val binding: RowDeivisionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}