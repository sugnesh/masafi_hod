package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowVanBatchDetailsItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.BatchdetailsList
import com.engear.vansales.model.Item
import com.engear.vansales.utils.Utils


class VanBatchDetailsAdapter(
    var context: Context,
    var allItemList: ArrayList<BatchdetailsList>,

    ) : RecyclerView.Adapter<VanBatchDetailsAdapter.ViewHolder>() {

    private var contactListFiltered: MutableList<BatchdetailsList> = allItemList
    var db: DatabaseHandler? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowVanBatchDetailsItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mItem = contactListFiltered[position]

        holder.binding.tvBatchNo.text = mItem.batchNumber.toString().trim()
        holder.binding.tvBatchExpiry.text=mItem.expiryDate.toString().trim()
        holder.binding.tvBatchQty.text=mItem.qty.toString().trim()
        holder.binding.executePendingBindings()

    }

    inner class ViewHolder(val binding: RowVanBatchDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}