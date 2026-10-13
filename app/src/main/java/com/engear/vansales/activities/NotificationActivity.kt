package com.engear.vansales.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.NotificationAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityNotificationBinding
import com.engear.vansales.databinding.OrderCommentDialogBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.BlockData
import com.engear.vansales.model.NotificationData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityNotificationBinding
    private var allList: List<NotificationData.NotificationView>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@NotificationActivity,
            "On Notification Screen "
        )
        init()
    }

    private fun init() {

        setToolbarTitle("Notification")

        userInfo = mPreferenceManager!!.getUserInfo()

        getNotificationList()

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = NotificationAdapter(this, allList)
    }

    override fun onItemsClick(position: Int, tag: String) {
        var type = allList!![position].type

        if (type == "Load Request") {
            startActivity(
                Intent(this, NotificationDetailActivity::class.java)
                    .putExtra("Type", type)
                    .putExtra("UUID", allList!![position].uuid)
            )
        } else if (type == "Geo Approval") {

            if (tag == "Approve") {
                sendApprovalRequest(allList!![position].uuid, "Approve", "")
            } else if (tag == "Reject") {
                orderCommentDialog(position)
            }
        } else if (type == "Over Due" || type == "Credit Limit") {

            if (tag == "Approve") {
                sendOverDueApprovalRequest(allList!![position].uuid, "Approve", "")
            } else if (tag == "Reject") {
                orderCommentDialog(position)
            }
        }
    }

    private fun orderCommentDialog(position: Int) {

        val factory = LayoutInflater.from(this)
        val binding: OrderCommentDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.order_comment_dialog, null, false)

        val deleteDialogView: View = binding.root

        binding.txtName.text = "Enter Reason"

        binding.lytLPO.visibility = View.GONE
        binding.lytInvoiceNo.visibility = View.GONE
        binding.lytInvoiceAmt.visibility = View.GONE

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        binding.btnConfirm.setOnClickListener {

            deleteDialog.dismiss()
            var orderComment = binding.edtComment.text.toString()

            sendApprovalRequest(allList!![position].uuid, "Reject", orderComment)

        }

        deleteDialog.show()
    }


    private fun getNotificationList() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getNotificationList(
                "Bearer ${userInfo!!.data.accessToken}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object : Callback<NotificationData> {
                override fun onResponse(
                    call: Call<NotificationData>,
                    response: Response<NotificationData>
                ) {

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            allList = ArrayList()
                            allList = response.body()!!.data
                        }
                    }

                    dismissDialog()
                    setUpRecycler()

                }

                override fun onFailure(call: Call<NotificationData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                }
            })
    }

    private fun sendApprovalRequest(uuid: String, status: String, reason: String) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("uuid", uuid)
        jObj.addProperty("status", status)
        jObj.addProperty("reason", reason)

        Log.e("GeoApproval request", jObj.toString())

        ApiClient.getClient(this).acceptGeoApprovalRequest(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<BlockData> {
            override fun onResponse(
                call: Call<BlockData>,
                response: Response<BlockData>
            ) {
                Log.e("CustomerBlock Success", "Success")
                dismissDialog()
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        Toast.makeText(
                            this@NotificationActivity,
                            "Request sent successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        getNotificationList()
                    } else {
                        Toast.makeText(
                            this@NotificationActivity,
                            "Request sent fail!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@NotificationActivity,
                        "Request sent fail!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<BlockData>, error: Throwable) {
                Log.e("CustomerBlock Fail", "Fail")
                dismissDialog()
                Toast.makeText(
                    this@NotificationActivity,
                    "Request sent fail!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun sendOverDueApprovalRequest(uuid: String, status: String, reason: String) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("uuid", uuid)
        jObj.addProperty("status", status)
        jObj.addProperty("reason", reason)

        Log.e("GeoApproval request", jObj.toString())

        ApiClient.getClient(this).acceptOverDueApprovalRequest(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<BlockData> {
            override fun onResponse(
                call: Call<BlockData>,
                response: Response<BlockData>
            ) {
                Log.e("CustomerBlock Success", "Success")
                dismissDialog()
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        Toast.makeText(
                            this@NotificationActivity,
                            "Request sent successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        getNotificationList()
                    } else {
                        Toast.makeText(
                            this@NotificationActivity,
                            "Request sent fail!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@NotificationActivity,
                        "Request sent fail!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<BlockData>, error: Throwable) {
                Log.e("CustomerBlock Fail", "Fail")
                dismissDialog()
                Toast.makeText(
                    this@NotificationActivity,
                    "Request sent fail!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}