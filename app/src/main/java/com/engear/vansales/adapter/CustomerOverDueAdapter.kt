package com.engear.vansales.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.engear.vansales.R
import com.engear.vansales.databinding.RowCustomerItemBinding
import com.engear.vansales.databinding.RowCustomerOverdueItemBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.CustomerData
import com.engear.vansales.utils.Utils


class CustomerOverDueAdapter(
    var context: Context,
    var allCustomerList: ArrayList<CustomerData.DataCustomer>,
    var type: String
) : RecyclerView.Adapter<CustomerOverDueAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: ArrayList<CustomerData.DataCustomer> = allCustomerList

    val onItemClickListener = context as OnShopItemClickListener
    private var db: DatabaseHandler = DatabaseHandler(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowCustomerOverdueItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mCustomerData = contactListFiltered[position]
        holder.binding.item = mCustomerData
        var isDayBlaock = db!!.isCustomerDayBlock(mCustomerData.user_id!!.toString())
        if (isDayBlaock == "1") {
            holder.binding.tvPhone.visibility = View.VISIBLE
            holder.binding.tvName.setTextColor(Color.RED)
            holder.binding.tvId.setTextColor(Color.RED)
        } else {
            holder.binding.tvPhone.visibility = View.GONE
        }

        var isLimitBlaock = db!!.isCustomerLimitBlock(mCustomerData.user_id!!.toString())
        if (isLimitBlaock == "1") {
            holder.binding.tvLimitBlock.visibility = View.VISIBLE
            holder.binding.tvName.setTextColor(Color.RED)
            holder.binding.tvId.setTextColor(Color.RED)
        } else {
            holder.binding.tvLimitBlock.visibility = View.GONE
        }

//        if (mCustomerData.isOverDue == "1") {
//            holder.binding.tvLimitBlock.visibility = View.GONE
//            holder.binding.tvPhone.visibility = View.VISIBLE
//            holder.binding.tvName.setTextColor(Color.RED)
//            holder.binding.tvId.setTextColor(Color.RED)
//        }else if (mCustomerData.isOverDue == "2") {
//            holder.binding.tvPhone.visibility = View.GONE
//            holder.binding.tvLimitBlock.visibility = View.VISIBLE
//            holder.binding.tvName.setTextColor(Color.RED)
//            holder.binding.tvId.setTextColor(Color.RED)
//        } else {
//            holder.binding.tvId.setTextColor(Color.parseColor("#363635"))
//            holder.binding.tvName.setTextColor(Color.parseColor("#4A4A4A"))
//        }
        holder.binding.executePendingBindings()

        var mRequetsend = db!!.getCustomerUnblockRequestSend(mCustomerData.user_id.toString())

        if (mRequetsend == "1") {
            holder.binding.ivMap.visibility = View.VISIBLE
            holder.binding.lytRequest.visibility = View.VISIBLE
            holder.binding.tvLimit.text =
                "Request Limit: " + db!!.getCustomerUnblockRequestLimit(mCustomerData.user_id.toString())
            holder.binding.tvDay.text =
                "Request Days: " + db!!.getCustomerUnblockRequestDays(mCustomerData.user_id.toString())
            holder.binding.ivMap.setImageResource(R.drawable.ic_request_send)
        } else if (mRequetsend == "2") {
            holder.binding.ivMap.visibility = View.VISIBLE
            holder.binding.ivMap.setImageResource(R.drawable.ic_aprove)
        } else {
            holder.binding.ivMap.visibility = View.GONE
        }
        val drawable = TextDrawable.builder()
            .buildRound(
                mCustomerData.userDetail.firstname[0].toString(),
                Utils.getMaterialColor(context, "300")
            )

        holder.binding.imgCircle.setImageDrawable(drawable)

        holder.itemView.setOnClickListener {
            onItemClickListener.onShopItemClick(mCustomerData.userDetail.id, "dialog", type)
        }

        holder.binding.ivMap.setOnClickListener {
            onItemClickListener.onShopItemClick(mCustomerData.userDetail.id, "location", type)
        }

        if (mCustomerData.customer_type_id == "2") {
            holder.binding.imgColor.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_blue
                )
            )
        } else if (mCustomerData.customer_type_id == "3") {
            holder.binding.imgColor.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_yellow
                )
            )
        } else {
            holder.binding.imgColor.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorRed
                )
            )
        }

    }


    inner class ViewHolder(val binding: RowCustomerOverdueItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = allCustomerList
                } else {
                    val filteredList: ArrayList<CustomerData.DataCustomer> =
                        ArrayList<CustomerData.DataCustomer>()
                    for (row in allCustomerList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.userDetail.firstname!!.toLowerCase()
                                .contains(charString.toLowerCase()) ||
                            row.userDetail.lastname!!.toLowerCase()
                                .contains(charString.toLowerCase()) ||
                            row.customer_code!!.toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
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
                contactListFiltered = filterResults.values as ArrayList<CustomerData.DataCustomer>
                notifyDataSetChanged()
            }
        }
    }
}