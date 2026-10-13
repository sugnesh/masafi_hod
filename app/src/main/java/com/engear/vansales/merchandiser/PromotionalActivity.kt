package com.engear.vansales.merchandiser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.PromotionalAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityPromotionalBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Promotional

class PromotionalActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityPromotionalBinding
    var allData: ArrayList<Promotional> = ArrayList()
    var mAdapter: PromotionalAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromotionalBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {
        setToolbarTitle("Promotional Accountability")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        allData = ArrayList()
        allData = db!!.getPromotionList()
        setAdapter()
    }

    private fun setClickEvent() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(me, AddPromotionalActivity::class.java))
        }
    }

    fun setAdapter() {
        binding.rvPromotionalList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = PromotionalAdapter(context = this, allList = allData)
        binding.rvPromotionalList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        TODO("Not yet implemented")
    }
}