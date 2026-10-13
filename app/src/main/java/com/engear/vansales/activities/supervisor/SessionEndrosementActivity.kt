package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.engear.vansales.R
import com.engear.vansales.adapter.LoadDetailsAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.ActivitySessionEndrosementBinding
import com.engear.vansales.fragments.CarryOverEndroseFragment
import com.engear.vansales.fragments.InvoiceEndroseFragment
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import com.learnina.materialverticalstepper.verticalstepper.VerticalItem
import com.learnina.sample.fragments.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SessionEndrosementActivity : BaseActivity() {

    lateinit var binding: ActivitySessionEndrosementBinding

    private lateinit var adapterLoad: LoadDetailsAdapter

    companion object {
        var routeId: String = "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionEndrosementBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()

    }


    fun init() {
        setToolbarTitle("Session Endrosement")

        routeId = intent.getStringExtra("route")!!
        val stepList = mutableListOf<VerticalItem>()

        var fragment = InvoiceEndroseFragment()
        var fragment2 = FocEndroseFragment()
        var fragment3 = CallsEndroseFragment()
        var fragment4 = CarryOverEndroseFragment()
        var fragment5 = RouteTransferEndroseFragment()
        var data = Bundle()
        data.putString("routeid", intent.getStringExtra("route"))

        fragment.arguments = data
        stepList.add(0, VerticalItem("Invoice ", "", fragment))
        stepList.add(1, VerticalItem("Calls", "", fragment3))
        stepList.add(2, VerticalItem("FOC", "", fragment2))
        stepList.add(3, VerticalItem("Carry Over", "", fragment4))
//        stepList.add(4, VerticalItem("Route Transfer", "", fragment5))

        binding.verticalList.setStepperStepList(stepList, supportFragmentManager)
        binding.verticalList.setButtonText("next", "prev", "finish")
        binding.verticalList.setButtonColor(R.color.colorPrimary, R.color.colorAccent)

        binding.btnEndrose.setOnClickListener {
            postEndross()
        }
    }

    private fun postEndross() {
        showOnlyProgressDialog()
        val jObj = JsonObject()
        jObj.addProperty(
            "uuid",
            SharedPreferenceManager(this!!)!!.getSupervisorUserInfo()!!.data.userInfo.uuid
        )
        jObj.addProperty("status", "Approved")
        ApiClient.getClient(this!!)
            .requestEndross(
                "Bearer ${SharedPreferenceManager(this!!)!!.getSupervisorUserInfo()!!.data.accessToken}",
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
                            Log.e("endrosse", response.body()!!.string())

                            Utils.logData(
                                this@SessionEndrosementActivity,
                                "Session endrose  Response ${response.body()!!.toString()}"
                            )}

                    } else {
                        Log.e("endrosse", response.errorBody()!!.string())
                        Utils.logData(
                            this@SessionEndrosementActivity,
                            "Session endrose  Response fail${response.errorBody()!!.toString()}"
                        )
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@SessionEndrosementActivity,
                            "Session endrose  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SessionEndrosementActivity,
                            "Session endrose  Response API Fail"
                        )
                    }

                }
            })
    }


}