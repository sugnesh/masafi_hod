package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityNewCustomerDetailsBinding
import com.engear.vansales.model.NewCustomer
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewCustomerDetailsActivity : BaseActivity() {

    lateinit var newCustomerDetailsBinding: ActivityNewCustomerDetailsBinding
   lateinit var customer: NewCustomer.Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newCustomerDetailsBinding = ActivityNewCustomerDetailsBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setCustomerDetailsUI()
            }

    fun init() {
        setToolbarTitle("Customer Details")

    }

    fun setCustomerDetailsUI() {
         customer = intent.getSerializableExtra("customer") as NewCustomer.Data

        Log.e("customerdata",Gson().toJson(customer))

        newCustomerDetailsBinding.etCustomerCode.setText(customer!!.customerCode)
        newCustomerDetailsBinding.etCustomerName.setText(customer!!.user.firstname + " " + customer.user.lastname)
        newCustomerDetailsBinding.etCustomerMobile.setText(customer!!.user.mobile.toString())
        newCustomerDetailsBinding.etCustomerAddress.setText(customer!!.customerAddress1)


        var array_routes = arrayOf("")
        var array_channel =
            arrayOf("")
        var array_category =
            arrayOf("")

        if (customer!!.route != null) {
            array_routes[0] = customer.route!!.routeName
        }
        if (customer!!.channel != null) {
            array_channel[0] = customer.channel!!.name
        }
        if (customer!!.customerCategory != null) {
            array_category[0] = customer.customerCategory!!.customerCategoryName
        }
        val adapterRoute = ArrayAdapter(
            this,
            R.layout.spinner_item_layout, array_routes
        )
        newCustomerDetailsBinding.spRoute.adapter = adapterRoute

        val adapterChannel = ArrayAdapter(
            this,
            R.layout.spinner_item_layout, array_channel
        )
        newCustomerDetailsBinding.spChannel.adapter = adapterChannel

        val adapterCategory = ArrayAdapter(
            this,
            R.layout.spinner_item_layout, array_category
        )
        newCustomerDetailsBinding.spCategory.adapter = adapterCategory

        newCustomerDetailsBinding.btnApprove.setOnClickListener {
            approveRequest()
        }

        newCustomerDetailsBinding.btnReject.setOnClickListener {
            rejectRequest()
        }
    }
    private fun approveRequest() {
        showOnlyProgressDialog()
        ApiClient.getClient(this)
            .approveRequest(
                "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,customer!!.uuid
            )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    dismissDialog()


                    if (response.isSuccessful) {
                        Log.e("aprove",response.body()!!.string())
                        Utils.logData(
                            this@NewCustomerDetailsActivity,
                            "Approve Customer  Response ${response.body()!!.string()}"
                        )
                    } else {
                        Log.e("aprove_err",response.errorBody()!!.string())
                        Utils.logData(
                            this@NewCustomerDetailsActivity,
                            "Approve Customer  Response fail${response.errorBody()!!.string()}"
                        )
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@NewCustomerDetailsActivity,
                            "Approve Customer  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@NewCustomerDetailsActivity,
                            "Approve Customer  Response API Fail"
                        )
                    }
                }
            })
    }
    private fun rejectRequest() {
        showOnlyProgressDialog()
        ApiClient.getClient(this)
            .rejectRequest(
                "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,customer!!.uuid
            )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    dismissDialog()


                    if (response.isSuccessful) {
                        Log.e("reject",response.body()!!.string())
                        Utils.logData(
                            this@NewCustomerDetailsActivity,
                            "Reject Customer  Response ${response.body()!!.string()}"
                        )
                    } else {
                        Log.e("reject_err",response.errorBody()!!.string())
                        Utils.logData(
                            this@NewCustomerDetailsActivity,
                            "Reject Customer  Response Fail${response.errorBody()!!.string()}"
                        )
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@NewCustomerDetailsActivity,
                            "Reject Customer  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@NewCustomerDetailsActivity,
                            "Reject Customer  Response API Fail"
                        )
                    }
                }
            })
    }
}