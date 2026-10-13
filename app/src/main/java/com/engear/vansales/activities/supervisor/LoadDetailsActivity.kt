package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.LoadDetailsAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityLoadDetailsBinding
import com.engear.vansales.model.LoadQuantity
import com.engear.vansales.model.SupervisorRoute
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadDetailsActivity : BaseActivity() {

    lateinit var loadDetailsBinding: ActivityLoadDetailsBinding
    private lateinit var adapterLoad: LoadDetailsAdapter
    private var loadlist = ArrayList<LoadQuantity.Data.Loadquantity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadDetailsBinding = ActivityLoadDetailsBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true)
        init()
        getData()
    }

    private fun getData() {
        Log.e("token", mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken)

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getLoadQunatity(
                "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
                intent.getStringExtra("route")!!
            )
            .enqueue(object :
                Callback<LoadQuantity> {
                override fun onResponse(
                    call: Call<LoadQuantity>,
                    response: Response<LoadQuantity>
                ) {
                    dismissDialog()
//                    Log.e("routedetails",response.toString())
//                    Log.e("routedetails",response.body()!!.string())
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@LoadDetailsActivity,
                            "Load Details Response ${response.body()!!.toString()}"
                        )
                        if (response.body() != null) {
                            for (data in response.body()!!.data) {
                                for (indata in data.loadquantity) {
                                    loadlist.add(indata)
                                }
                            }
                            setRouteAdapter()
                        }

                    } else {

                    }

                }

                override fun onFailure(call: Call<LoadQuantity>, error: Throwable) {
                    // Fail to Post
                    Log.e("faild", error.message!!)
                    if (error.message != null) {
                        Utils.logData(
                            this@LoadDetailsActivity,
                            "Load Details Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@LoadDetailsActivity,
                            "Load Details Response API Fail"
                        )
                    }
                    dismissDialog()
                }
            })
    }

    fun init() {
        setToolbarTitle("Load")
    }


    fun setRouteAdapter() {

        adapterLoad =
            LoadDetailsAdapter(loadlist)
        val layoutManager = LinearLayoutManager(this)
        loadDetailsBinding.recyclerViewLoadItem.layoutManager = layoutManager
        loadDetailsBinding.recyclerViewLoadItem.itemAnimator = DefaultItemAnimator()
        loadDetailsBinding.recyclerViewLoadItem.adapter = adapterLoad
    }


}