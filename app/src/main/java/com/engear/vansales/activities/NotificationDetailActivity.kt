package com.engear.vansales.activities

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.LoadIRequestAdapter
import com.engear.vansales.adapter.NotificationAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityNotificationBinding
import com.engear.vansales.databinding.ActivityNotificationDetailBinding
import com.engear.vansales.model.LoadRequestData
import com.engear.vansales.model.NotificationData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationDetailActivity : BaseActivity() {

    lateinit var binding: ActivityNotificationDetailBinding
    var type = ""
    var uuID = ""
    private var allList: List<LoadRequestData.LoadRequestView.ItemDetail>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@NotificationDetailActivity,
            "On Notification details Screen "
        )
        init()
    }

    private fun init() {

        type = intent.getStringExtra("Type")!!
        uuID = intent.getStringExtra("UUID")!!

        setToolbarTitle("Load Request")

        getNotificationList()

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = LoadIRequestAdapter(this, allList!!)
    }

    private fun getNotificationList() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getLoadRequestDetail(
                "Bearer ${userInfo!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                uuID
            )
            .enqueue(object : Callback<LoadRequestData> {
                override fun onResponse(
                    call: Call<LoadRequestData>,
                    response: Response<LoadRequestData>
                ) {

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            allList = ArrayList()

                            if (response.body()!!.data != null) {
                                allList = response.body()!!.data.itemDetil
                            }

                        }
                    }

                    setUpRecycler()
                    dismissDialog()
                }

                override fun onFailure(call: Call<LoadRequestData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                }
            })
    }
}