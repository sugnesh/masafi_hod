package com.engear.vansales.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.AllocationAdapter
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityMasterItemsBinding
import com.engear.vansales.databinding.ActivityPromoAllocationBinding
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.SalesRestrictData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PromoAllocationActivity : BaseActivity() {

    lateinit var binding: ActivityPromoAllocationBinding
    var itemList: ArrayList<SalesRestrictData.RestrictItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromoAllocationBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@PromoAllocationActivity,
            "On Item List Screen "
        )
        init()
    }

    fun init() {
        setToolbarTitle("Promo Allocation")


        itemList = ArrayList()
        itemList = db!!.getAllSalesAllocationItem()
        setAdapter()

        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@PromoAllocationActivity,
                "On Refresh button click "
            )
            getItem()
        }
    }

    private fun getItem() {

        showOnlyProgressDialog()


        val jObj = JsonObject()
        jObj.addProperty("salesman_id", userInfo!!.data.userInfo.salesmanInfo.userId)

        ApiClient.getClient(this).getSalesRestrictData(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<SalesRestrictData> {
            override fun onResponse(
                call: Call<SalesRestrictData>,
                response: Response<SalesRestrictData>
            ) {
                dismissDialog()
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.deleteSalesrestrictItem()
                        db!!.insertSalesRestrictData(response.body()!!.data)
                    }
                }

                itemList = ArrayList()
                itemList = db!!.getAllSalesAllocationItem()
                setAdapter()

            }

            override fun onFailure(call: Call<SalesRestrictData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()

            }
        })
    }

    fun setAdapter() {
        var mAdapter = AllocationAdapter(this, itemList!!)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerViewNewCustomer.layoutManager = layoutManager
        binding.recyclerViewNewCustomer.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewNewCustomer.adapter = mAdapter
    }
}