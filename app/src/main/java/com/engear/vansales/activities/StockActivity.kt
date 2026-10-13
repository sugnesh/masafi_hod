package com.engear.vansales.activities

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.StockItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityStockBinding
import com.engear.vansales.model.StockData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockActivity : BaseActivity() {

    lateinit var binding: ActivityStockBinding
    private var arrList: List<StockData.Data.StockList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@StockActivity,
            "On stock details Screen "
        )
        setToolbarTitle("Stock Details")

        if (Utils.isNetworkAvailable(this)) {
            getStockDetail()
        } else {
            Utils.displayAlert(this, "Internet is not available!")
        }
    }

    private fun getStockDetail() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getWarehouseDetail(
            "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.id,
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.depotId.toInt()
        ).enqueue(object : Callback<StockData> {
            override fun onResponse(
                call: Call<StockData>,
                response: Response<StockData>
            ) {
                dismissDialog()
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        arrList = response.body()!!.data.itemList!!
                        setAdapter()
                    } else {
                        Utils.displayAlert(
                            mContext = this@StockActivity,
                            message = "Status is false"
                        )
                    }
                } else {
                    Utils.displayAlert(
                        mContext = this@StockActivity,
                        message = "Response Blank"
                    )
                }
            }

            override fun onFailure(call: Call<StockData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
                Utils.displayAlert(
                    mContext = this@StockActivity,
                    message = error!!.message.toString()
                )
            }
        })
    }

    private fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = StockItemAdapter(this, arrList!!)
    }
}