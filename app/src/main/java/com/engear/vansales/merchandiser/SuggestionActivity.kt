package com.engear.vansales.merchandiser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.SuggestionAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySuggestionBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Complaint
import com.engear.vansales.model.CustomerData

class SuggestionActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivitySuggestionBinding
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    var allComplain: ArrayList<Complaint> = ArrayList()
    var mAdapter: SuggestionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuggestionBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {
        setToolbarTitle("Suggestions")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

    }

    private fun setClickEvent() {

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(me, AddSuggestionActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
            })
        }
    }

    override fun onResume() {
        super.onResume()

        allComplain = ArrayList()
        allComplain = db!!.getAllSuggestionList(customerCode!!)

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
        mAdapter = SuggestionAdapter(context = this, allList = allComplain)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {

    }

}