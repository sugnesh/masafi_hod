package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.SupervisorOrderItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySaleBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.OrderRouteMonitoring
import kotlin.collections.ArrayList


class OrderItemsActivity : BaseActivity(), OnItemClickListener {
    var salelist: List<OrderRouteMonitoring.Data.OrderDetail> = ArrayList()

    lateinit var binding: ActivitySaleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        var  saleitem : OrderRouteMonitoring.Data = intent.getSerializableExtra("OrderItem") as OrderRouteMonitoring.Data
        salelist = saleitem.orderDetails

        init()
    }

    private fun init() {
        setToolbarTitle("Item")
        setUpRecycler()

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = SupervisorOrderItemAdapter(this, salelist)
    }



    override fun onItemsClick(position: Int, tag: String) {

    }
}