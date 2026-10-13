package com.engear.vansales.adapter

import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.activities.LoadConfirmActivity
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.DialogSimpleBinding
import com.engear.vansales.databinding.RowItemLoadBinding
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.GeoApprovalData
import com.engear.vansales.model.Label
import com.engear.vansales.model.LoadList
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessController.getContext
import java.util.*


class LoadAdapter(
    var context: Context,
    var allLoaList: MutableList<LoadList>

) : RecyclerView.Adapter<LoadAdapter.ViewHolder>(), Filterable {

    private var contactListFiltered: MutableList<LoadList> = allLoaList
    var mPreferenceManager: SharedPreferenceManager? = null
    var strloadNumber: String =""
    var strLoadType: String=""
    var strLoadId:String=""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemLoadBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mLoadData = contactListFiltered[position]
        holder.binding.item = mLoadData
        holder.binding.executePendingBindings()
        mPreferenceManager = SharedPreferenceManager(context)
        var salesmen_current_radius =
            mPreferenceManager!!.getString(AppConstants.SALESMENCURRENTRADIUS).toString()

        var warehouse_radius =
            mPreferenceManager!!.getString(AppConstants.WAREHOUSE_RADIUS).toString()

        holder.binding.root.setOnClickListener {
            if (mLoadData.loadStatus == "0") {
                strloadNumber=mLoadData.loadNumber.toString()
                strLoadType=mLoadData.loadType.toString()
                strLoadId=mLoadData.id.toString()

                try{
                   if (salesmen_current_radius.toDouble() >= warehouse_radius.toDouble()) {
                      //  dialogPrint(context, mLoadData.loadNumber, mLoadData.loadType, mLoadData.id,salesmen_current_radius)
                       context.startActivity(Intent(context, LoadConfirmActivity::class.java).apply {
                           this.putExtra("loadNo", mLoadData.loadNumber)
                           this.putExtra("loadType", mLoadData.loadType)
                           this.putExtra("loadID", mLoadData.id)
                       })
                    } else {
                        context.startActivity(Intent(context, LoadConfirmActivity::class.java).apply {
                            this.putExtra("loadNo", mLoadData.loadNumber)
                            this.putExtra("loadType", mLoadData.loadType)
                            this.putExtra("loadID", mLoadData.id)
                        })
                    }
                }catch (e:Exception){
                    context.startActivity(Intent(context, LoadConfirmActivity::class.java).apply {
                        this.putExtra("loadNo", mLoadData.loadNumber)
                        this.putExtra("loadType", mLoadData.loadType)
                        this.putExtra("loadID", mLoadData.id)
                    })
                }

            }
            /* context.startActivity(Intent(context, LoadConfirmActivity::class.java).apply {
                 this.putExtra("loadNo", mLoadData.loadNumber)
                 this.putExtra("loadType", mLoadData.loadType)
                 this.putExtra("loadID",mLoadData.id)
             })*/
        }

        if (mLoadData.loadStatus == "0")
            holder.binding.imgVerified.setImageResource(R.drawable.ic_icon_not_verified)
        else if (mLoadData.loadDate == "1")
            holder.binding.imgVerified.setImageResource(R.drawable.ic_icon_verified_sel)

    }

    inner class ViewHolder(val binding: RowItemLoadBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = allLoaList
                } else {
                    val filteredList: MutableList<LoadList> =
                        ArrayList<LoadList>()
                    for (row in allLoaList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.loadNumber!!.toLowerCase().contains(charString.toLowerCase())) {
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
                contactListFiltered = filterResults.values as ArrayList<LoadList>
                notifyDataSetChanged()
            }
        }
    }

    fun dialogPrint(
        mContext: Context?,
        loadNumber: String?,
        loadType: String?,
        id: String,
        salesmen_current_radius: String
    ) {
        val alertDialog = Dialog(mContext!!)
        alertDialog.setCancelable(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.dialog_load_request_permission)
        //alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var btnConfirm: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnConfirm)

        var btnCancel: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnCancel)

        btnConfirm!!.setOnClickListener {
            alertDialog.dismiss()
          sendApprovalRequest(loadNumber!!,salesmen_current_radius)
        /*  context.startActivity(Intent(context, LoadConfirmActivity::class.java).apply {
                this.putExtra("loadNo", loadNumber)
                this.putExtra("loadType", loadType)
                this.putExtra("loadID", id)
            })*/
        }

        btnCancel!!.setOnClickListener {
            alertDialog.dismiss()
       /*     context.startActivity(Intent(context, LoadConfirmActivity::class.java).apply {
                this.putExtra("loadNo", loadNumber)
                this.putExtra("loadType", loadType)
                this.putExtra("loadID", id)
            })*/
        }

        alertDialog.show()
    }



    private var mDialog: Dialog? = null
    var mProgressDialog: ProgressDialog? = null
    fun showOnlyProgressDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                return
            }
            mDialog = Dialog(context)
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

    fun showWaitDialog() {
        mProgressDialog = ProgressDialog(context)
        mProgressDialog!!.setMessage("Please wait until supervisor approve..."); // Setting Message
        mProgressDialog!!.setTitle(""); // Setting Title
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog!!.show(); // Display Progress Dialog
        mProgressDialog!!.setCancelable(false);
    }
    private fun sendApprovalRequest(
        loadNumber: String,
        salesmen_current_radius: String
    ) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserInfo()!!.data.userInfo.id)
        jObj.addProperty("salesman_lat", AppConstants.Latitude)
        jObj.addProperty("salesman_long", AppConstants.Longitude)
        jObj.addProperty("load_no", loadNumber)
        jObj.addProperty(
            "supervisor_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.supervisor.id
        )
        jObj.addProperty("radius", salesmen_current_radius)
        Log.e("GeoApproval request", jObj.toString())

        ApiClient.getClient(context).sendLoadGeoApprovalRequest(
            "Bearer ${BaseActivity.userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<GeoApprovalData> {
            override fun onResponse(
                call: Call<GeoApprovalData>,
                response: Response<GeoApprovalData>
            ) {
                Log.e("CustomerBlock Success", "Success")
                Log.e("GeoApproval Body", response.body().toString())
                dismissDialog()
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        showWaitDialog()
                        // Utils.displayAlert(context, "Request Sent,Waiting for Approval.")
                    } else {
                        Utils.displayAlert(context, "Request Sent fail!!")
                    }
                } else {
                    Utils.displayAlert(context, "Request Sent fail!!")
                }

            }

            override fun onFailure(call: Call<GeoApprovalData>, error: Throwable) {
                Log.e("CustomerBlock Fail", error.toString())
                dismissDialog()
                Utils.displayAlert(context, "Request Sent fail!!")
            }
        })
    }


}