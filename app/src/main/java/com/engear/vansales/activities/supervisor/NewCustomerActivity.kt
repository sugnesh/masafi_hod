package com.engear.vansales.activities.supervisor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.engear.supervisor.view.interfaces.NewCustomerOnClickListener
import com.engear.vansales.adapter.CustomerAdapterSupervisor
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityNewCustomerBinding
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.NewCustomer
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewCustomerActivity : BaseActivity(), NewCustomerOnClickListener {
    lateinit var newCustomerBinding: ActivityNewCustomerBinding
    private var listNewCustomers = ArrayList<NewCustomer.Data>()
    private lateinit var adapterCustomer: CustomerAdapterSupervisor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newCustomerBinding = ActivityNewCustomerBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        init()
        getCustomers()
        newCustomerBinding!!.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getCustomers()
        })

    }

    private fun getCustomers() {
        showOnlyProgressDialog()
        ApiClient.getClient(this)
            .getCustomerList(
                "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object :
                Callback<NewCustomer> {
                override fun onResponse(
                    call: Call<NewCustomer>,
                    response: Response<NewCustomer>
                ) {
                    dismissDialog()
                    newCustomerBinding!!.swipeRefreshLayout.isRefreshing = false
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@NewCustomerActivity,
                                "customer list Response ${response.body()!!.toString()}"
                            )
                            listNewCustomers = response.body()!!.data as ArrayList<NewCustomer.Data>
                            Log.e("size", listNewCustomers.size.toString())
                            setAdapter()
                        }
                    } else {

                    }

                }

                override fun onFailure(call: Call<NewCustomer>, error: Throwable) {
                    newCustomerBinding!!.swipeRefreshLayout.isRefreshing = false
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@NewCustomerActivity,
                            "Customer list Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@NewCustomerActivity,
                            "Customer list Response API Fail"
                        )
                    }
                }
            })
    }


    fun init() {
        setToolbarTitle("New Customers")
        setNavigationView()
    }

    fun setAdapter() {
        adapterCustomer =
            CustomerAdapterSupervisor(listNewCustomers, this)
        val layoutManager = LinearLayoutManager(applicationContext)
        newCustomerBinding.recyclerViewNewCustomer.layoutManager = layoutManager
        newCustomerBinding.recyclerViewNewCustomer.itemAnimator = DefaultItemAnimator()
        newCustomerBinding.recyclerViewNewCustomer.adapter = adapterCustomer

    }

    override fun onNewCustomerOnClickListener(customer: NewCustomer.Data) {
        intent = Intent(applicationContext, NewCustomerDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("customer", customer)
        startActivity(intent)

    }

}