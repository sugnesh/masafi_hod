package com.engear.vansales.activities

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.OrderDetailItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityOrderDetailBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.UomData
import com.engear.vansales.utils.Utils
import java.lang.reflect.Type

class OrderDetailActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityOrderDetailBinding
    var arrItemList: MutableList<UomData>? = ArrayList()
    var orderNo: String? = null
    var itemType: String? = null
    var type: Type? = null
    var orderAmount: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {

        Utils.logData(
            this@OrderDetailActivity,
            "On order details Screen "
        )
        itemType = intent.getStringExtra("type")
        orderNo = intent.getStringExtra("order_no")

        if (itemType == "order") {
            setToolbarTitle("Order Detail")
            arrItemList = db!!.getOrderDetail(orderNo = orderNo.toString())
            orderAmount = db!!.getOrderTotalAmount(orderNo.toString())
        } else if (itemType == "load") {
            setToolbarTitle("Load Request Detail")
            arrItemList = db!!.getLoadOrderDetail(orderNo = orderNo.toString())
            orderAmount = db!!.getLoadOrderTotalAmount(orderNo.toString())

        } else if (itemType == "return") {
            setToolbarTitle("Return Detail")
            arrItemList = db!!.getReturnDetail(orderNo = orderNo.toString())
            orderAmount = db!!.getReturnTotalAmount(orderNo.toString())
        }else if (itemType == "invoice") {
            setToolbarTitle("Invoice Detail")
            arrItemList = db!!.getInvoiceDetail(orderNo = orderNo.toString())
            orderAmount = db!!.getInvoiceTotalAmount(orderNo.toString())
        }

        setUpRecycler()

        binding.txtTot.text = "Total: " + Utils.getCurrencyFormate(
            this,
            orderAmount.toFloat()
        ) + " " + Utils.getCurrency(this)
    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = OrderDetailItemAdapter(this, arrItemList!!)

    }

    override fun onItemsClick(position: Int, tag: String) {

    }
}