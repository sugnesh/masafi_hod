package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.databinding.RowPrmotionItemListBinding
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.PromoOfferData


class PromotionListAdapter(
    var context: Context,
    var allList: List<PromoOfferData>?,
    var db: DatabaseHandler
) : RecyclerView.Adapter<PromotionListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        R.layout.row_prmotion_item_list
        val binding =
            RowPrmotionItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList?.get(position)
        holder.binding.tvName.text = "Plan Name - " + mData!!.promotionName.toString()
        holder.binding.tvCode.text = "Apply - " + mData!!.promotionSequace.toString()


        var mAdapter = PromotionItemAdapter(mData!!.orderItem, db!!)
        var mAdapter2 = PromotionItemAdapter(mData!!.offerItem, db!!)
        val layoutManager = LinearLayoutManager(context)
        val layoutManager2 = LinearLayoutManager(context)
        holder.binding.rvItemList.layoutManager = layoutManager
        holder.binding.rvItemList.itemAnimator = DefaultItemAnimator()
        holder.binding.rvItemList.adapter = mAdapter
//
        holder.binding.rvOfferList.layoutManager = layoutManager2
        holder.binding.rvOfferList.itemAnimator = DefaultItemAnimator()
        holder.binding.rvOfferList.adapter = mAdapter2


        holder.binding.executePendingBindings()
    }


    inner class ViewHolder(val binding: RowPrmotionItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}