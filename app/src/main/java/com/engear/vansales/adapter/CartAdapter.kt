package com.engear.vansales.adapter

import android.R
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowCartItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnUOMUpdateListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.ProductCatelog


class CartAdapter(
    var context: Context,
    var allList: ArrayList<ProductCatelog>
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    var db = DatabaseHandler(context)
    val onItemClickListener = context as OnItemClickListener
    val onUOMListener = context as OnUOMUpdateListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowCartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.item = mData
        val dataAdapter = mData.mUOMList.let {
            ArrayAdapter<String>(
                context,
                R.layout.simple_spinner_item, it
            )
        }
        dataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.binding.qtySpinner.adapter = dataAdapter
        var value = holder.binding.qtySpinner.selectedItemPosition
        holder.binding.executePendingBindings()
        holder.binding.qtySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View,
                    mPosition: Int,
                    id: Long
                ) {
                    if (value != mPosition) {
                        var mSelectUOMId = db!!.getUOMId(mData.mUOMList[mPosition])
                        Log.e("UOMId", mSelectUOMId)
                        onUOMListener.onUOMUpdate(position, mSelectUOMId)
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    // your code here
                }
            }

        holder.binding.btnPlus.setOnClickListener {
            onItemClickListener.onItemsClick(position, "plus")
        }

        holder.binding.btnMinus.setOnClickListener {
            onItemClickListener.onItemsClick(position, "minus")
        }

    }


    inner class ViewHolder(val binding: RowCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}