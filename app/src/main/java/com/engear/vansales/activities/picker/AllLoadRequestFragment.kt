package com.engear.vansales.activities.picker

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.FragmentAllLoadRequestBinding
import com.engear.vansales.databinding.FragmentLoadBinding
import com.engear.vansales.databinding.RowItemPickerLoadBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.AssignData
import com.engear.vansales.model.EODData
import com.engear.vansales.model.PickerLoadData
import com.engear.vansales.restAPI.ApiClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.opencv.android.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AllLoadRequestFragment : Fragment() {


    var binding: FragmentAllLoadRequestBinding? = null
    var db: DatabaseHandler? = null
    var mPreferenceManager: SharedPreferenceManager? = null
    var allLoadList: ArrayList<PickerLoadData.PickerDataDetail> = ArrayList()
    var mAdapter: PickerLoadAdapter? = null
    private var mDialog: Dialog? = null
    var picker_id: String = ""
    var mUserType = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_all_load_request, container, false
        )

        val view = binding!!.root

        db = DatabaseHandler(activity!!)
        mPreferenceManager = SharedPreferenceManager(activity!!)
        mUserType = mPreferenceManager!!.getUserTypeLogin()!!
        picker_id = mPreferenceManager!!.getUserId().toString()

        if (mPreferenceManager!!.getUserTypeLogin() == "3") {
            getLoad()
        } else if (mPreferenceManager!!.getUserTypeLogin() == "4") {
            getCheckerLoad()
        } else {
            getLoaderLoad()
        }

        return view
    }

    private fun getLoad() {

        showOnlyProgressDialog()

        ApiClient.getClient(activity!!)
            .getPickerLoadList(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object : Callback<PickerLoadData> {
                override fun onResponse(
                    call: Call<PickerLoadData>,
                    response: Response<PickerLoadData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            allLoadList =
                                (response.body()!!.data as ArrayList<PickerLoadData.PickerDataDetail>)

                            setUpRecycler()
                        }
                    }

                }

                override fun onFailure(call: Call<PickerLoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                }
            })
    }

    private fun getLoaderLoad() {

        showOnlyProgressDialog()

        ApiClient.getClient(activity!!)
            .getLoaderLoadList(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object : Callback<PickerLoadData> {
                override fun onResponse(
                    call: Call<PickerLoadData>,
                    response: Response<PickerLoadData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            allLoadList =
                                (response.body()!!.data as ArrayList<PickerLoadData.PickerDataDetail>)

                            setUpRecycler()
                        }
                    }

                }

                override fun onFailure(call: Call<PickerLoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                }
            })
    }

    private fun getCheckerLoad() {

        showOnlyProgressDialog()
        Log.e("Check Log","All checker")
        ApiClient.getClient(activity!!)
            .getCheckerLoadList(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object : Callback<PickerLoadData> {
                override fun onResponse(
                    call: Call<PickerLoadData>,
                    response: Response<PickerLoadData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            allLoadList =
                                (response.body()!!.data as ArrayList<PickerLoadData.PickerDataDetail>)

                            setUpRecycler()
                        }
                    }

                }

                override fun onFailure(call: Call<PickerLoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                }
            })
    }

    private fun setUpRecycler() {

        binding!!.rvLoadList.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        mAdapter = PickerLoadAdapter(activity!!, allLoadList)
        binding!!.rvLoadList.adapter = mAdapter

    }

    fun showOnlyProgressDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                return
            }
            mDialog = Dialog(activity!!)
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setCancelable(false)
            mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            mDialog!!.setContentView(R.layout.custom_only_progress_dialog)
            mDialog!!.window!!.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            mDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun dismissDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                mDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class PickerLoadAdapter(
        var context: Context,
        var allLoaList: ArrayList<PickerLoadData.PickerDataDetail>

    ) : RecyclerView.Adapter<PickerLoadAdapter.ViewHolder>(), Filterable {

        private var contactListFiltered: MutableList<PickerLoadData.PickerDataDetail> = allLoaList
        var mPreferenceManager: SharedPreferenceManager? = null

        val onItemClickListener = context as OnItemClickListener
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowItemPickerLoadBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return contactListFiltered.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val mLoadData = contactListFiltered[position]
            holder.binding.item = mLoadData

            if (mLoadData.status == "Pick Slip Generated") {
                holder.binding.imgAssigned.visibility = View.VISIBLE
            } else if (mLoadData.status == "Waiting for Checker") {
                holder.binding.imgAssigned.visibility = View.VISIBLE
            } else if (mLoadData.status == "Waiting for Loader") {
                holder.binding.imgAssigned.visibility = View.VISIBLE
            } else {
                holder.binding.imgAssigned.visibility = View.GONE
            }

            holder.binding.tvDriver.text = "Driver:" +
                    mLoadData.salesman!!.firstname + " " + mLoadData.salesman!!.lastname
            if (mLoadData.truck != null) {
                holder.binding.tvTruck.text = "Truck No:" + mLoadData.truck
            }
            holder.binding.executePendingBindings()

            holder.binding.imgAssigned.setOnClickListener {
                onItemClickListener.onItemsClick(position, "Assign")
            }

            holder.binding.root.setOnClickListener {
                onItemClickListener.onItemsClick(position, "Load")
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
                            if (row.load_number!!.toLowerCase()
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
                    contactListFiltered =
                        filterResults.values as ArrayList<PickerLoadData.PickerDataDetail>
                    notifyDataSetChanged()
                }
            }
        }

    }

    fun openLoadDetail(position: Int) {
        var mLoadData = allLoadList[position]
        if (mUserType == "3") {
            if (mLoadData.status == "Picker Assign") {
                startActivity(
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
            }
        } else if (mPreferenceManager!!.getUserTypeLogin() == "4") {
            if (mLoadData.status == "Checker Assign") {
                startActivity(
                    Intent(
                        context,
                        PickerLoadRequestDetailsActivity::class.java
                    ).apply {
                        this.putExtra("loadItem", Gson().toJson(mLoadData.load_request_detail))
                        this.putExtra("loadNo", mLoadData.load_number)
                        this.putExtra("loadDate", mLoadData.load_date)
                        this.putExtra("uuid", mLoadData.uuid)
                        this.putExtra("loadId", mLoadData.id.toString())
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
        } else if (mPreferenceManager!!.getUserTypeLogin() == "5") {
            if (mLoadData.status == "Loader Assign") {
                startActivity(
                    Intent(
                        context,
                        PickerLoadRequestDetailsActivity::class.java
                    ).apply {
                        this.putExtra("loadItem", Gson().toJson(mLoadData.load_request_detail))
                        this.putExtra("loadNo", mLoadData.load_number)
                        this.putExtra("loadDate", mLoadData.load_date)
                        this.putExtra("uuid", mLoadData.uuid)
                        this.putExtra("loadId", mLoadData.id.toString())
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

    fun showAssignedDialog(position: Int) {
        val alertDialogBuilder =
            android.app.AlertDialog.Builder(activity!!)
        alertDialogBuilder.setTitle("Alert")
            .setMessage("Are you sure you want to assigned this Load to You?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, which ->

                if (mPreferenceManager!!.getUserTypeLogin() == "3") {
                    assignedLoad(allLoadList[position].id.toString())
                } else if (mPreferenceManager!!.getUserTypeLogin() == "4") {
                    assignedCheckerLoad(allLoadList[position].id.toString())
                } else {
                    assignedLoaderLoad(allLoadList[position].id.toString())
                }

            }.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.dismiss() }

        alertDialogBuilder.show()
    }

    private fun assignedLoad(loadId: String) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", picker_id)
        jObj.addProperty("load_request_id", loadId)
        Log.e("Assign Picker", jObj.toString())
        ApiClient.getClient(activity!!)
            .assignedPicker(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object : Callback<AssignData> {
                override fun onResponse(
                    call: Call<AssignData>,
                    response: Response<AssignData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            Toast.makeText(
                                activity!!,
                                "Picker Assigned successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()

                            getLoad()
                        } else {
                            Toast.makeText(
                                activity!!,
                                "Picker Assigned fail!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            activity!!,
                            "Picker Assigned fail!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<AssignData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                    Toast.makeText(
                        activity!!,
                        "Picker Assigned fail!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun assignedCheckerLoad(loadId: String) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", picker_id)
        jObj.addProperty("load_request_id", loadId)
        Log.e("Assign Picker", jObj.toString())
        ApiClient.getClient(activity!!)
            .assignedChecker(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object : Callback<AssignData> {
                override fun onResponse(
                    call: Call<AssignData>,
                    response: Response<AssignData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            Toast.makeText(
                                activity!!,
                                "Checker Assigned successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()

                            getCheckerLoad()
                        } else {
                            Toast.makeText(
                                activity!!,
                                "Checker Assigned fail!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            activity!!,
                            "Checker Assigned fail!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<AssignData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                    Toast.makeText(
                        activity!!,
                        "Checker Assigned fail!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun assignedLoaderLoad(loadId: String) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", picker_id)
        jObj.addProperty("load_request_id", loadId)
        Log.e("Assign Loader", jObj.toString())
        ApiClient.getClient(activity!!)
            .assignedLoader(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object : Callback<AssignData> {
                override fun onResponse(
                    call: Call<AssignData>,
                    response: Response<AssignData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            Toast.makeText(
                                activity!!,
                                "Loader Assigned successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()

                            getLoaderLoad()
                        } else {
                            Toast.makeText(
                                activity!!,
                                "Loader Assigned fail!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            activity!!,
                            "Loader Assigned fail!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<AssignData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                    Toast.makeText(
                        activity!!,
                        "Loader Assigned fail!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun refreshLoad() {

        if (binding != null) {

            if (mPreferenceManager!!.getUserTypeLogin() == "3") {
                getLoad()
            } else if (mPreferenceManager!!.getUserTypeLogin() == "4") {
                getCheckerLoad()
            } else {
                getLoaderLoad()
            }

        }
    }

}