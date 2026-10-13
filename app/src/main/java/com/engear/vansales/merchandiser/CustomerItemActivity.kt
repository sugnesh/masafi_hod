package com.engear.vansales.merchandiser

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityCustomerItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.DistributionData
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.RoutePortfolioData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerItemActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityCustomerItemBinding
    var customerObj: CustomerData.DataCustomer? = null
    var customerCode: String? = null
    var itemsList: ArrayList<ItemData.Data>? = ArrayList()
    var mAdapter: OrderRequestItemAdapter? = null
    var mId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerItemBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {

        customerCode = intent.getStringExtra("customer_code")
        if (intent.getStringExtra("type") == "Customer") {
            customerObj = db!!.getCustomerDetail(customerCode!!)
            mId = customerObj!!.userDetail.id.toString()
        } else {
            mId = customerCode!!
        }
        setToolbarTitle("Product Listing")

        itemsList = db!!.getAllCUstomerItem(mId)
        setUpRecycler()

        binding.btnRefresh!!.setOnClickListener {
            getDistribution()
        }
    }

    private fun getData() {
        itemsList = ArrayList()
        itemsList = db!!.getAllCUstomerItem(mId)
        setUpRecycler()
    }


    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = OrderRequestItemAdapter(this, itemsList!!, "")
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {

    }

    private fun getDistribution() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getRouteCustomerPortfolio(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.routeId
        ).enqueue(object : Callback<RoutePortfolioData> {
            override fun onResponse(
                call: Call<RoutePortfolioData>,
                response: Response<RoutePortfolioData>
            ) {
                Utils.logData(
                    this@CustomerItemActivity,
                    "Portfolio Response$response"
                )
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.deleteCUstomerItem()
                        db!!.insertCustomerROutePoretfolioItem(response.body()!!.data)
                    }
                }

                dismissDialog()
                getData()
            }

            override fun onFailure(call: Call<RoutePortfolioData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }
}