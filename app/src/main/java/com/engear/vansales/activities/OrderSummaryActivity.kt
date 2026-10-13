package com.engear.vansales.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.engear.vansales.R
import com.engear.vansales.adapter.SalesSummaryAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityOrderSummaryBinding
import com.engear.vansales.databinding.ActivitySalesSummaryViewBinding
import com.engear.vansales.model.Invoice
import com.engear.vansales.utils.Utils

class OrderSummaryActivity : BaseActivity() {

    lateinit var  binding: ActivityOrderSummaryBinding
    private var invoiceList: ArrayList<Invoice>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@OrderSummaryActivity,
            "On Sales Summary Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Daily Order")

        invoiceList = ArrayList()
        invoiceList = db!!.getAllOrderList()


        getTotal()

        setAdapter()
    }

    fun setAdapter() {
        var mAdapter = SalesSummaryAdapter(this, invoiceList!!)
        binding.rvList.adapter = mAdapter
    }

    private fun getTotal() {
        var mNet = 0.0
        var mGross = 0.0

        for (mInv in invoiceList!!) {
            mNet += mInv.total!!.toDouble()
            mGross += mInv.gross!!.toDouble()

        }

        binding.txtNet.text = "" + mNet
        binding.txtGross.text = "" + mGross
    }
}