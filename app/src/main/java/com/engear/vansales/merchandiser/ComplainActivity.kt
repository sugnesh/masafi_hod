package com.engear.vansales.merchandiser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.ComplaintAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityComplainBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Complaint
import com.engear.vansales.model.CustomerData

class ComplainActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityComplainBinding
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    var allComplain: ArrayList<Complaint> = ArrayList()
    var mAdapter: ComplaintAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplainBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {
        setToolbarTitle("Complaint Feedback")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

    }

    private fun setClickEvent() {

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(me, AddComplainActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
                this.putExtra("Type", "Add")
            })
        }
    }

    override fun onResume() {
        super.onResume()

        allComplain = ArrayList()
        allComplain = db!!.getAllComplaintList(customerCode!!)

        if (allComplain.size > 0) {
            binding.rvList.visibility = View.VISIBLE
            binding.txtNoContain.visibility = View.GONE
        } else {
            binding.rvList.visibility = View.GONE
            binding.txtNoContain.visibility = View.VISIBLE
        }
        setAdapter()
    }

    fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = ComplaintAdapter(context = this, allList = allComplain)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
//        startActivity(Intent(me, AddComplainActivity::class.java).apply {
//            this.putExtra("customer_code", customerCode)

//            this.putExtra("Type", "View")
//            this.putExtra("complain_id", allComplain[position].complaintId)
//        })
    }
}