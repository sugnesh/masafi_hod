package com.engear.vansales.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.CustomerAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityMasterItemsBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerListActivity : BaseActivity(), OnShopItemClickListener {
    lateinit var binding: ActivityMasterItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterItemsBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@CustomerListActivity,
            "On Customer Screen Screen "
        )
        init()

    }

    fun init() {
        setToolbarTitle("Customers")
        setAdapter()
        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@CustomerListActivity,
                "On Refresh buttom click "
            )

            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
                getDeliveryCustomer()
            }else{
                getCustomer()
            }

        }
    }

    private fun getCustomer() {

        showOnlyProgressDialog()
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        ApiClient.getClient(this).getAllCustomer(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<CustomerData> {
                override fun onResponse(
                    call: Call<CustomerData>,
                    response: Response<CustomerData>
                ) {

                    Utils.logData(
                        this@CustomerListActivity,
                        "Distribution Response$response"
                    )
                    dismissDialog()
                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            db!!.deleteCustomerRelease()
                            db!!.insertCustomer(response.body()!!.data,"")
                            setAdapter()
                        }
                    }
                }

                override fun onFailure(call: Call<CustomerData>, error: Throwable) {
                    Utils.logData(this@CustomerListActivity, "Customer Failed")
                }
            })
    }

    private fun getDeliveryCustomer() {
        Log.e("API CALL", "Customer Call")

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty(
            "salesman_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.userId
        )
        Log.e("CustomerData", jObj.toString())
        ApiClient.getClient(this).getDeliveryCustomer(
            "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<CustomerData> {
                override fun onResponse(
                    call: Call<CustomerData>,
                    response: Response<CustomerData>
                ) {
                    Log.e("API CALL", "CUstomer  Response")
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                        if (response.body()!!.status!!) {
//                            Log.e("CustomerData",Gson().toJson(response.body()!!.data))
                            //db!!.deleteCUstomer()
                            db!!.insertCustomer(response.body()!!.data, "")
                        }
                    }

                    dismissDialog()

                    setAdapter()
                }

                override fun onFailure(call: Call<CustomerData>, error: Throwable) {
                    Utils.logData(this@CustomerListActivity, "Customer Failed")
                    dismissDialog()

                    setAdapter()
                }
            })
    }
    fun setAdapter() {
      var customerList=   db!!.getAllCustomer("")
        val layoutManager = LinearLayoutManager(applicationContext)
       var mAllAdapter = CustomerAdapter(this, customerList, "all")

        binding.recyclerViewNewCustomer.layoutManager = layoutManager
        binding.recyclerViewNewCustomer.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewNewCustomer.adapter = mAllAdapter
    }

    override fun onShopItemClick(position: Int, tag: String, type: String) {
        Log.e("click",tag)
    }


}