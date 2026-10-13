package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.OrderItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityOrderBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CreditData
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.CustomerPriceData
import com.engear.vansales.model.DeliveryData
import com.engear.vansales.model.Order
import com.engear.vansales.model.VisitedList
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityOrderBinding
    var customerCode: String? = null
    private var orderList: ArrayList<Order>? = ArrayList()
    var customerObj: CustomerData.DataCustomer? = null
    var visitId: String? = null
    var activityId: String? = null
    var lobCode: String = ""

    companion object {
        var isOrderDone = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@OrderActivity,
            "On Order Screen "
        )
        setToolbarTitle("Order")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        setActivity()

        if (intent.getStringExtra("lob_code") != null) {
            lobCode = intent.getStringExtra("lob_code")!!
        }

        getCustomerPrice()

        binding.btnAdd.setOnClickListener {
            Utils.logData(
                this@OrderActivity,
                "On Add button click"
            )
            startActivity(Intent(this@OrderActivity, OrderRequestActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
                this.putExtra("type", "order")
                this.putExtra("lob", lobCode)
            })
        }
    }

    private fun getCustomerOrder() {
        orderList = ArrayList()
        orderList = db!!.getCustomerOrder(customerCode!!)

        setUpRecycler()
    }

    private fun setUpRecycler() {
        binding.rvItemList.layoutManager =
            LinearLayoutManager(this@OrderActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvItemList.adapter = OrderItemAdapter(this@OrderActivity, orderList)
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (orderList!![position].isDamaged == "1") {
            startActivity(Intent(this@OrderActivity, OrderPostActivity::class.java).apply {
                this.putExtra("order_no", orderList!![position].orderNo)
                this.putExtra("type", "order")
                this.putExtra("customer_code", customerCode)
            })
        } else {
            startActivity(Intent(this@OrderActivity, OrderDetailActivity::class.java).apply {
                this.putExtra("order_no", orderList!![position].orderNo)
                this.putExtra("type", "order")
            })
        }

    }

    override fun onResume() {
        super.onResume()
        getCustomerOrder()
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "order"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.get24CurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "2")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    override fun onBackPressed() {
        var action = ""
        if (isOrderDone) {
            isOrderDone = false
            CustomerDetailActivity.completedTask++
            action = "Order created"
            var endTime = Utils.get24CurrentTime()
            db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
        } else {
            db!!.deleteVisitActivity(activityId!!)
        }
        super.onBackPressed()
    }

    private fun getCustomerPrice() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerOraclePrice(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            customerCode!!
        ).enqueue(object : Callback<CreditData> {
            override fun onResponse(
                call: Call<CreditData>,
                response: Response<CreditData>
            ) {

                dismissDialog()
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    Log.e("deliveries", Gson().toJson(response.body()))
                }

                getCustomerItemPrice()
            }

            override fun onFailure(call: Call<CreditData>, error: Throwable) {
                dismissDialog()
                getCustomerItemPrice()
            }
        })
    }

    private fun getCustomerItemPrice() {

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerObj!!.user_id.toString()
        )

        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerHODPrice(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<CustomerPriceData> {
            override fun onResponse(
                call: Call<CustomerPriceData>,
                response: Response<CustomerPriceData>
            ) {

                dismissDialog()
                if (response.isSuccessful) {
                    db!!.deleteMasafiPriceItem()
                    db!!.insertCustomerPriceMasafi(
                        response.body()!!.data,
                        "Site",
                        customerCode!!,
                        customerObj!!.user_id.toString()
                    )
                }
            }

            override fun onFailure(call: Call<CustomerPriceData>, error: Throwable) {
                dismissDialog()
            }
        })
    }

}