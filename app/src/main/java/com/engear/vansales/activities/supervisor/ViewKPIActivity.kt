package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.engear.vansales.R
import com.engear.vansales.adapter.ViewPagerAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityViewKpiactivityBinding
import com.engear.vansales.fragments.KPIView1Fragment
import com.engear.vansales.fragments.KPIView2Fragment
import com.engear.vansales.fragments.KPIView3Fragment
import com.engear.vansales.model.EndroseRequest
import com.engear.vansales.model.SalesmanRequestData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ViewKPIActivity : BaseActivity() {

    lateinit var binding: ActivityViewKpiactivityBinding

    companion object {

        var mActivity: ViewKPIActivity? = null

        var salesmanId: String = ""
        var routeId: String = ""
        var mSalesmanRequestData: SalesmanRequestData.SalesmanData? = null
        var isEndroseReq = false
        var endroUUID: String = ""

        fun callEndrosment() {
            mActivity!!.postEndross()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_kpiactivity)

        mActivity = this
        salesmanId = intent.getStringExtra("route")!!
        routeId = intent.getStringExtra("routeId")!!
        Log.e("Salesman", salesmanId)
        Log.e("ROuteId", routeId)

        var routeName = intent.getStringExtra("routeName")
        var salesmanName = intent.getStringExtra("salesmanName")
        binding.txtRoute.text = "Route : $routeName"
        binding.name.text = salesmanName

        getViewKPI()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    fun setStatePageAdapter() {

        val myViewPageStateAdapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        myViewPageStateAdapter.addFragment(KPIView1Fragment(), "")
        myViewPageStateAdapter.addFragment(KPIView2Fragment(), "")
        myViewPageStateAdapter.addFragment(KPIView3Fragment(), "")
        binding!!.pager.adapter = myViewPageStateAdapter
        binding.dotsIndicator.setViewPager(binding.pager)

        binding!!.pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

            }
        })
    }

    fun getViewKPI() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        val JArray = JsonArray()
        JArray.add(salesmanId)
        jObj.add("user_id", JArray)
        jObj.addProperty("from_date", "")
        jObj.addProperty("to_date", "")
        Log.e("Salesman request", jObj.toString())

        ApiClient.getClient(this)
            .getSalesmanData(
                "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<SalesmanRequestData> {
                override fun onResponse(
                    call: Call<SalesmanRequestData>,
                    response: Response<SalesmanRequestData>
                ) {

//                    Log.e("route",response.body()!!.string())
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@ViewKPIActivity,
                            "Route  Response ${response.body()!!.toString()}"
                        )
                        if (response.body() != null) {
                            var data = response.body()!!.data
                            mSalesmanRequestData = data
                        }
                    }

                    getEndrosementList()
                }

                override fun onFailure(call: Call<SalesmanRequestData>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@ViewKPIActivity,
                            "Data  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ViewKPIActivity,
                            "Data  Response API Fail"
                        )
                    }
                    setStatePageAdapter()
                }
            })
    }

    private fun getEndrosementList() {

        ApiClient.getClient(this)
            .getEndrosementRequest(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object :
                Callback<EndroseRequest> {
                override fun onResponse(
                    call: Call<EndroseRequest>,
                    response: Response<EndroseRequest>
                ) {

                    if (response.isSuccessful) {
//                        Log.e("response",response.body()!!.string())
                        if (response.body() != null) {

                            if (response.body()!!.data.isNotEmpty()) {

                                for (mReq in response.body()!!.data) {
                                    if (mReq.routeId == routeId.toInt()) {
                                        if (mReq.status == "Pending") {
                                            isEndroseReq = true
                                            endroUUID = mReq.uuid
                                        }
                                    }
                                }
                            }
                        }
                    }

                    dismissDialog()
                    setStatePageAdapter()
                }

                override fun onFailure(call: Call<EndroseRequest>, error: Throwable) {
                    // Fail to Post
                    Log.e("faild", error.message!!)
                    dismissDialog()
                    setStatePageAdapter()
                }
            })
    }

    fun postEndross() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("uuid", endroUUID)
        jObj.addProperty("status", "Approved")

        ApiClient.getClient(this!!)
            .requestEndross(
                "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Toast.makeText(
                                this@ViewKPIActivity,
                                "Session Endrosement completed!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Utils.logData(
                                this@ViewKPIActivity,
                                "Session endrosement  Response ${response.body().toString()}"
                            )
                            SPEndrosementActivity.isEndoCompleted = true
                            finish()
                        }
                    } else {
                        Log.e("endrosse", response.errorBody()!!.string())
                        Toast.makeText(
                            this@ViewKPIActivity,
                            "Session Endrosement failed!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    Toast.makeText(
                        this@ViewKPIActivity,
                        "Session Endrosement failed!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (error.message != null) {
                        Utils.logData(
                            this@ViewKPIActivity,
                            "Session Endrosement  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ViewKPIActivity,
                            "Session Endrosement  Response API Fail"
                        )
                    }
                }
            })
    }

}