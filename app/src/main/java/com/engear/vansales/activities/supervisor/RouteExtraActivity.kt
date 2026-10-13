package com.engear.vansales.activities.supervisor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityRouteExtraBinding
import com.engear.vansales.model.SaleOrderReturnAmount
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteExtraActivity : BaseActivity() {

    lateinit var binding: ActivityRouteExtraBinding
    var routeid: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteExtraBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        routeid = intent.getStringExtra("route")!!

        getData()
        init()

    }

    private fun getData() {
        Log.e("token", mPreferenceManager!!.getString(AppConstants.TOKEN)!!)
        Log.e("routeId", routeid)

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getSaleOrderCreditAmount(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                routeid
            )
            .enqueue(object :
                Callback<SaleOrderReturnAmount> {
                override fun onResponse(
                    call: Call<SaleOrderReturnAmount>,
                    response: Response<SaleOrderReturnAmount>
                ) {
                    if (response.isSuccessful) {
//                        Log.e("response",response.body()!!.string())
                        if (response.body() != null) {
                            setData(response.body()!!.data)
                        }
                    }

                    dismissDialog()
                }

                override fun onFailure(call: Call<SaleOrderReturnAmount>, error: Throwable) {
                    // Fail to Post
                    Log.e("faild", error.message!!)
                    dismissDialog()
                }
            })
    }

    private fun setData(data: SaleOrderReturnAmount.Data) {

        var mOrder = 0F
        var mSales = 0F
        var mReturn = 0F
        var mCollection = 0F
        var mVisit = 0
        if (data.orderAmount != null) {
            mOrder = data.orderAmount!!.toFloat()
        }

        if (data.salesAmount != null) {
            mSales = data.salesAmount!!.toFloat()
        }

        if (data.returnAmount != null) {
            mReturn = data.returnAmount!!.toFloat()
        }

        if (data.collectionAmount != null) {
            mCollection = data.collectionAmount!!.toFloat()
        }

        if (data.visitCount != null) {
            mVisit = data.visitCount!!.toInt()
        }

        binding.orderTv.text = "AED " + Utils.getCurrencyFormate(this, mOrder)
        binding.saleTv.text = "AED " + Utils.getCurrencyFormate(this, mSales)
        binding.returnTv.text = "AED " + Utils.getCurrencyFormate(this, mReturn)
        binding.collectionTv.text = "AED " + Utils.getCurrencyFormate(this, mCollection)
        binding.visitTv.text = mVisit.toString()
    }

    private fun init() {

        setToolbarTitle("Route Monitoring")

        binding.btnLoad.setOnClickListener {
            intent = Intent(applicationContext, LoadDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("route", routeid)
            startActivity(intent)
        }

        binding.btnSale.setOnClickListener {
            intent = Intent(applicationContext, SaleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("route", routeid)
            startActivity(intent)
        }

        binding.btnOrder.setOnClickListener {
            intent = Intent(applicationContext, OrderActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("route", routeid)
            startActivity(intent)
        }

        binding.btnReturn.setOnClickListener {
            intent = Intent(applicationContext, CreditActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("route", routeid)
            startActivity(intent)
        }

        binding.btnvisit.setOnClickListener {
            intent = Intent(applicationContext, VisitActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("route", routeid)
            startActivity(intent)
        }

    }
}