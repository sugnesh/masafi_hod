package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.SurveyAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivitySensorySurveyBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.merchandiser.SensorySurveyViewActivity
import com.engear.vansales.model.SensorySurveyData
import com.engear.vansales.model.Survey
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SensorySurveyActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivitySensorySurveyBinding
    var allData: ArrayList<Survey> = ArrayList()
    var mAdapter: SurveyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySensorySurveyBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {

        Utils.logData(
            this@SensorySurveyActivity,
            "On sensory survey Screen "
        )
        setToolbarTitle("Sensory Survey")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        getData()

        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@SensorySurveyActivity,
                "On refresh button click "
            )
            getSensorySurveyList()
        }
    }

    private fun getData() {
        allData = ArrayList()
        allData = db!!.getAllSensorySurvey()
        setAdapter()
    }

    fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = SurveyAdapter(context = this, allList = allData)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        startActivity(Intent(me, SensorySurveyViewActivity::class.java).apply {
            this.putExtra("surveyId", allData[position].surveyId)
            this.putExtra("surveyName", allData[position].surveyName)
            this.putExtra("surveyTypeId", allData[position].surveyType)
        })
    }

    private fun getSensorySurveyList() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getSensorySurvey(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN
        ).enqueue(object : Callback<SensorySurveyData> {
            override fun onResponse(
                call: Call<SensorySurveyData>,
                response: Response<SensorySurveyData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertSensorySurvey(response.body()!!.data)
                    }
                }
                dismissDialog()
                getData()
            }

            override fun onFailure(call: Call<SensorySurveyData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }

}