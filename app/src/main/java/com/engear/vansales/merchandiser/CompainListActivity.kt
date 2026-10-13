package com.engear.vansales.merchandiser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.CampaignAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityCompainListBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Campaign
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.VisitedList
import com.engear.vansales.utils.Utils

class CompainListActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityCompainListBinding
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    var allData: ArrayList<Campaign> = ArrayList()
    var mAdapter: CampaignAdapter? = null
    var visitId: String? = null
    var activityId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompainListBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {
        setToolbarTitle("Campaign")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        setActivity()

    }

    private fun setClickEvent() {

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(me, AddCompaignActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
                this.putExtra("Type", "Add")
            })
        }
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "Campaign"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.getCurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "9")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    override fun onResume() {
        super.onResume()

        allData = ArrayList()
        allData = db!!.getAllCampaigntList(customerObj!!.userDetail.id.toString())

        if (allData.size > 0) {
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
        mAdapter = CampaignAdapter(context = this, allList = allData)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        startActivity(Intent(me, AddCompaignActivity::class.java).apply {
            this.putExtra("customer_code", customerCode)
            this.putExtra("Type", "View")
            this.putExtra("campaignId", allData[position].compaignId)
        })
    }

    override fun onBackPressed() {
        var action = ""
        action = ""
        var endTime = Utils.getCurrentTime()
        db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
        super.onBackPressed()
    }
}