package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.databinding.RowLoadOrderAdapterItemBinding
import com.engear.vansales.databinding.RowTaskItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Order
import com.engear.vansales.model.TaskData

class TaskItemAdapter(
    var context: Context,
    var taskList: List<TaskData.Task>
) : RecyclerView.Adapter<TaskItemAdapter.ViewHolder>() {


    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        R.layout.row_task_item
        val binding =
            RowTaskItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return taskList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = taskList!![position]
        holder.binding.item = mOrderData
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
        }
    }


    inner class ViewHolder(val binding: RowTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}