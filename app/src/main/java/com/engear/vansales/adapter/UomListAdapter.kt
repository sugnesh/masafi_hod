package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowUomListItemBinding
import com.engear.vansales.databinding.RowUomSalesItemBinding
import com.engear.vansales.model.UomData
import com.engear.vansales.utils.Utils

class UomListAdapter(
    var context: Context,
    var viewType: Int,
    var qtyList: MutableList<Any>,
    var type: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    inner class View1ViewHolder(val binding: RowUomListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    inner class View2ViewHolder(val binding: RowUomSalesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            return View1ViewHolder(
                RowUomListItemBinding.inflate(LayoutInflater.from(context), parent, false)
            )
        }
        return View2ViewHolder(
            RowUomSalesItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return qtyList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (viewType == VIEW_TYPE_ONE) {
            val list: List<UomData> =
                qtyList.filterIsInstance<UomData>()
            val mListData = list[position]
            (holder as View1ViewHolder).binding.item = mListData
            if (type == "load") {
                (holder as View1ViewHolder).binding.tvUomPrice.visibility = View.GONE

            } else {
                (holder as View1ViewHolder).binding.tvUomPrice.text =
                    "Price: " + Utils.getCurrency(context!!) + " " + Utils.getCurrencyFormate(
                        context!!,
                        mListData.grossPrice.toFloat()
                    )

            }
            holder.binding.executePendingBindings()

        } else {
            val list: List<UomData> =
                qtyList.filterIsInstance<UomData>()
            val mListData = list[position]
            (holder as View2ViewHolder).binding.item = mListData
            if (type == "load") {
                (holder as View2ViewHolder).binding.tvPrice.visibility = View.GONE

            } else {
                (holder as View2ViewHolder).binding.tvPrice.text =
                    "Price: " + Utils.getCurrency(context!!) + " " + Utils.getCurrencyFormate(
                        context!!,
                        mListData.uomPrice.toFloat()
                    )
            }
            holder.binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }
}

