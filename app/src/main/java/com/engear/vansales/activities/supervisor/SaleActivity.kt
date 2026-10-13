package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.RouteSalesAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivitySaleBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.SaleMonitoring
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class SaleActivity : BaseActivity(), OnItemClickListener {
    var salelist: List<SaleMonitoring.Data> = ArrayList()

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
        setToolbarTitle("Sale")
        getSaleRoute()

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = RouteSalesAdapter(this, salelist)
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
            .getTotalSales(
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

                    salelist = ArrayList()
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@SaleActivity,
                            "Sale  Response ${response.body().toString()}"
                        )
                        salelist = response.body()!!.data
                    }
                    setUpRecycler()
                }

                override fun onFailure(call: Call<SaleMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setUpRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@SaleActivity,
                            "Sale  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SaleActivity,
                            "Sale  Response API Fail"
                        )
                    }
                }
            })
    }

    override fun onItemsClick(position: Int, tag: String) {

//        intent = Intent(applicationContext, SaleItemsActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        intent.putExtra("saleitem", salelist[position])
//        startActivity(intent)
    }
}