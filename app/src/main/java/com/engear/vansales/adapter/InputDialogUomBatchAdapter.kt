package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowUomDialogBatchBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerItemReturnData
import com.engear.vansales.model.UomData


class InputDialogUomBatchAdapter(
    var context: Context,
    var qtyList: MutableList<UomData>?
) : RecyclerView.Adapter<InputDialogUomBatchAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowUomDialogBatchBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return qtyList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val mQtyData = qtyList!![position]
        var batchData = qtyList!![position]
        // holder.binding.item = mQtyData
        holder.binding.executePendingBindings()


        holder.binding.tvUom.text = batchData.uomType
        holder.binding.tvPrice.text = batchData.uomPrice
        holder.binding.tvQty.text = batchData.uomQty
       // holder.binding.tvBatch.text = batchData.batchs[0].batchNumber
        holder.binding.tvExpiry.text = batchData.expiryDate

        holder.binding.btnRemove.setOnClickListener {
            onItemClickListener.onItemsClick(position, "remove")
        }

        holder.binding.btnEdit.visibility = View.GONE
    }


    inner class ViewHolder(val binding: RowUomDialogBatchBinding) :
        RecyclerView.ViewHolder(binding.root)
}