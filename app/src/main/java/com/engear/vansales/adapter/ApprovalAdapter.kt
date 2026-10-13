package com.engear.vansales.Approvals

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.activities.ApprovalDetailActiivty
import com.engear.vansales.model.ApprovalRes
import com.engear.vansales.utils.Utils

class ApprovalAdapter(private val dataList: List<ApprovalRes.Data?>?) :
    RecyclerView.Adapter<ApprovalAdapter
    .DataViewHolder>() {
    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: ApprovalRes.Data) {
            // Bind your data to your views here

            var tvdate = itemView.findViewById<TextView>(R.id.tvdate);
            var tvRouteCode = itemView.findViewById<TextView>(R.id.tvRouteCode);
            var tvSalesMan = itemView.findViewById<TextView>(R.id.tvSalesMan);
            var tvSite = itemView.findViewById<TextView>(R.id.tvSite);
            var tvStatus = itemView.findViewById<TextView>(R.id.tvStatus);

            tvdate.setTypeface(tvdate.typeface, Typeface.BOLD_ITALIC)
            tvRouteCode.setTypeface(
                tvRouteCode.getTypeface(),
                Typeface.BOLD_ITALIC
            )
            tvSalesMan.setTypeface(tvSalesMan.getTypeface(), Typeface.BOLD_ITALIC)
            tvSite.setTypeface(tvSite.getTypeface(), Typeface.BOLD_ITALIC)
            tvStatus.setTypeface(tvStatus.getTypeface(), Typeface.BOLD_ITALIC)

            tvdate.text = "Date: " + Utils.formatLocalTime(
                data
                    .created_at.orEmpty(), "yyyy-MM-dd hh:mm:ss a"
            )
            //itemView.findViewById<TextView>(R.id.tvRouteCode).text = "Route Code: "+"${data.id}"
            tvRouteCode.text = "Type: " + "${data.approval_type!!}"
            tvSalesMan.text = "SalesMan: " +
                    "(${data.salesman?.user_id}) ${data.salesman?.user?.firstname}  ${data.salesman?.user?.lastname}"
            tvSite.text = "Site: " +
                    "(${data.customer?.customer_code}) ${data.customer?.user?.firstname}  ${data.customer?.user?.lastname}"
            tvStatus.text = "Status: " + "${
                data
                    .approval_status
            }"

            // Add more bindings as needed


            itemView.setOnClickListener {
                itemView.context.startActivity(
                    Intent(
                        itemView.context,
                        ApprovalDetailActiivty::class.java
                    ).apply {
                        putExtra("site_code", "${data.customer?.customer_code}")
                        putExtra(
                            "site_name",
                            "${data.customer?.user?.firstname}  ${data.customer?.user?.lastname}"
                        )
                        putExtra(
                            "salesman",
                            "(${data.salesman?.user_id})${data.salesman?.user?.firstname}  ${data.salesman?.user?.lastname}"
                        )
                        // putExtra("route_code", "${data.id}")
                        //putExtra("route_code", "${Utils.isrouteid}")
                        putExtra("request_limit", "${data.requested_amount}")
                        putExtra("request_day", "${data.no_of_days_requested}")
                        putExtra("overdue_balance", "${data.overdue_amount}")
                        putExtra("approve_days", "${data.no_of_days_approved}")
                        putExtra("approve_limit", "${data.approved_amount}")
                        putExtra("status", "${data.approval_status}")
                        putExtra("type", "${data.approval_type}")
                        putExtra("object_id", data.id.toString())
                    })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.approval_list_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        dataList?.get(position)?.let { holder.bind(it) }
    }


    override fun getItemCount() = dataList?.size ?: 0
}
