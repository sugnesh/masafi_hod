package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.databinding.RowSensorySurveyBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.SurveyPost
import com.engear.vansales.utils.Utils


class SensorySurveyAdapter(
    var context: Context,
    var allList: ArrayList<SurveyPost>
) : RecyclerView.Adapter<SensorySurveyAdapter.ViewHolder>() {

    val onItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowSensorySurveyBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mData = allList[position]
        holder.binding.item = mData
        val drawable = TextDrawable.builder()
            .buildRound(
                mData.customerName!![0].toString(),
                Utils.getMaterialColor(context, "300")
            )

        holder.binding.imgCircle.setImageDrawable(drawable)
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemsClick(position, "dialog")
        }

    }

    inner class ViewHolder(val binding: RowSensorySurveyBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

}