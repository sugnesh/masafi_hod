package com.engear.vansales.activities.supervisor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.RouteOrderAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivitySaleBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.OrderRouteMonitoring
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderActivity : BaseActivity() , OnItemClickListener {

    var orderList: List<OrderRouteMonitoring.Data> = ArrayList()

    lateinit var binding: ActivitySaleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Order")
        getSaleRoute()

    }

    /*private fun setExpandableList(data: List<OrderRouteMonitoring.Data>) {

        expandableListAdapter =
            OrderRouteMonitringAdapter(this, data)
        binding.expandebleList.setAdapter(expandableListAdapter)
        binding.expandebleList.setOnGroupExpandListener { groupPosition ->

        }
        binding.expandebleList.setOnGroupCollapseListener { groupPosition ->

        }

    }*/


    private fun getSaleRoute() {
        showOnlyProgressDialog()
        ApiClient.getClient(this)
            .getOrderDetails(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN, intent.getStringExtra("route")!!, "order"
            )
            .enqueue(object :
                Callback<OrderRouteMonitoring> {
                override fun onResponse(
                    call: Call<OrderRouteMonitoring>,
                    response: Response<OrderRouteMonitoring>
                ) {
                    dismissDialog()
                    Log.e("order", Gson().toJson(response.body()!!.data))

                    orderList = ArrayList()
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@OrderActivity,
                            "Order  Response ${response.body()!!.toString()}"
                        )
                        orderList = response.body()!!.data
                    }else{
                        Utils.logData(
                            this@OrderActivity,
                            "Order  Response fail${response.errorBody()!!.toString()}"
                        )
                    }

                    setUpRecycler()
                }

                override fun onFailure(call: Call<OrderRouteMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@OrderActivity,
                            "Order  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@OrderActivity,
                            "Order  Response API Fail"
                        )
                    }
                    setUpRecycler()

                }
            })
    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = RouteOrderAdapter(this, orderList = orderList)
    }

    override fun onItemsClick(position: Int, tag: String) {
        intent = Intent(applicationContext, OrderItemsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("OrderItem", orderList[position])
        startActivity(intent)
    }
}