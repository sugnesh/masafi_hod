package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import com.engear.vansales.adapter.CustomerDivisionAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityCustomerDivisionBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.UserData
import com.engear.vansales.utils.Utils

class CustomerDivisionActivity : BaseActivity(), OnShopItemClickListener {
    lateinit var binding: ActivityCustomerDivisionBinding
    var customerCode: String? = null
    private var customerObj: CustomerData.DataCustomer? = null
    private var lobList: ArrayList<CustomerData.DataCustomer.Customerlob>? = null
    var isEnforceFlag: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDivisionBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@CustomerDivisionActivity,
            "On Customer Division Screen "
        )
        init()


    }

    private fun init() {

        customerCode = intent.getStringExtra("customer_code")
        isEnforceFlag = intent.getStringExtra("seq_force")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        setToolbarTitle(customerObj!!.userDetail.firstname + " " + customerObj!!.userDetail.lastname)

        var cuslobList = db!!.getCustomerLobList(customerObj!!.userDetail.id.toString())
        var salesLOB: List<UserData.Data.UserInfo.SalesmanInfo.SalesmanLob>? = null
        if (mPreferenceManager!!.getUserInfo() != null) {
            if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList != null) {
                salesLOB = mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList
            }
        }

        lobList = ArrayList()
       // if (salesLOB!!.isNotEmpty()) {
            for (i in cuslobList!!) {
                lobList!!.add(i)
               /* for (j in salesLOB!!) {
                    if (j.lob_id == i.lob_id) {
                        lobList!!.add(i)
                    }

                }*/
            }
        //}

        setUpRecycler()
    }

    private fun setUpRecycler() {

        binding.rvList.layoutManager =
            GridLayoutManager(this, 2)
        var mSeqAdapter = CustomerDivisionAdapter(this, lobList!!)
        binding.rvList.adapter = mSeqAdapter
    }

    override fun onShopItemClick(position: Int, tag: String, type: String) {

        startActivity(Intent(this, CustomerDetailActivity::class.java).apply {
            this.putExtra("customer_code", customerCode)
            this.putExtra("lob_code", lobList!!.get(position).lob_id.toString())
            this.putExtra("seq_force", isEnforceFlag)
        })
        finish()
    }

}