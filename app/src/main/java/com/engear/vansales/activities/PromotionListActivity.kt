package com.engear.vansales.activities

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.PromotionListAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityPromotionBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.PDPData
import com.engear.vansales.model.PromoOfferData
import com.engear.vansales.model.PromotionGroupData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class PromotionListActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityPromotionBinding
    var itemList: List<PromoOfferData> = ArrayList()
    var arrData: List<PDPData.PriceData> = ArrayList()

    companion object {

        class MyAsyncTask internal constructor(context: PromotionListActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<PromotionListActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                activity!!.itemList = activity!!.db!!.getAllPromotion()
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

        class MyAsyncInsertPromoTask internal constructor(context: PromotionListActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<PromotionListActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                if (activity!!.arrData.isNotEmpty()) {
                    activity!!.db!!.deletePromotion()
                    activity!!.db!!.insertPDPPromotion(activity!!.arrData)
                }

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                activity.dismissDialog()
                activity.openOtherTask()
            }

        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPromotionBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }


    fun init() {

        Utils.logData(
            this@PromotionListActivity,
            "On Promotion list Screen "
        )
        setToolbarTitle("Promotion")

        userInfo = mPreferenceManager!!.getUserInfo()

        val task = Companion.MyAsyncTask(this)
        task.execute(10)

        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@PromotionListActivity,
                "On refresh button click"
            )
            getPromotionGroupItem()
        }
    }

    private fun openOtherTask() {
        val task = Companion.MyAsyncTask(this)
        task.execute(10)
    }

    private fun getPromotionGroupItem() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getPromotionGroupItem(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN
        ).enqueue(object : Callback<PromotionGroupData> {
            override fun onResponse(
                call: Call<PromotionGroupData>,
                response: Response<PromotionGroupData>
            ) {
                Utils.logData(
                    this@PromotionListActivity,
                    "Mer Portfoloio Response$response"
                )
                if (response.isSuccessful) {
                    Utils.logData(
                        this@PromotionListActivity,
                        "Mer Portfolio Response${response.body().toString()}"
                    )
                    if (response.body()!!.status!!) {
                        db!!.deletePromotionGroupItem()
                        db!!.insertPromotionGroupItem(response.body()!!.data)
                    }
                }
                getCustomerPromotion()
            }

            override fun onFailure(call: Call<PromotionGroupData>, error: Throwable) {
                if (error.message != null) {
                    Utils.logData(
                        this@PromotionListActivity,
                        "Mer Portfolio Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@PromotionListActivity,
                        "Mer Portfolio Response API Fail"
                    )
                }
                getCustomerPromotion()
            }
        })
    }

    private fun getCustomerPromotion() {


        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        jObj.addProperty("type", "Promotion")


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
                            Log.e("promotion_api", Gson().toJson(response.body()!!))
                            arrData = ArrayList()
                            arrData = response.body()!!.data

//                            db!!.deletePromotion()
//                            db!!.insertPDPPromotion(response.body()!!.data)
                        }
                    }

                    val task = Companion.MyAsyncInsertPromoTask(this@PromotionListActivity)
                    task.execute(10)

                }

                override fun onFailure(call: Call<PDPData>, error: Throwable) {
                    Utils.logData(this@PromotionListActivity, "Customer Failed")
                    dismissDialog()
                }
            })

    }

    fun setAdapter() {
        var mAdapter = PromotionListAdapter(this, itemList, db!!)
        val layoutManager2 = LinearLayoutManager(applicationContext)
        binding.recyclerViewItem.layoutManager = layoutManager2
        binding.recyclerViewItem.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewItem.adapter = mAdapter

    }

    override fun onItemsClick(position: Int, tag: String) {
        Log.e("click", tag)
    }

}