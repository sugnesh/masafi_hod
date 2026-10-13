package com.engear.vansales.merchandiser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.SensorySurveyAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySensorySurveyViewBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.SurveyPost

class SensorySurveyViewActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivitySensorySurveyViewBinding
    var surveyId: String? = null
    var surveyTypeId: String? = null
    var surveyName: String? = null
    var allData: ArrayList<SurveyPost> = ArrayList()
    var mAdapter: SensorySurveyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorySurveyViewBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        init()
        setClickEvent()
    }

    private fun init() {
        surveyId = intent.getStringExtra("surveyId")
        surveyTypeId = intent.getStringExtra("surveyTypeId")
        surveyName = intent.getStringExtra("surveyName")
        setToolbarTitle(surveyName!!)

    }


    private fun setClickEvent() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(me, SurveyViewActivity::class.java).apply {
                this.putExtra("Type", "Sensory")
                this.putExtra("surveyId", surveyId)
                this.putExtra("surveyName", surveyName)
                this.putExtra("surveyTypeId", surveyTypeId)
            })
        }
    }

    fun setAdapter() {

        if (allData.size > 0) {
            binding.rvList.visibility = View.VISIBLE
            binding.txtNoContain.visibility = View.GONE
        } else {
            binding.rvList.visibility = View.GONE
            binding.txtNoContain.visibility = View.VISIBLE
        }
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = SensorySurveyAdapter(context = this, allList = allData)
        binding.rvList.adapter = mAdapter
    }


    override fun onItemsClick(position: Int, tag: String) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()

        allData = ArrayList()
        allData = db!!.getSensorySurveyPost(surveyId!!)
        setAdapter()
    }
}