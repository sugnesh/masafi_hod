package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.DeliveryItemAdapter
import com.engear.vansales.adapter.ViewOrderItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityDeliveryBinding
import com.engear.vansales.databinding.ActivityMultiOrderViewBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.DeliveryHeader
import com.engear.vansales.model.VisitedList
import com.engear.vansales.utils.Utils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Collections

class MultiOrderViewActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityMultiOrderViewBinding
    var customerObj: CustomerData.DataCustomer? = null
    var customerId: String = ""
    var mAdapter: ViewOrderItemAdapter? = null
    var deliveryList: MutableList<DeliveryHeader>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiOrderViewBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@MultiOrderViewActivity,
            "On Order View Screen "
        )
        init()

    }

    private fun init() {
        setToolbarTitle("Order")

        var customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        customerId = customerObj!!.user_id!!.toString()

        getCustomerDelivery()
    }

    private fun getCustomerDelivery() {
        deliveryList!!.clear()
        deliveryList = ArrayList()

        deliveryList = db!!.getCustomerDelivery(customerId, "")

        setUpRecycler()
    }

    private fun setUpRecycler() {

        binding.rvDeliveryList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = ViewOrderItemAdapter(this, deliveryList!!)
        binding.rvDeliveryList.adapter = mAdapter

    }

    override fun onItemsClick(position: Int, tag: String) {
        when (tag) {
            "click" -> {
                openCUstomer()
            }

            "onMap" -> {
                startActivity(Intent(this, LocationActivity::class.java).apply {
                    this.putExtra("customer_code", customerObj!!.customer_code)
                    this.putExtra("Type", "Single")
                    this.putExtra("Latitude", deliveryList!![position].mOrder.mAddress.latitude)
                    this.putExtra("Long", deliveryList!![position].mOrder.mAddress.longitude)
                })
            }
        }
    }


    private fun openCUstomer() {

        val mVisited = VisitedList().apply {
            customerId = customerObj!!.userDetail.id.toString()
            customerCode = customerObj!!.customer_code
            status = "open"
            startTime = Utils.get24CurrentTime()
            endTime = ""
            latitude = AppConstants.Latitude
            longitude = AppConstants.Longitude
            reason = ""
            comment = ""
            isSequence = "1"
            completedTask = "0"
            planId = ""
            visitedOffId = Utils.getVisitNo(customerObj!!.userDetail.id.toString())
        }

        db?.updateCustomerShopStatus(customerObj!!.userDetail.id.toString(), "0")
        db?.insertVisitedCustomer(mVisited, "open")

        CustomerDetailActivity.completedTask = 0
        startActivity(Intent(this, CustomerDetailActivity::class.java).apply {
            putExtra("customer_code", customerObj!!.customer_code)
            putExtra("seq_force", "0")
            putExtra("type", "JP")
        })
    }


}