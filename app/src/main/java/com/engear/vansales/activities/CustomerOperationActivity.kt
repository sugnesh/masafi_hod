package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import com.engear.vansales.adapter.CustomerOperationAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityCustomerOperationBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.merchandiser.*
import com.engear.vansales.model.ActivityProfile
import com.engear.vansales.model.CustomerData
import com.engear.vansales.utils.Utils

class CustomerOperationActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityCustomerOperationBinding
    var customerCode: String? = null
    private var customerObj: CustomerData.DataCustomer? = null
    var allData: ArrayList<ActivityProfile> = ArrayList()
    var mAdapter: CustomerOperationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerOperationBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@CustomerOperationActivity,
            "On Customer Operation Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Merchandising")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

    }

    private fun setAdapter() {
        binding.rvList.layoutManager = GridLayoutManager(applicationContext, 3)
        mAdapter = CustomerOperationAdapter(this, allData)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        var name = allData[position].name
        if (name == "Shelf Display") {
            Utils.logData(
                me,
                "On Shelf Display Click"
            )
            /* var shelfCount =
                 db!!.getCustomerDistributionCount(customerObj!!.userDetail.id.toString())
             if (shelfCount.toInt() > 0) {*/
            setMyActivity("SHELF_DISPLAY")
            startActivity(Intent(me, BrandActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
                this.putExtra("Type", "Display Tool")
            })
            /*} else {
                Toast.makeText(
                    applicationContext,
                    "No display tool available for this customer",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
        } else if (name == "Stock In Store") {
            Utils.logData(
                me,
                "On Stock In Store Click"
            )
            setMyActivity("STOCK_IN_STORE")
            startActivity(Intent(me, BrandActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
                this.putExtra("Type", "Inventory")
            })
//            startActivity(Intent(me, InventoryCheckActivity::class.java).apply {
//                this.putExtra("customer_code", customerCode)
//            })
        } else if (name == "Replenishment") {
            Utils.logData(
                me,
                "On Replenishment Click"
            )
            startActivity(Intent(me, BrandActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
                this.putExtra("Type", "Replenishment")
            })
        } else if (name == "Complain Feedback") {
            Utils.logData(
                me,
                "On Complain Feedback Click"
            )
            setMyActivity("COMPLAIN_FEEDBACK")
            startActivity(Intent(me, ComplaintSelectionActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
            })
        } else if (name == "Planogram") {
            Utils.logData(
                me,
                "On Planogram Click"
            )
            setMyActivity("PLANOGRAM")
            startActivity(Intent(me, PlanogramListActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
            })
        } else if (name == "Campaign Picture") {
            Utils.logData(
                me,
                "On Compaign Picture Click"
            )
            setMyActivity("COMAIGN_PICTURE")
            startActivity(Intent(me, CompainListActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
            })
        } else if (name == "Consumer Survey") {
            Utils.logData(
                me,
                "On Consumer Survey Click"
            )
            setMyActivity("CONSUMER_SURVEY")
            startActivity(Intent(me, ConsumerSurveyActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
            })
        } else if (name == "Asset Tracking") {
            Utils.logData(
                me,
                "On Asset Tracking Click"
            )
            setMyActivity("ASSET_TRACKING")
            startActivity(Intent(me, AssetListActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
            })
        }
    }
    private fun setMyActivity(  acticvity: String) {
//        val mVisited = MyVisitedList()
//        mVisited.customerId = customerObj!!.userDetail.id.toString()
//        mVisited.customerCode = customerCode
//        mVisited.activityName = acticvity
//        mVisited.activityAction = ""
//        if (!db!!.checkIsVisitedExist(mVisited.customerId.toString(), mVisited.activityName.toString())){
//            db!!.insertMyActivity(mVisited)
//        }
    }
    private fun getData() {
        allData = ArrayList()
        if (db!!.checkIsCustomerActivity(customerObj!!.userDetail.id.toString())) {
            allData = db!!.getCustomerActivity(
                customerObj!!.userDetail.id.toString(),
                customerObj!!.userDetail.id.toString()
            )
        } else if (db!!.checkIsCustomerActivity(mPreferenceManager!!.getUserId()!!)) {
            allData =
                db!!.getCustomerActivity(
                    mPreferenceManager!!.getUserId()!!,
                    customerObj!!.userDetail.id.toString()
                )
        } else {
            allData = Utils.getMerchanActivity()
        }
        setAdapter()
    }

    override fun onResume() {
        super.onResume()

        getData()
    }
}