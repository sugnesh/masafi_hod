package com.engear.vansales.activities.picker

import android.app.ActionBar
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.RowItemPickerLoadBinding
import com.engear.vansales.model.GeoApprovalData
import com.engear.vansales.model.LoadList
import com.engear.vansales.model.PickerLoadData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


class PickerLoadAdapter(
    var context: Context,
    var allLoaList: ArrayList<PickerLoadData.PickerDataDetail>

) : RecyclerView.Adapter<PickerLoadAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: MutableList<PickerLoadData.PickerDataDetail> = allLoaList
    var mPreferenceManager: SharedPreferenceManager? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemPickerLoadBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mLoadData = contactListFiltered[position]
        holder.binding.item = mLoadData
        holder.binding.imgAssigned.visibility = View.GONE

        holder.binding.tvDriver.text = "Driver:" +
                mLoadData.salesman!!.firstname + " " + mLoadData.salesman!!.lastname
        if (mLoadData.truck != null) {
            holder.binding.tvTruck.text = "Truck No:" + mLoadData.truck
        }

        holder.binding.executePendingBindings()

        holder.binding.root.setOnClickListener {

            if (mLoadData.status == "Picker Assign") {
                context.startActivity(
                    Intent(
                        context,
                        PickerLoadRequestDetailsActivity::class.java
                    ).apply {
                        this.putExtra("loadItem", Gson().toJson(mLoadData.load_request_detail))
                        this.putExtra("loadNo", mLoadData.load_number)
                        this.putExtra("loadDate", mLoadData.load_date)
                        this.putExtra("loadId", mLoadData.id.toString())
                        this.putExtra("uuid", mLoadData.uuid)
                        this.putExtra("status", mLoadData.current_stage)
                        this.putExtra("routeid", mLoadData.route!!.id.toString())
                        this.putExtra("salesmanid", mLoadData.salesman_id.toString())
                        this.putExtra(
                            "salesman",
                            mLoadData.salesman!!.firstname + " " + mLoadData.salesman!!.lastname
                        )
                        this.putExtra(
                            "route",
                            mLoadData.route!!.route_name + " - " + mLoadData.route!!.route_code
                        )
                    })
            }else if (mLoadData.status == "Checker Assign") {
                context.startActivity(
                    Intent(
                        context,
                        PickerLoadRequestDetailsActivity::class.java
                    ).apply {
                        this.putExtra("loadItem", Gson().toJson(mLoadData.load_request_detail))
                        this.putExtra("loadNo", mLoadData.load_number)
                        this.putExtra("loadDate", mLoadData.load_date)
                        this.putExtra("loadId", mLoadData.id.toString())
                        this.putExtra("uuid", mLoadData.uuid)
                        this.putExtra("status", mLoadData.current_stage)
                        this.putExtra("routeid", mLoadData.route!!.id.toString())
                        this.putExtra("salesmanid", mLoadData.salesman_id.toString())
                        this.putExtra(
                            "salesman",
                            mLoadData.salesman!!.firstname + " " + mLoadData.salesman!!.lastname
                        )
                        this.putExtra(
                            "route",
                            mLoadData.route!!.route_name + " - " + mLoadData.route!!.route_code
                        )
                    })
            }else if (mLoadData.status == "Loader Assign") {
                context.startActivity(
                    Intent(
                        context,
                        PickerLoadRequestDetailsActivity::class.java
                    ).apply {
                        this.putExtra("loadItem", Gson().toJson(mLoadData.load_request_detail))
                        this.putExtra("loadNo", mLoadData.load_number)
                        this.putExtra("loadId", mLoadData.id.toString())
                        this.putExtra("loadDate", mLoadData.load_date)
                        this.putExtra("uuid", mLoadData.uuid)
                        this.putExtra("status", mLoadData.current_stage)
                        this.putExtra("routeid", mLoadData.route!!.id.toString())
                        this.putExtra("salesmanid", mLoadData.salesman_id.toString())
                        this.putExtra(
                            "salesman",
                            mLoadData.salesman!!.firstname + " " + mLoadData.salesman!!.lastname
                        )
                        this.putExtra(
                            "route",
                            mLoadData.route!!.route_name + " - " + mLoadData.route!!.route_code
                        )
                    })
            }


        }


    }

    inner class ViewHolder(val binding: RowItemPickerLoadBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = allLoaList
                } else {
                    val filteredList: MutableList<PickerLoadData.PickerDataDetail> =
                        ArrayList<PickerLoadData.PickerDataDetail>()
                    for (row in allLoaList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.load_number!!.toLowerCase().contains(charString.toLowerCase())) {
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
                    filterResults.values as ArrayList<PickerLoadData.PickerDataDetail>
                notifyDataSetChanged()
            }
        }
    }


}