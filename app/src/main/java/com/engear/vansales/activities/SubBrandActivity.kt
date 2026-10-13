package com.engear.vansales.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.BrandAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySubBrandBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Brand
import com.engear.vansales.model.CustomerData
import com.engear.vansales.utils.Utils

class SubBrandActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivitySubBrandBinding
    var customerCode: String? = null
    var strType: String = ""
    var brandId: String = ""
    private var allData: ArrayList<Brand> = ArrayList()
    var mAdapter: BrandAdapter? = null
    private var customerObj: CustomerData.DataCustomer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubBrandBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            me,
            "On Sub Brand Screen"
        )
        strType = intent.getStringExtra("Type")!!
        brandId = intent.getStringExtra("BrandId")!!
        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        if (strType == "Display Tool") {
            setToolbarTitle("Display Tool Sub Brand")
        } else if (strType == "Inventory") {
            setToolbarTitle("Inventory Sub Brand")
        } else {
            setToolbarTitle("Replenishment Sub Brand")
        }

        getData()
    }

    private fun getData() {
        allData = ArrayList()
        if (strType == "Inventory") {
            allData =
                db!!.getCustomerInvSubBrandList(customerObj!!.userDetail.id.toString(), brandId)
        } else if (strType == "Display Tool") {
            allData =
                db!!.getCustomerDisplaySubBrandList(customerObj!!.userDetail.id.toString(), brandId)
        } else {
//            allData = db!!.getCustomerReplanishSubBrandList(
//                customerObj!!.userDetail.id.toString(),
//                brandId
//            )
        }

        setAdapter()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun setAdapter() {
        binding.rvList.layoutManager = GridLayoutManager(applicationContext, 2)
        mAdapter = BrandAdapter(this, allData)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        Utils.logData(
            me,
            "Select " + allData[position].name
        )
        startActivity(Intent(me, CategoryActivity::class.java).apply {
            this.putExtra("customer_code", customerCode)
            this.putExtra("Type", strType)
            this.putExtra("SubType", "SubBrand")
            this.putExtra("HeadBrand", brandId)
            this.putExtra("BrandId", allData[position].barndId)
        })
    }
}