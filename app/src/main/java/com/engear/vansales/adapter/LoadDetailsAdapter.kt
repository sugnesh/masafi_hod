package com.engear.vansales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.model.LoadQuantity


class LoadDetailsAdapter(
    private var listLoadItems: ArrayList<LoadQuantity.Data.Loadquantity> //,
    //  private val onLoadOnClickListener: FragmentActivity?
) : RecyclerView.Adapter<LoadDetailsAdapter.LoadDetailViewHolder>() {

    inner class LoadDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var loadItemNumber: TextView = view.findViewById(R.id.text_itemNo)
        var loadItemName: TextView = view.findViewById(R.id.text_itemName)
        var itemLoadQuantity: TextView = view.findViewById(R.id.text_itemLoadQuantity)
        var itemSalesQuantity: TextView = view.findViewById(R.id.text_itemSalesQuantity)
        var itemReturnQuantity: TextView = view.findViewById(R.id.text_itemReturnQuantity)
        var itemUnLoadQuantity: TextView = view.findViewById(R.id.text_itemUnLoadQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadDetailViewHolder {
        val loadView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_load_details_layout, parent, false)
        return LoadDetailViewHolder(loadView)
    }

    override fun getItemCount(): Int {
        return listLoadItems.size
    }

    override fun onBindViewHolder(holder: LoadDetailViewHolder, position: Int) {
        val loadItems = listLoadItems[position]
        holder.loadItemNumber.text = "Item No: ITEM0"+loadItems.item.itemCode.toString()
        holder.loadItemName.text = "Item Name: "+loadItems.item.itemName
        holder.itemLoadQuantity.text = "Load Qty: "+loadItems.loadQty.toString()
        holder.itemSalesQuantity.text = "Sales Qty: "+loadItems.salesQty.toString()
        holder.itemReturnQuantity.text = "Return Qty: "+loadItems.returnQty.toString()
        holder.itemUnLoadQuantity.text = "UnLoad Qty: "+loadItems.unloadQty.toString()

    }

}