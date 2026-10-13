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
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.CustomerData
import com.engear.vansales.utils.Utils


class CustomerAdapter(
    var context: Context,
    var allCustomerList: ArrayList<CustomerData.DataCustomer>,
    var type: String
) : RecyclerView.Adapter<CustomerAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: ArrayList<CustomerData.DataCustomer> = allCustomerList
    private var mtype: String = type
    val onItemClickListener = context as OnShopItemClickListener
    private var db: DatabaseHandler = DatabaseHandler(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowCustomerItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mCustomerData = contactListFiltered[position]
        holder.binding.item = mCustomerData

        if (mCustomerData.customer_group_id != null) {
            if (mCustomerData.customer_group_id!!.isEmpty()) {
                holder.binding.tvName.text =
                    mCustomerData.userDetail.firstname + " " + mCustomerData.userDetail.lastname
            } else {
                holder.binding.tvName.text = mCustomerData.customer_group_id
            }

        } else {
            holder.binding.tvName.text =
                mCustomerData.userDetail.firstname + " " + mCustomerData.userDetail.lastname
        }

        if (mtype == "sequence") {
            var mAddress = db!!.getDeliveryAddress(mCustomerData.user_id!!.toString())

            if (mAddress.isEmpty()) {
                holder.binding.tvAddess!!.text =
                    "Address:" + mCustomerData.customer_address_1
            } else {
                holder.binding.tvAddess!!.text =
                    "Address:" + mAddress
            }

//            var mLastVisit = db!!.getLastLocusVisitSequnece()
//            var mLastVisitSTatus = db!!.getLastLocusVisitSequneceStatus()
//            if (mLastVisitSTatus == "1") {
//                if (((position)) == (mLastVisit.toInt())) {
//                    holder.binding.lytMainview!!.alpha = 1f
//                    holder.binding.lytMainview!!.isEnabled = true
//                } else if (((position + 1)) <= (mLastVisit.toInt())) {
//                    holder.binding.lytMainview!!.alpha = 1f
//                    holder.binding.lytMainview!!.isEnabled = true
//                } else {
//                    holder.binding.lytMainview!!.alpha = 0.5f
//                    holder.binding.lytMainview!!.isEnabled = false
//                    holder.binding.lytMainview!!.isClickable = false
//                }
//
//            } else {
//                if (((position)) == (mLastVisit.toInt())) {
//                    holder.binding.lytMainview!!.alpha = 1f
//                    holder.binding.lytMainview!!.isEnabled = true
//                } else if (((position + 1)) <= (mLastVisit.toInt())) {
//                    holder.binding.lytMainview!!.alpha = 1f
//                    holder.binding.lytMainview!!.isEnabled = true
//                } else {
//                    holder.binding.lytMainview!!.alpha = 0.5f
//                    holder.binding.lytMainview!!.isEnabled = false
//                    holder.binding.lytMainview!!.isClickable = false
//                }
//            }

            holder.binding.lytMainview!!.alpha = 1f
            holder.binding.lytMainview!!.isEnabled = true
        } else {
            holder.binding.tvAddess!!.text =
                "Address:" + mCustomerData.customer_address_1
        }


        var isDayBlaock = db!!.isCustomerDayBlock(mCustomerData.user_id!!.toString())
        var isLimitBlaock = db!!.isCustomerLimitBlock(mCustomerData.user_id!!.toString())
        if (isDayBlaock == "1" || isLimitBlaock == "1") {
            holder.binding.tvName.setTextColor(Color.RED)
            holder.binding.tvId.setTextColor(Color.RED)
        } else {
            holder.binding.tvId.setTextColor(Color.parseColor("#363635"))
            holder.binding.tvName.setTextColor(Color.parseColor("#4A4A4A"))
        }
//        if (mCustomerData.isOverDue == "1") {
//            holder.binding.tvName.setTextColor(Color.RED)
//            holder.binding.tvId.setTextColor(Color.RED)
//        } else if (mCustomerData.isOverDue == "2") {
//            holder.binding.tvName.setTextColor(Color.RED)
//            holder.binding.tvId.setTextColor(Color.RED)
//        } else {
//            holder.binding.tvId.setTextColor(Color.parseColor("#363635"))
//            holder.binding.tvName.setTextColor(Color.parseColor("#4A4A4A"))
//        }

        if (db!!.getCustomerGeoStatus(mCustomerData.user_id.toString()) == "Send") {
            holder.binding.btnGeorequest!!.visibility = View.VISIBLE
            holder.binding.ivGeo!!.setImageResource(R.drawable.ic_request_send)
        } else if (db!!.getCustomerGeoStatus(mCustomerData.user_id.toString()) == "Approve") {
            holder.binding.btnGeorequest!!.visibility = View.VISIBLE
            holder.binding.ivGeo!!.setImageResource(R.drawable.ic_aprove)
        } else if (db!!.getCustomerGeoStatus(mCustomerData.user_id.toString()) == "Reject") {
            holder.binding.btnGeorequest!!.visibility = View.VISIBLE
            holder.binding.ivGeo!!.setImageResource(R.drawable.rejected)
        } else {
            holder.binding.btnGeorequest!!.visibility = View.GONE
        }

        holder.binding.executePendingBindings()

        if (db!!.getCustomerShopStatus(mCustomerData.user_id.toString()) == "1") {
            holder.binding.ivShopClose.visibility = View.VISIBLE
        } else {
            holder.binding.ivShopClose.visibility = View.GONE
        }

        if (mtype == "sequence") {
            val drawable = TextDrawable.builder()
                .buildRound(
                    mCustomerData.planSeq.toString(),
                    Utils.getMaterialColor(context, "300")
                )
            holder.binding.imgCircle.setImageDrawable(drawable)
        } else {
            val drawable = TextDrawable.builder()
                .buildRound(
                    "A",
                    Utils.getMaterialColor(context, "300")
                )

            holder.binding.imgCircle.setImageDrawable(drawable)
       }


        holder.itemView.setOnClickListener {
            onItemClickListener.onShopItemClick(mCustomerData.userDetail.id, "dialog", type)
        }

        holder.binding.ivMap.setOnClickListener {
            onItemClickListener.onShopItemClick(mCustomerData.userDetail.id, "location", type)
        }

        holder.binding.imgIsDeliveries.setOnClickListener {
            onItemClickListener.onShopItemClick(mCustomerData.userDetail.id, "delivery", type)
        }

        holder.binding.iconPhone!!.setOnClickListener {
            onItemClickListener.onShopItemClick(mCustomerData.userDetail.id, "phone", type)
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

        if (mCustomerData.cust_sale == "1") {
            holder.binding.imgSmallSales.visibility = View.VISIBLE
        } else {
            holder.binding.imgSmallSales.visibility = View.GONE
        }

        if (mCustomerData.cust_order == "1") {
            holder.binding.imgSmallOrder.visibility = View.VISIBLE
        } else {
            holder.binding.imgSmallOrder.visibility = View.GONE
        }

        if (mCustomerData.cust_coll == "1") {
            holder.binding.imgSmallCollection.visibility = View.VISIBLE
        } else {
            holder.binding.imgSmallCollection.visibility = View.GONE
        }

        if (mCustomerData.cust_delviery == "1") {
            holder.binding.imgSmallDelivey.visibility = View.VISIBLE
        } else {
            holder.binding.imgSmallDelivey.visibility = View.GONE
        }

        if (mCustomerData.cust_return == "1") {
            holder.binding.imgSmallReturn!!.visibility = View.VISIBLE
        } else {
            holder.binding.imgSmallReturn!!.visibility = View.GONE
        }

        if (mCustomerData.cust_merchan == "1") {
            holder.binding.imgSmallMerch.visibility = View.VISIBLE
        } else {
            holder.binding.imgSmallMerch.visibility = View.GONE
        }

        if (db!!.getDelveiryCountCustomer(mCustomerData.user_id.toString()) > 0) {
            holder.binding.imgIsDeliveries!!.visibility = View.VISIBLE
            if (db!!.getDelveiryCommentCustomer(mCustomerData.user_id.toString()).isNotEmpty()) {
                holder.binding.tvComment!!.visibility = View.VISIBLE
                holder.binding.tvComment.text =
                    "Comment: " + db!!.getDelveiryCommentCustomer(mCustomerData.user_id.toString())
            } else {
                holder.binding.tvComment!!.visibility = View.GONE
            }
            if (db!!.getDelveiryPrepaidCountCustomer(mCustomerData.user_id.toString()) > 0) {
                holder.binding.imgPrepaid!!.visibility = View.VISIBLE
            } else {
                holder.binding.imgPrepaid!!.visibility = View.GONE
            }

            if (db!!.getDelveiryExpressCountCustomer(mCustomerData.user_id.toString()) > 0) {
                holder.binding.tvExpressInsa!!.text = "Express Delivery"
                holder.binding.tvExpressInsa!!.visibility = View.VISIBLE
            } else {
                if (db!!.getDelveiryInstaShopCountCustomer(mCustomerData.user_id.toString()) > 0) {
                    holder.binding.tvExpressInsa!!.text = "Instashop Delivery"
                    holder.binding.tvExpressInsa!!.visibility = View.VISIBLE
                } else {
                    holder.binding.tvExpressInsa!!.visibility = View.GONE
                }
            }
        } else {
            holder.binding.imgIsDeliveries!!.visibility = View.VISIBLE
            holder.binding.imgPrepaid!!.visibility = View.GONE
            holder.binding.tvComment!!.visibility = View.GONE
        }
    }


    inner class ViewHolder(val binding: RowCustomerItemBinding) :
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
                        var firstname = ""
                        var lastname = ""
                        var customercode = ""
                        var mPhoenNumber = ""

                        if (row.userDetail.firstname != null) {
                            firstname = row.userDetail.firstname
                        }
                        if (row.userDetail.lastname != null) {
                            lastname = row.userDetail.lastname
                        }

                        if (row.customer_code != null) {
                            customercode = row.customer_code
                        }

                        if (row.customer_phone != null) {
                            mPhoenNumber = row.customer_phone!!
                        }

                        if (firstname.toLowerCase()
                                .contains(charString.toLowerCase()) ||
                            lastname.toLowerCase()
                                .contains(charString.toLowerCase()) ||
                            customercode.toLowerCase()
                                .contains(charString.toLowerCase()) ||
                            mPhoenNumber.toLowerCase()
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