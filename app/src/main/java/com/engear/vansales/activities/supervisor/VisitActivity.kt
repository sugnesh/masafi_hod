package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.CustomerVisitAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivitySaleBinding
import com.engear.vansales.databinding.ActivityVisitBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.VisitData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VisitActivity : BaseActivity(), OnShopItemClickListener {
    var listNewCustomers: List<VisitData.Data> = ArrayList()
    lateinit var binding: ActivityVisitBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Visit")
        getCustomer()

    }

    fun setAdapter() {

        var adapterCustomer = CustomerVisitAdapter(this, listNewCustomers)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.rvList.layoutManager = layoutManager
        binding.rvList.itemAnimator = DefaultItemAnimator()
        binding.rvList.adapter = adapterCustomer

    }

    private fun getCustomer() {
        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getVisitDetail(
                "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, intent.getStringExtra("route")!!, "visit"
            )
            .enqueue(object :
                Callback<VisitData> {
                override fun onResponse(
                    call: Call<VisitData>,
                    response: Response<VisitData>
                ) {
                    dismissDialog()
                    Log.e("order", Gson().toJson(response.body()!!.data))

                    listNewCustomers = ArrayList()
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@VisitActivity,
                            "Visit  Response b${response.body()!!.toString()}"
                        )
                        listNewCustomers = response.body()!!.data
                    }

                    setAdapter()
                }

                override fun onFailure(call: Call<VisitData>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setAdapter()
                    if (error.message != null) {
                        Utils.logData(
                            this@VisitActivity,
                            "Visit  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@VisitActivity,
                            "vist  Response API Fail"
                        )
                    }

                }
            })
    }


    override fun onShopItemClick(position: Int, tag: String, type: String) {

    }
}