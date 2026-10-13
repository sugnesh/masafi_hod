package com.engear.vansales.activities.supervisor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ExpandableListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.CreditRouteMonitringAdapter
import com.engear.vansales.adapter.RouteCreditAdapter
import com.engear.vansales.adapter.RouteSalesAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivitySaleBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CreditRouteMonitoring
import com.engear.vansales.model.SaleMonitoring
import com.engear.vansales.model.SaleRouteMonitoring
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreditActivity : BaseActivity(), OnItemClickListener {

    var creditList: List<SaleMonitoring.Data> = ArrayList()

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
        setToolbarTitle("Credit")
        getSaleRoute()

    }

    private fun getSaleRoute() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", intent.getStringExtra("salesmanId"))
        jObj.addProperty("role_id", "1")
        jObj.addProperty("from_date", Utils.getCurrentDate())
        jObj.addProperty("to_date", Utils.getCurrentDate())
        Log.e("Salesman request", jObj.toString())

        ApiClient.getClient(this)
            .getTotalReturn(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                intent.getStringExtra("salesmanId")!!,
                "1",
                Utils.getCurrentDate(),
                Utils.getCurrentDate()
            )
            .enqueue(object :
                Callback<SaleMonitoring> {
                override fun onResponse(
                    call: Call<SaleMonitoring>,
                    response: Response<SaleMonitoring>
                ) {

                    dismissDialog()

                    creditList = ArrayList()
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@CreditActivity,
                            "Sale  Response ${response.body().toString()}"
                        )
                        creditList = response.body()!!.data
                    }
                    setUpRecycler()
                }

                override fun onFailure(call: Call<SaleMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setUpRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@CreditActivity,
                            "Sale  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@CreditActivity,
                            "Sale  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = RouteCreditAdapter(this, creditList)
    }

    override fun onItemsClick(position: Int, tag: String) {
//        intent = Intent(applicationContext, ReturnItemsActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        intent.putExtra("ReturnItem", creditList[position])
//        startActivity(intent)
    }
}