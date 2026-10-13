package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.CompetitorInfoAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityCompetitorInfoBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.merchandiser.AddCompetitorInfoActivity
import com.engear.vansales.model.CompetitorInfo
import com.engear.vansales.utils.Utils

class CompetitorInfoActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityCompetitorInfoBinding
    var allData: ArrayList<CompetitorInfo> = ArrayList()
    var mAdapter: CompetitorInfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompetitorInfoBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@CompetitorInfoActivity,
            "On Competitor Info Screen "
        )
        init()
        setClickEvent()
    }

    private fun init() {
        setToolbarTitle("Competitor Information")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)
    }

    private fun setClickEvent() {
        binding.btnAdd.setOnClickListener {
            Utils.logData(
                this@CompetitorInfoActivity,
                "On add button click"
            )
            startActivity(Intent(me, AddCompetitorInfoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        allData = ArrayList()
        allData = db!!.getAllCompetitorInfoList()

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
        mAdapter = CompetitorInfoAdapter(context = this, allList = allData)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        TODO("Not yet implemented")
    }

}