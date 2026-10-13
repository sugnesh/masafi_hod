package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowItemLoadDetailBinding
import com.engear.vansales.databinding.RowItemLoadRequestDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.LoadRequestData
import com.engear.vansales.model.SalesmanLoadDetail
import com.engear.vansales.utils.Utils
import java.util.*


class LoadIRequestAdapter(
    var context: Context,
    var loadItemList: List<LoadRequestData.LoadRequestView.ItemDetail>
) : RecyclerView.Adapter<LoadIRequestAdapter.ViewHolder>() {

    var db: DatabaseHandler = DatabaseHandler(context)
    private var contactListFiltered: List<LoadRequestData.LoadRequestView.ItemDetail> = loadItemList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowItemLoadRequestDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mLoadData = contactListFiltered[position]
        holder.binding.item = mLoadData

        var itemCode = db!!.getItemCode(mLoadData.item_id)
        var itemName = db!!.getItemName(mLoadData.item_id)

        if (itemName != null && itemName.isNotEmpty()) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    itemName[0].toString().toUpperCase(),
                    Utils.getMaterialColor(context, "300")
                )

            holder.binding.imgCircle.setImageDrawable(drawable)
        }
        holder.binding.tvCode.text = itemCode
        holder.binding.tvName.text = itemName
        holder.binding.tvreqstQty.text =
            "Request Qty : " + db!!.getUOMName(mLoadData.item_req_uom_id) + " " + mLoadData.requested_qty
        holder.binding.tvapprvQty.text =
            "Approve Qty : " + db!!.getUOMName(mLoadData.item_uom_id) + " " + mLoadData.qty

        holder.binding.executePendingBindings()
    }

    inner class ViewHolder(val binding: RowItemLoadRequestDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}