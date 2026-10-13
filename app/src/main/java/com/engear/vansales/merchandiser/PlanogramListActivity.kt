package com.engear.vansales.merchandiser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.PlanogramAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityPlanogramListBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Planogram
import com.engear.vansales.model.PlanogramData
import com.engear.vansales.model.VisitedList
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlanogramListActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityPlanogramListBinding
    var customerCode: String? = null
    private var customerObj: CustomerData.DataCustomer? = null
    private var allData: ArrayList<Planogram> = ArrayList()
    var mAdapter: PlanogramAdapter? = null
    var visitId: String? = null
    var activityId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanogramListBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {

        setToolbarTitle("Planogram")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        setActivity()

        getData()

        binding.btnRefresh.setOnClickListener {
            getPlanogram()
        }
    }

    private fun getData() {
        allData = db!!.getAllPlanogramList(customerObj!!.userDetail.id.toString())
        setAdapter()
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "Planogram"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.getCurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "10")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    private fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = PlanogramAdapter(this, allData)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        startActivity(Intent(me, PlanogramDetailActivity::class.java).apply {
            this.putExtra("customer_code", customerCode)
            this.putExtra("display_tool_id", allData[position].distributionId)
            this.putExtra("planogramId", allData[position].planogramId)
            this.putExtra("name", allData[position].name)
        })
    }

    override fun onBackPressed() {
        var action = ""
        action = ""
        var endTime = Utils.getCurrentTime()
        db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
        super.onBackPressed()
    }

    private fun getPlanogram() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerPlanogram(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserId()!!.toInt()
        ).enqueue(object : Callback<PlanogramData> {
            override fun onResponse(
                call: Call<PlanogramData>,
                response: Response<PlanogramData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertCustomerPlanogram(response.body()!!.data)
                    }
                }
                dismissDialog()
                getData()
            }

            override fun onFailure(call: Call<PlanogramData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }
}