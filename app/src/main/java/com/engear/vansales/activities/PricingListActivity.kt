package com.engear.vansales.activities

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.PricingItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityMasterItemsBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class PricingListActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityMasterItemsBinding
    var itemList: ArrayList<PDPData.PriceData> = ArrayList()
    var arrData: List<PDPData.PriceData> = ArrayList()

    companion object {

        class MyAsyncTask internal constructor(context: PricingListActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<PricingListActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                activity!!.itemList = activity!!.db!!.getpdpriceMasafiData()
                Log.e("itemss", Gson().toJson(activity!!.itemList))

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                activity.dismissDialog()
                activity.setAdapter()
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMasterItemsBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        init()

    }

    private fun openOtherTask() {
        val task = MyAsyncTask(this)
        task.execute(10)
    }

    fun init() {
        Utils.logData(
            this@PricingListActivity,
            "On pricing list Screen "
        )
        setToolbarTitle("Pricing")
        userInfo = mPreferenceManager!!.getUserInfo()

        val task = Companion.MyAsyncTask(this@PricingListActivity)
        task.execute(10)

        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@PricingListActivity,
                "On refresh button click "
            )
            getPDPSiteDATA()
        }
    }

    private fun getPDPSiteDATA() {

        Log.e("API CALL", "Pricing Call")
        showOnlyProgressDialog()
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        //jObj.addProperty("type", "Pricing")

        ApiClient.getClient(this).getPDPCustomerPriceData(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<PriceMFData> {
            override fun onResponse(
                call: Call<PriceMFData>,
                response: Response<PriceMFData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.deleteMasafiPriceItem()
                        db!!.insertPriceMasafi(response.body()!!.data, "Site")
                    }
                }

                getPDPParentDATA()
            }

            override fun onFailure(call: Call<PriceMFData>, error: Throwable) {
                Utils.logData(this@PricingListActivity, "Customer Failed")
                getPDPParentDATA()
            }
        })

    }

    private fun getPDPParentDATA() {

        Log.e("API CALL", "PricingParent Call")
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        //jObj.addProperty("type", "Pricing")

        ApiClient.getClient(this).getPDPParentPriceData(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<PriceMFData> {
            override fun onResponse(
                call: Call<PriceMFData>,
                response: Response<PriceMFData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertPriceMasafi(response.body()!!.data, "Parent")
                    }
                }
                getPDPChannelDATA()
            }

            override fun onFailure(call: Call<PriceMFData>, error: Throwable) {
                Utils.logData(this@PricingListActivity, "Customer Failed")
                getPDPChannelDATA()
            }
        })

    }

    private fun getPDPChannelDATA() {

        Log.e("API CALL", "PricingParent Call")
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        //jObj.addProperty("type", "Pricing")

        ApiClient.getClient(this).getPDPChannelPriceData(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<PriceMFData> {
            override fun onResponse(
                call: Call<PriceMFData>,
                response: Response<PriceMFData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertPriceMasafi(response.body()!!.data, "Channel")
                    }
                }

                getPDPSubChannelDATA()

            }

            override fun onFailure(call: Call<PriceMFData>, error: Throwable) {
                Utils.logData(this@PricingListActivity, "Customer Failed")
                getPDPSubChannelDATA()
            }
        })

    }

    private fun getPDPSubChannelDATA() {

        Log.e("API CALL", "PricingSub  Call")

        ApiClient.getClient(this).getPDPSubChannelPriceData(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN
        ).enqueue(object : Callback<PriceMFData> {
            override fun onResponse(
                call: Call<PriceMFData>,
                response: Response<PriceMFData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertPriceMasafi(response.body()!!.data, "Sub Channel")
                    }
                }

                dismissDialog()

                openOtherTask()

            }

            override fun onFailure(call: Call<PriceMFData>, error: Throwable) {
                Utils.logData(this@PricingListActivity, "Customer Failed")
                dismissDialog()
            }
        })

    }

    fun setAdapter() {

        var mAdapter = PricingItemAdapter(itemList)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerViewNewCustomer.layoutManager = layoutManager
        binding.recyclerViewNewCustomer.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewNewCustomer.adapter = mAdapter

    }

    override fun onItemsClick(position: Int, tag: String) {
        Log.e("click", tag)
    }


}