package com.engear.vansales.activities.picker

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowItemLoadSupervisiorDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnItemUpdateListener
import com.engear.vansales.model.PickerLoadData
import com.engear.vansales.utils.Utils
import kotlin.collections.ArrayList


class PickerLoadItemAdapter(
    var context: Context,
    var loadItemList: MutableList<PickerLoadData.PickerDataDetail.LoadRequest>,
    var status: String
) : RecyclerView.Adapter<PickerLoadItemAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: MutableList<PickerLoadData.PickerDataDetail.LoadRequest> =
        loadItemList
    val onItemClickListener = context as OnItemClickListener

    val onItemUpdateListener = context as OnItemUpdateListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowItemLoadSupervisiorDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mLoadData = contactListFiltered[position]
        holder.binding.item = mLoadData

        holder.binding.executePendingBindings()
        holder.binding.tvCode.text = mLoadData.item!!.itemCode
        holder.binding.tvName.text = mLoadData.item!!.item_name
        try {
            holder.binding.tvreqstQty.text =
                "Qty  : " + mLoadData.itemUom!!.uomName + " " + mLoadData.approval_qty_by_warehouse
        } catch (e: Exception) {
            e.toString()
        }


        if (mLoadData.item!!.item_name != null) {
            val drawable = TextDrawable.builder()
                .buildRound(
                    mLoadData.item!!.item_name[0].toString().toUpperCase(),
                    Utils.getMaterialColor(context, "300")
                )

            holder.binding.imgCircle.setImageDrawable(drawable)
        }

        /* if(status=="Approved" || status=="Rejected"){
             holder.binding.imgEdit.visibility=View.GONE
         }else{
             holder.binding.imgEdit.visibility=View.VISIBLE
         }*/
        holder.binding.imgEdit.visibility = View.VISIBLE

        holder.binding.imgEdit.setOnClickListener {
            holder.binding.laySuperEdit.visibility = View.VISIBLE
        }

        holder.binding.edtQty!!.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    if (s.toString().isNotEmpty()) {
                        contactListFiltered[holder.adapterPosition].uomQty = s.toString()
                        onItemUpdateListener.onItemsUpdate(
                            contactListFiltered[holder.adapterPosition].id,
                            s.toString()
                        )
                    }

                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {

                }
            })

        holder.itemView.setOnClickListener {

        }

    }

    inner class ViewHolder(val binding: RowItemLoadSupervisiorDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = loadItemList
                } else {

                    var mSplit = charString.split(" ")

                    val filteredList: MutableList<PickerLoadData.PickerDataDetail.LoadRequest> =
                        ArrayList<PickerLoadData.PickerDataDetail.LoadRequest>()
                    for (row in loadItemList) {

                        var isContain = true
                        var name = row.item!!.itemCode + " " + row.item!!.item_name
                        for (mSpW in mSplit) {
                            if (!name.contains(mSpW, true)) {
                                isContain = false
                            }
                        }

                        if (isContain) {
                            filteredList.add(row)
                        }

                    }
                    contactListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                contactListFiltered =
                    filterResults.values as ArrayList<PickerLoadData.PickerDataDetail.LoadRequest>
                notifyDataSetChanged()
            }
        }
    }
}