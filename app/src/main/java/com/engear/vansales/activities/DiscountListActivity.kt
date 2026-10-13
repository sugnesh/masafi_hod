package com.engear.vansales.activities

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.DiscountItemAdapter
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

class DiscountListActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityMasterItemsBinding
    var itemList: List<PromoOfferData> = ArrayList()

    companion object {

        class MyAsyncTask internal constructor(context: DiscountListActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<DiscountListActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                activity!!.itemList = activity!!.db!!.getAlldiscount()
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
        Utils.logData(
            this@DiscountListActivity,
            "On Discount List Screen "
        )
        init()

    }


    fun init() {
        setToolbarTitle("Discounts")
       // setAdapter()
        userInfo = mPreferenceManager!!.getUserInfo()

        val task = Companion.MyAsyncTask(this)
        task.execute(10)

        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@DiscountListActivity,
                "On Refresh Button Screen "
            )
            getCustomerDiscount()
        }
    }

    private fun getCustomerDiscount() {

        showOnlyProgressDialog()
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        jObj.addProperty("type", "Discount")

        ApiClient.getClient(this).getPDPRouteData(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<PDPData> {
                override fun onResponse(
                    call: Call<PDPData>,
                    response: Response<PDPData>
                ) {
                    dismissDialog()
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                        if (response.body()!!.status!!) {
                            Log.e("discount_api",Gson().toJson(response.body()!! ))
                            db!!.deleteDiscount()
                            db!!.insertPDPDiscount(response.body()!!.data)
                            setAdapter()
                        }
                    }

                    val task = Companion.MyAsyncTask(this@DiscountListActivity)
                    task.execute(10)

                }

                override fun onFailure(call: Call<PDPData>, error: Throwable) {
                    Utils.logData(this@DiscountListActivity, "Customer Failed")
                    dismissDialog()
                }
            })
    }

    fun setAdapter() {
//        var itemList = db!!.getAlldiscount()
//        Log.e("discount",Gson().toJson(itemList ))

        var mAdapter = DiscountItemAdapter((itemList as ArrayList<PromoOfferData>?)!!)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerViewNewCustomer.layoutManager = layoutManager
        binding.recyclerViewNewCustomer.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewNewCustomer.adapter = mAdapter

    }

    override fun onItemsClick(position: Int, tag: String) {
        Log.e("click", tag)
    }


}