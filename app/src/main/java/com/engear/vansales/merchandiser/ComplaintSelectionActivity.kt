package com.engear.vansales.merchandiser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityComplaintSelectionBinding
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.VisitedList
import com.engear.vansales.utils.Utils

class ComplaintSelectionActivity : BaseActivity() {

    lateinit var binding: ActivityComplaintSelectionBinding
    var customerCode: String? = null
    var visitId: String? = null
    var activityId: String? = null
    private var customerObj: CustomerData.DataCustomer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintSelectionBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Complaint Feedback")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        setActivity()

        binding.btnComplaint.setOnClickListener {
            startActivity(Intent(me, ComplainActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
            })
        }

        binding.btnCompliment.setOnClickListener {

        }

        binding.btnSuggestion.setOnClickListener {
            startActivity(Intent(me, SuggestionActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
            })
        }
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "Complain Feedback"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.getCurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "8")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    override fun onBackPressed() {
        var action = ""
        action = ""
        var endTime = Utils.getCurrentTime()
        db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
        super.onBackPressed()
    }

}