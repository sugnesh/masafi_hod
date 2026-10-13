package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.SupervisorSaleItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySaleBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.SaleRouteMonitoring
import com.google.gson.Gson
import kotlin.collections.ArrayList


class SaleItemsActivity : BaseActivity(), OnItemClickListener {
    var salelist: List<SaleRouteMonitoring.Data.Invoice> = ArrayList()

    lateinit var binding: ActivitySaleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        var  saleitem : SaleRouteMonitoring.Data = intent.getSerializableExtra("saleitem") as SaleRouteMonitoring.Data
        Log.e(":itemlist",Gson().toJson(saleitem))
        salelist = saleitem.invoices

        init()
    }

    private fun init() {
        setToolbarTitle("Item")
        setUpRecycler()

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = SupervisorSaleItemAdapter(this, salelist)
    }



    override fun onItemsClick(position: Int, tag: String) {

    }
}