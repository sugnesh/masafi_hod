package com.engear.vansales.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.engear.vansales.R
import com.engear.vansales.adapter.DepositeSummaryAdapter
import com.engear.vansales.adapter.SalesSummaryAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityDepositeSummaryBinding
import com.engear.vansales.databinding.ActivitySalesSummaryViewBinding
import com.engear.vansales.model.DepositeSummary
import com.engear.vansales.utils.Utils

class DepositeSummaryActivity : BaseActivity() {

    lateinit var binding: ActivityDepositeSummaryBinding
    private var collectionList: ArrayList<DepositeSummary>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositeSummaryBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@DepositeSummaryActivity,
            "On Deposite Summary Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Daily Collection")

        collectionList = ArrayList()
        collectionList = db!!.getAllFinalDepositeCollection()

        setAdapter()

        getTotal()
    }

    fun setAdapter() {
        var mAdapter = DepositeSummaryAdapter(this, collectionList!!)
        binding.rvList.adapter = mAdapter
    }

    private fun getTotal() {
        var mNet = 0.0
        var mGross = 0.0

        for (mInv in collectionList!!) {

            if (mInv.collectionType!! == "1") {
                mNet += mInv.amountPaid!!.toDouble()
            } else {
                mGross += mInv.amountPaid!!.toDouble()
            }

        }

        binding.txtNet.text = "" + mNet
        binding.txtGross.text = "" + mGross
    }
}