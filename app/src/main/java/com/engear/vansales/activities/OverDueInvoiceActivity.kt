package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.OverDueInvoiceAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityOverDueInvoiceBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.OverDueInvoice
import com.engear.vansales.utils.Utils

class OverDueInvoiceActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityOverDueInvoiceBinding
    private var allList: ArrayList<OverDueInvoice> = ArrayList()
    private var mAdapter: OverDueInvoiceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverDueInvoiceBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        init()
    }

    private fun init() {
        Utils.logData(
            this@OverDueInvoiceActivity,
            "On overdue invoce Screen "
        )
        setToolbarTitle("Overdue Invoice")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

    }

    fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = OverDueInvoiceAdapter(context = this, allList = allList)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        val `in` = Intent(this@OverDueInvoiceActivity, PaymentDetailActivity::class.java)
        `in`.putExtra("customer", allList[position].customerId)
        `in`.putExtra("amount",  allList[position].amountDue)
        `in`.putExtra("invNo", allList[position].invoiceNo)
        `in`.putExtra("Type", "overDue")
        startActivity(`in`)
    }

    override fun onResume() {
        super.onResume()
        allList = ArrayList()
        allList = db!!.getOverDueInvoice(Utils.getCurrentDate())
        setAdapter()
    }
}