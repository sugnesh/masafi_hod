package com.engear.vansales.merchandiser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.SurveyAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityDistributionSurveyBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Survey
import com.engear.vansales.model.ToolSurveyData
import com.engear.vansales.restAPI.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DistributionSurveyActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityDistributionSurveyBinding
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    var toolId: String? = null
    var toolName: String? = null
    var allData: ArrayList<Survey> = ArrayList()
    var mAdapter: SurveyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDistributionSurveyBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        customerCode = intent.getStringExtra("customer_code")
        toolId = intent.getStringExtra("tool_id")
        toolName = intent.getStringExtra("tool_name")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        setToolbarTitle(toolName!!)

        getData()

        binding.btnRefresh.setOnClickListener {
            geDistributionSurveyList()
        }
    }

    private fun getData() {
        allData = ArrayList()
        allData = db!!.getToolSurvey(toolId!!)
        setAdapter()
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
            this.putExtra("Type", "Tool")
            this.putExtra("surveyId", allData[position].surveyId)
            this.putExtra("surveyName", allData[position].surveyName)
            this.putExtra("surveyTypeId", allData[position].surveyType)
            this.putExtra("toolId", toolId)
        })
    }

    private fun geDistributionSurveyList() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getDistributionSurvey(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserId()!!.toInt()
        ).enqueue(object : Callback<ToolSurveyData> {
            override fun onResponse(
                call: Call<ToolSurveyData>,
                response: Response<ToolSurveyData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertToolSurvey(response.body()!!.data)
                    }
                }
                dismissDialog()
                getData()
            }

            override fun onFailure(call: Call<ToolSurveyData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }
}