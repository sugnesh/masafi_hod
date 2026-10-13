package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.engear.vansales.adapter.BrandAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityBrandBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.merchandiser.DistributionListActivity
import com.engear.vansales.merchandiser.InventoryCheckActivity
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BrandActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityBrandBinding

    var customerCode: String? = null
    var strType: String = ""
    private var allData: ArrayList<Brand> = ArrayList()
    var mAdapter: BrandAdapter? = null
    var visitId: String? = null
    var activityId: String? = null
    private var customerObj: CustomerData.DataCustomer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrandBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@BrandActivity,
            "On Brand Screen "
        )
        init()
    }

    private fun init() {
        Utils.logData(
            me,
            "On Brand Screen"
        )
        strType = intent.getStringExtra("Type")!!
        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        if (strType == "Display Tool") {
            setToolbarTitle("Display Tool Brand")
        } else if (strType == "Inventory") {
            setToolbarTitle("Inventory Brand")
        } else {
            binding.btnRefresh.visibility = View.GONE
            setToolbarTitle("Replenishment Brand")
        }

        setActivity()
        getData()

        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@BrandActivity,
                "On Refresh Button Click "
            )
            if (strType == "Display Tool") {
                getDistribution()
            } else if (strType == "Inventory") {
                getInventoryDetail()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        if (strType == "Display Tool") {
            activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "6")
            mVisited.activityName = "Shelf Display"
        } else if (strType == "Inventory") {
            activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "7")
            mVisited.activityName = "Stock In Store"
        } else {
            activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "11")
            mVisited.activityName = "Replenishment"
        }
        mVisited.activityAction = ""
        mVisited.startTime = Utils.getCurrentTime()
        mVisited.endTime = ""

        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    private fun getData() {

        allData = ArrayList()
        if (strType == "Inventory") {
            allData = db!!.getCustomerInvBrandList(customerObj!!.userDetail.id.toString())
        } else if (strType == "Display Tool") {
            allData = db!!.getCustomerDisplayBrandList(customerObj!!.userDetail.id.toString())
        }
//        else {
//            allData = db!!.getCustomeReplanishBrandList(customerObj!!.userDetail.id.toString())
//        }
        setAdapter()
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
        if (db!!.checkBrandSubBrandExist(allData[position].barndId.toString())) {
            startActivity(Intent(me, SubBrandActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
                this.putExtra("Type", strType)
                this.putExtra("BrandId", allData[position].barndId)
            })
        } else {
            startActivity(Intent(me, CategoryActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
                this.putExtra("Type", strType)
                this.putExtra("SubType", "Brand")
                this.putExtra("BrandId", allData[position].barndId)
                this.putExtra("HeadBrand", allData[position].barndId)
            })
        }

    }

    override fun onBackPressed() {

        if (strType == "Display Tool") {
            if (DistributionListActivity.isCompleted) {
                DistributionListActivity.isCompleted = false
                CustomerDetailActivity.completedTask++
                var action = ""
                action = "Shelf Display checked"
                var endTime = Utils.getCurrentTime()
                db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
            } else {
                db!!.deleteVisitActivity(activityId!!)
            }
        } else if (strType == "Inventory") {
            if (InventoryCheckActivity.isCompleted) {
                InventoryCheckActivity.isCompleted = false
                CustomerDetailActivity.completedTask++
                var action = ""
                action = "Stock Captured"
                var endTime = Utils.getCurrentTime()
                db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
            } else {
                db!!.deleteVisitActivity(activityId!!)
            }
        } else {
//            if (SubCategoryActivity.isRePlanishmentCompleted) {
//                SubCategoryActivity.isRePlanishmentCompleted = false
//                CustomerDetailActivity.completedTask++
//                var action = ""
//                action = "Replenishment check"
//                var endTime = Utils.getCurrentTime()
//                db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
//            } else {
//                db!!.deleteVisitActivity(activityId!!)
//            }
        }
        super.onBackPressed()
    }

    private fun getInventoryDetail() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerInventory(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserId()!!.toInt()
        ).enqueue(object : Callback<InventoryData> {
            override fun onResponse(
                call: Call<InventoryData>,
                response: Response<InventoryData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertCustomerInventory(response.body()!!.data)
                    }
                }

                dismissDialog()
                getData()

            }

            override fun onFailure(call: Call<InventoryData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }

    private fun getDistribution() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerDistribution(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserId()!!.toInt()
        ).enqueue(object : Callback<DistributionData> {
            override fun onResponse(
                call: Call<DistributionData>,
                response: Response<DistributionData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertCustomerDistribution(response.body()!!.data)
                    }
                }

                dismissDialog()
                getData()
            }

            override fun onFailure(call: Call<DistributionData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }
}