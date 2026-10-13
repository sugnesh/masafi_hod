package com.engear.vansales.Approvals

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.model.ApprovalData

class HirerachyAdapter(private val dataList: List<ApprovalData>) : RecyclerView.Adapter<HirerachyAdapter
.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ApprovalData) {
            // Bind your data to your views here
            var tvname = itemView.findViewById<TextView>(R.id.tvname);
            var tvrole = itemView.findViewById<TextView>(R.id.tvrole);
            var tvStatus = itemView.findViewById<TextView>(R.id.tvStatus);

            tvname.setTypeface(tvname.getTypeface(), Typeface.BOLD_ITALIC)
            tvrole.setTypeface(tvrole.getTypeface(), Typeface.BOLD_ITALIC)
            tvStatus.setTypeface(tvStatus.getTypeface(), Typeface.BOLD_ITALIC)

            tvname.text="Name:"  +"${data.user.firstname} ${data.user.lastname}"
            tvrole.text ="Role:"  +"${data.user.role.name}"
            tvStatus.text = "Status:"  +"${data.approval_status}"

            // Add more bindings as needed
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.hirearchy_list_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size
}
