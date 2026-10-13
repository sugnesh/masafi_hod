package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.databinding.RowItemDateBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.localDB.DatabaseHandler

class TaskDateItemAdapter(
    var context: Context,
    var taskList: ArrayList<String>,
   var db: DatabaseHandler?
) : RecyclerView.Adapter<TaskDateItemAdapter.ViewHolder>() {


    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        R.layout.row_task_item
        val binding =
            RowItemDateBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return taskList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mOrderData = taskList!![position]
        holder.binding.executePendingBindings()
        holder.binding.txtview.text = mOrderData

        holder. binding.rvList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.  binding.rvList.adapter = TaskItemAdapter(context, db!!.getAllTaskbyDate(mOrderData))
    }


    inner class ViewHolder(val binding: RowItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}