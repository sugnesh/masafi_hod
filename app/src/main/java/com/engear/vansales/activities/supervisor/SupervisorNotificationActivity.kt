package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.NotificationAdapter
import com.engear.vansales.adapter.SupervisorNotificationAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityNotificationBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.NotificationData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupervisorNotificationActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityNotificationBinding
    private var allList: List<NotificationData.NotificationView>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Notification")

//        userInfo = mPreferenceManager!!.getUserInfo()

        getNotificationList()

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = SupervisorNotificationAdapter(this, allList)
    }

    override fun onItemsClick(position: Int, tag: String) {

    }

    private fun getNotificationList() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getNotificationList(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object : Callback<NotificationData> {
                override fun onResponse(
                    call: Call<NotificationData>,
                    response: Response<NotificationData>
                ) {

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            Utils.logData(
                                this@SupervisorNotificationActivity,
                                "Supervisor notification Response${response.body().toString()}"
                            )
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
                    if (error.message != null) {
                        Utils.logData(
                            this@SupervisorNotificationActivity,
                            "Supervisor notification Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SupervisorNotificationActivity,
                            "Supervisor notification Response API Fail"
                        )
                    }
                }
            })
    }
}