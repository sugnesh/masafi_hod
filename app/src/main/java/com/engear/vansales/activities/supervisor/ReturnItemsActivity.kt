package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.SupervisorReturnItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySaleBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CreditRouteMonitoring
import kotlin.collections.ArrayList


class ReturnItemsActivity : BaseActivity(), OnItemClickListener {
    var salelist: List<CreditRouteMonitoring.Data.CreditNoteDetail> = ArrayList()

    lateinit var binding: ActivitySaleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        var  saleitem : CreditRouteMonitoring.Data= intent.getSerializableExtra("ReturnItem") as CreditRouteMonitoring.Data
        salelist = saleitem.creditNoteDetails

        init()
    }

    private fun init() {
        setToolbarTitle("Item")
        setUpRecycler()

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = SupervisorReturnItemAdapter(this, salelist)
    }



    override fun onItemsClick(position: Int, tag: String) {

    }
}