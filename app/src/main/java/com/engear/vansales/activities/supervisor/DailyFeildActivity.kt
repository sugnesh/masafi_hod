package com.engear.vansales.activities.supervisor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.DailyFeildCustomerAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityDailyFeildBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.DailyFeildRequestModel
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DailyFeildActivity : BaseActivity(), OnShopItemClickListener {

    var listNewCustomers: ArrayList<CustomerData.DataCustomer> = ArrayList()
    private lateinit var adapterCustomer: DailyFeildCustomerAdapter
    private var lobid = ""
    private var routeId = ""
    lateinit var binding: ActivityDailyFeildBinding
    private var dailyData: DailyFeildRequestModel = DailyFeildRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyFeildBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        lobid = intent.getStringExtra("lob")!!
        routeId = intent.getStringExtra("route")!!
        var  data = db!!.getDailyFeildList()
        Log.e("getDailyFeildList", Gson().toJson(data))
        init()
        getCustomer()


    }

    private fun getCustomer() {
        showOnlyProgressDialog()
        val jObj = JsonObject()
        jObj.addProperty("route_id", intent.getStringExtra("route"))
        Log.e("CustomerData", jObj.toString())
        ApiClient.getClient(this).getAllCustomer(
            "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<CustomerData> {
                override fun onResponse(
                    call: Call<CustomerData>,
                    response: Response<CustomerData>
                ) {
                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            listNewCustomers =
                                response.body()!!.data as ArrayList<CustomerData.DataCustomer>
                            for (itm in listNewCustomers.indices){
                                var data =  db!!.getDailyFeildisCreated(listNewCustomers[itm].id.toString(),routeId)
                                if (data.equals("Y")){
                                    listNewCustomers[itm].isCheck = true
                                    dailyData.customers!!.add(db!!.getDailyFeild(listNewCustomers[itm].id.toString(), routeId)!!)
                                }
                            }
                            setAdapter()
                        }
                    }
                }

                override fun onFailure(call: Call<CustomerData>, error: Throwable) {
                    dismissDialog()
                }
            })
    }

    private fun postCustomer() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).postDailyFeild(
            "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            dailyData
        )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    dismissDialog()

//
                    if (response.isSuccessful) {
                        db!!.updateCustomerVisit(routeId ,"1")
                        Toast.makeText(
                            this@DailyFeildActivity,
                            "Daily field Activity Report Posted Successfully!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Utils.logData(
                            this@DailyFeildActivity,
                            "Daily field Response ${response.body().toString()}"
                        )
                        finish()
                    } else {
                        db!!.updateCustomerVisit(routeId ,"0")
                        val error = response.errorBody()!!.string()
                        Utils.logData(
                            this@DailyFeildActivity,
                            "Daily field Response faild${error}"
                        )
                        Toast.makeText(
                            this@DailyFeildActivity,
                            "Daily field Activity Report Posted fail!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    dismissDialog()
                    db!!.updateCustomerVisit(routeId ,"0")
                    Toast.makeText(
                        this@DailyFeildActivity,
                        "Daily field Activity Report Posted fail!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (error.message != null) {
                        Utils.logData(
                            this@DailyFeildActivity,
                            "Daily field Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@DailyFeildActivity,
                            "Daily field Response API Fail"
                        )
                    }
                }
            })
    }


    fun init() {
        setToolbarTitle("Daily Field Activity")
        dailyData!!.lobId = intent.getStringExtra("lob")!!
        dailyData!!.status = 1
        dailyData!!.date = Utils.getCurrentDate()
        dailyData!!.supervisorId = mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.id

        binding.btnSubmit.setOnClickListener {
            Log.e("postdata", Gson().toJson(dailyData))
            postCustomer()
        }
    }

    fun setAdapter() {

        adapterCustomer = DailyFeildCustomerAdapter(this, listNewCustomers,db)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.rvList.layoutManager = layoutManager
        binding.rvList.itemAnimator = DefaultItemAnimator()
        binding.rvList.adapter = adapterCustomer

    }


    override fun onShopItemClick(position: Int, tag: String, type: String) {

        intent = Intent(applicationContext, DivisionItemActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("customerid", listNewCustomers[position].id.toString())
        intent.putExtra("lobId", lobid)
        intent.putExtra("routeId", routeId)
        startActivityForResult(intent, 11)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 11) {
            if (null != data) {
                // fetch the message String
                val dailyDataCustomer =
                    data!!.getSerializableExtra("dailydata") as DailyFeildRequestModel.Customer

                if (listNewCustomers!!.size > 0) {
                    for (i in listNewCustomers.indices) {
                        if (listNewCustomers[i].id == dailyDataCustomer.customerId) {
                            listNewCustomers[i].isCheck = true
                            adapterCustomer.notifyDataSetChanged()
                            break
                        }
                    }

                }

                if (dailyData.customers!!.size > 0) {
                    for (i in dailyData.customers!!.indices) {
                        if (dailyData.customers!![i].customerId == dailyDataCustomer.customerId) {
                            dailyData.customers!!.removeAt(i)
                            break
                        }
                    }
                    dailyData.customers!!.add(dailyDataCustomer)
                } else {
                    dailyData.customers!!.add(
                        dailyDataCustomer
                    )
                }


            }
        }

    }
}