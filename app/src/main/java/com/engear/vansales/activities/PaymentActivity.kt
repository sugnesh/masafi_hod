package com.engear.vansales.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityPaymentBinding
import com.engear.vansales.model.EndoRequestData
import com.engear.vansales.model.OutstandingData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : BaseActivity() {
    lateinit var binding: ActivityPaymentBinding
    var collectionList: ArrayList<OutstandingData.CollectionData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@PaymentActivity,
            "On payment Screen "
        )
        setToolbarTitle("Payment")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        collectionList = db!!.getAllPaidCollection()

        var due_amt = 0.0
        var cheque = 0.0
        var cash = 0.0

        for (mCOll in collectionList) {

            if (mCOll.itype == "Invoice") {
                due_amt += mCOll.amountPay.toDouble()
                if (mCOll.invoice_due_date == "1") {
                    cash += mCOll.amountPay.toDouble()
                } else {
                    cheque += mCOll.amountPay.toDouble()
                }
            }
        }

        binding.txtAmtDue.text = "" + Utils.getCurrencyFormate(this, due_amt.toFloat())

        binding.txtCash.text = "" + Utils.getCurrencyFormate(this, cash.toFloat())
        binding.txtCheque.text = "" + Utils.getCurrencyFormate(this, cheque.toFloat())

        val total = cash + cheque
        binding.txtTotal.text = "" + Utils.getCurrencyFormate(this, total.toFloat())

        userInfo = mPreferenceManager!!.getUserInfo()

        if (mPreferenceManager!!.getString(AppConstants.IS_ENDO_REQUEST) == "1") {
            binding.btnRequest.visibility = View.GONE
        }else{
            binding.btnRequest.visibility = View.GONE
        }

        binding.btnRequest.setOnClickListener {
            Utils.logData(
                this@PaymentActivity,
                "On request button click"
            )

            if (db!!.isSyncCompleteTransaction()) {
                Utils.displayAlert(this, "Please sync unload transaction first!");
            } else {
                sendEndoRequest()
            }
        }

    }

    private fun sendEndoRequest() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
        jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
        jObj.addProperty(
            "supervisor_id",
            mPreferenceManager!!.getString(AppConstants.SUPERVISOR_ID)
        )
        jObj.addProperty("status", "0")

        ApiClient.getClient(this).sendEndorsmentRequest(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN, jObj
        )
            .enqueue(object : Callback<EndoRequestData> {
                override fun onResponse(
                    call: Call<EndoRequestData>,
                    response: Response<EndoRequestData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                        if (response.body()!!.status!!) {
                            mPreferenceManager!!.setString(AppConstants.IS_ENDO_REQUEST, "1")
                            Utils.displayAlert(this@PaymentActivity, "Request Sent successfully!!")
                            binding.btnRequest.visibility = View.GONE
                        } else {
                            Utils.displayAlert(this@PaymentActivity, "Request Sent fail!!")
                        }
                    } else {
                        Utils.displayAlert(this@PaymentActivity, "Request Sent fail!!")
                    }

                }

                override fun onFailure(call: Call<EndoRequestData>, error: Throwable) {
                    dismissDialog()
                    Utils.displayAlert(this@PaymentActivity, "Request Sent fail!!")
                }
            })
    }

}