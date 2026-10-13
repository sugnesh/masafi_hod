package com.engear.vansales.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.engear.vansales.R
import com.engear.vansales.databinding.RowChildItemExpanableBinding
import com.engear.vansales.model.CreditRouteMonitoring

class CreditRouteMonitringAdapter(
    private val context: Context,
    private val expandableListTitle: List<CreditRouteMonitoring.Data>
) : BaseExpandableListAdapter() {
    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return expandableListTitle[listPosition].creditNoteDetails[expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        var binding: RowChildItemExpanableBinding ? = null
        val item = getChild(listPosition,expandedListPosition) as CreditRouteMonitoring.Data.CreditNoteDetail
        binding = RowChildItemExpanableBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        if (expandedListPosition == 0){
            binding.headerView.visibility = View.VISIBLE
            binding.textcutomercode.text = "Item No"
            binding.textCustomerName.text = "Item Name"
            binding.textInvoceNo.text = "Qty"
            binding.textNetSale.text = "UOM"
            binding.textTotalsale.text = "Price"
        }else{
            binding.headerView.visibility = View.VISIBLE
        }
        binding.tvcutomercode.text =  item.item.id.toString()
        binding.tvCustomerName.text =  item.item.itemName
        binding.tvInvoceNo.text =  item.itemQty
        binding.tvNetSale.text =  item.itemUom.name
        binding.tvTotalsale.text =  item.itemGrandTotal
        return binding.root
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return expandableListTitle[listPosition].creditNoteDetails.size
    }

    override fun getGroup(listPosition: Int): Any {
        return expandableListTitle[listPosition]
    }

    override fun getGroupCount(): Int {
        return expandableListTitle.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        val invoiceNumber = getGroup(listPosition) as CreditRouteMonitoring.Data
        if (convertView == null) {
            var layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.row_header_expandable_item, null)
        }
        val listTitleTextView = convertView!!
            .findViewById<View>(R.id.tvName) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = invoiceNumber.creditNoteNumber
        return convertView!!
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(
        listPosition: Int,
        expandedListPosition: Int
    ): Boolean {
        return true
    }

}