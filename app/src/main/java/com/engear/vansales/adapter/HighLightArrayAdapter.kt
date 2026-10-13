package com.engear.vansales.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.engear.vansales.R
import java.util.ArrayList

class HighLightArrayAdapter : ArrayAdapter<String> {

    private var mSelectedIndex = -1

    var mContext: Context


    fun setSelection(position: Int) {
        mSelectedIndex = position
        notifyDataSetChanged()
    }

   constructor(context: Context, resource: Int, id: Int, objects: ArrayList<String>) : super(
        context,
        resource,
        objects
    ) {
        mContext = context

    }

    constructor(context: Context, resource: Int, objects: ArrayList<String>) : super(context, resource, objects) {
        mContext = context
    }



    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = super.getDropDownView(position, convertView, parent)

        if (position == mSelectedIndex) {
            itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.selected))
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.white))
        }


        return itemView
    }

    override fun getCount(): Int {
        val count = super.getCount()
        return if (count > 0) count - 1 else count
    }
}