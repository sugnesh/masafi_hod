package com.engear.vansales.merchandiser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.SurveyAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityConsumerSurveyBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.ConsumerSurveyData
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Survey
import com.engear.vansales.model.VisitedList
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsumerSurveyActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityConsumerSurveyBinding
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    var allData: ArrayList<Survey> = ArrayList()
    var mAdapter: SurveyAdapter? = null
    var visitId: String? = null
    var activityId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsumerSurveyBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Consumer Survey")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        setActivity()

        getData()

        binding.btnRefresh.setOnClickListener {
            getConsumerSurveyList()
        }

    }

    private fun getData() {
        allData = ArrayList()
        allData = db!!.getAllConsumerSurvey(customerObj!!.userDetail.id.toString())
        setAdapter()
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "Consumer Survey"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.getCurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "11")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = SurveyAdapter(context = this, allList = allData)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        startActivity(Intent(me, SurveyViewActivity::class.java).apply {
            this.putExtra("customer_code", customerCode)
            this.putExtra("Type", "Consumer")
            this.putExtra("surveyId", allData[position].surveyId)
            this.putExtra("surveyName", allData[position].surveyName)
            this.putExtra("surveyTypeId", allData[position].surveyType)
        })
    }

    override fun onBackPressed() {
        var action = ""
        action = ""
        var endTime = Utils.getCurrentTime()
        db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
        super.onBackPressed()
    }

    private fun getConsumerSurveyList() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getConsumerSurvey(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserId()!!.toInt()
        ).enqueue(object : Callback<ConsumerSurveyData> {
            override fun onResponse(
                call: Call<ConsumerSurveyData>,
                response: Response<ConsumerSurveyData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertConsumerSurvey(response.body()!!.data)
                    }
                }
                dismissDialog()
                getData()
            }

            override fun onFailure(call: Call<ConsumerSurveyData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }
}