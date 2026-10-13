package com.engear.vansales.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityMasterBinding
import com.engear.vansales.databinding.ActivitySettingBinding
import com.engear.vansales.model.CouponData
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.OutstandingData
import com.engear.vansales.model.Promotional
import com.engear.vansales.model.ReasonData
import com.engear.vansales.model.RouteSettingData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MasterActivity : BaseActivity() {
    lateinit var binding: ActivityMasterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@MasterActivity,
            "On Master Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Master")
        binding.lytCustomer!!.setOnClickListener {
            startActivity(Intent(this, CustomerListActivity::class.java))
        }

        binding.lytMaterial.setOnClickListener {
            startActivity(Intent(this, ItemListActivity::class.java))
        }

        binding.lytDiscount.setOnClickListener {
            startActivity(Intent(this, DiscountListActivity::class.java))
        }
        binding.lytPricing.setOnClickListener {
            startActivity(Intent(this, PricingListActivity::class.java))
        }
        binding.lytPromotion.setOnClickListener {
            startActivity(Intent(this, PromotionListActivity::class.java))
        }

        binding.lytAllocation.setOnClickListener {
            startActivity(Intent(this, PromoAllocationActivity::class.java))
        }

        binding.lytOutStanding.setOnClickListener {
            startActivity(Intent(this, OutstandingInvoiceActivity::class.java))
        }

        binding.lytCopon.setOnClickListener {
            getSalesmanCouponItem()
        }

        binding.lytReleaseSite.setOnClickListener {
            getCustomer()
        }

        binding.lytSeting.setOnClickListener {
            getRouteList()
        }

        binding.lytReason.setOnClickListener {
            getReason()
        }

    }

    private fun getReason() {
        Log.e("API CALL", "Reason Call")
        showOnlyProgressDialog()
        ApiClient.getClient(this).getReason(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN
        ).enqueue(object : Callback<ReasonData> {
            override fun onResponse(
                call: Call<ReasonData>,
                response: Response<ReasonData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.deleteReason()
                        db!!.insertReason(response.body()!!.data)
                    }
                }
                dismissDialog()
            }

            override fun onFailure(call: Call<ReasonData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()

            }
        })
    }

    private fun getCustomer() {

        showOnlyProgressDialog()
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        ApiClient.getClient(this).getAllCustomer(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<CustomerData> {
                override fun onResponse(
                    call: Call<CustomerData>,
                    response: Response<CustomerData>
                ) {


                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            db!!.deleteCustomerRelease()
                            db!!.insertCustomer(response.body()!!.data, "")
                        }
                    }

                    getCustomerOutstanding()
                }

                override fun onFailure(call: Call<CustomerData>, error: Throwable) {
                    Utils.logData(this@MasterActivity, "Customer Failed")
                }
            })
    }

    private fun getRouteList() {

        showOnlyProgressDialog()
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        ApiClient.getClient(this).getAllRouteSetting(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<RouteSettingData> {
                override fun onResponse(
                    call: Call<RouteSettingData>,
                    response: Response<RouteSettingData>
                ) {


                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            var mRouetDate = response.body()!!.data

                            mPreferenceManager!!.setString(
                                AppConstants.INVOICE_LAST,
                                mRouetDate!!.invoice_range_from
                            )

                            mPreferenceManager!!.setString(
                                AppConstants.ORDER_LAST,
                                mRouetDate!!.order_range_next_number
                            )


                            mPreferenceManager!!.setString(
                                AppConstants.COLLECTION_LAST,
                                mRouetDate!!.collection_range_from
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.RETURN_LAST,
                                mRouetDate!!.return_range_from
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.UNLOAD_LAST,
                                mRouetDate!!.unload_range_from
                            )

                            mPreferenceManager!!.setString(
                                AppConstants.LOAD_LAST,
                                mRouetDate!!.load_range_from
                            )


                        }
                    }

                    dismissDialog()
                }

                override fun onFailure(call: Call<RouteSettingData>, error: Throwable) {
                    Utils.logData(this@MasterActivity, "Customer Failed")
                    dismissDialog()
                }
            })
    }

    private fun getCustomerOutstanding() {

        ApiClient.getClient(this).getRoutePendingInvoice(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            userInfo!!.data.userInfo.salesmanInfo.routeId
        ).enqueue(object : Callback<OutstandingData> {
            override fun onResponse(
                call: Call<OutstandingData>,
                response: Response<OutstandingData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.deleteOutstanding()
                        db!!.insertOutStandingInvoice(response.body()!!.data, "")
                    }
                } else {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Blank")
                }
                //dismissDialog()
                getCustomerBlockUnBlock()

            }

            override fun onFailure(call: Call<OutstandingData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })

    }

    private fun getCustomerBlockUnBlock() {
        db!!.deleteBlockCustomer()
        var allCustomer = db!!.getAllCustomer("")
        for (mCustData in allCustomer) {
//            var creditDays =
//                db!!.getCustomerCreditDays(mCustData.user_id!!.toString())
//            var customerType =
//                db!!.getCustomerTYpe(mCustData.user_id!!.toString())

            var mOverdueCollection =
                db!!.getCustomerOverDueCollection(mCustData.user_id!!.toString())
            if (mOverdueCollection.size > 0) {
                var mApproveDays = db!!.getCustomerApproveDays(mCustData.user_id!!.toString())
                if (mApproveDays.toDouble() > 0) {
                    var mApprveDate = db!!.getCustomerApproveDayDate(mCustData.user_id!!.toString())
                    if (!mApprveDate.isEmpty()) {
                        if (Utils.passEndDate(Utils.getCurrentDate(), mApprveDate)) {
                            db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "1")
                        }
                    } else {
                        db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "1")
                    }
                } else {
                    db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "1")
                }
            }

            var customerLimit = db!!.getCustomerCreditLimit(mCustData.user_id!!.toString())
            var customerOutstanding =
                db!!.getCustomerFinalBalance(mCustData.user_id!!.toString())

            var balance = (customerLimit) - customerOutstanding
            if (balance < 0) {

                var mApproveLimit = db!!.getCustomerApproveLimit(mCustData.user_id!!.toString())
                if (mApproveLimit.toDouble() > 0) {
                    var mApprveDate = db!!.getCustomerApproveDate(mCustData.user_id!!.toString())
                    if (!mApprveDate.isEmpty()) {
                        if (Utils.passEndDate(Utils.getCurrentDate(), mApprveDate)) {
                            db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "2")
                        } else {
                            var mFinalAPLimit =
                                db!!.getCustomerApproveLimit(mCustData.user_id!!.toString())
                            var mFinalOutstLimit =
                                db!!.getCustomerApproveLimitOutstanding(mCustData.user_id!!.toString())
                            if (mFinalAPLimit.toDouble() > 0) {
                                var mApprveDate =
                                    db!!.getCustomerApproveDate(mCustData.user_id!!.toString())
                                if (mApprveDate.isEmpty()) {
                                    if (Utils.passEndDate(Utils.getCurrentDate(), mApprveDate)) {
                                        mFinalAPLimit = "0"
                                    }
                                }
                            }

                            var mFInalBalance =
                                (mFinalOutstLimit.toDouble() + mFinalAPLimit.toDouble()) - customerOutstanding
                            if (mFInalBalance < 0) {
                                db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "2")
                            }
                        }
                    } else {
                        var mFinalAPLimit =
                            db!!.getCustomerApproveLimit(mCustData.user_id!!.toString())

                        var mFinalOutstLimit =
                            db!!.getCustomerApproveLimitOutstanding(mCustData.user_id!!.toString())
                        if (mFinalAPLimit.toDouble() > 0) {
                            var mApprveDate =
                                db!!.getCustomerApproveDate(mCustData.user_id!!.toString())
                            if (mApprveDate.isEmpty()) {
                                if (Utils.passEndDate(Utils.getCurrentDate(), mApprveDate)) {
                                    mFinalAPLimit = "0"
                                }
                            }
                        }
                        var mFInalBalance =
                            (mFinalOutstLimit.toDouble() + mFinalAPLimit.toDouble()) - customerOutstanding
                        if (mFInalBalance < 0) {
                            db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "2")
                        }
                    }
                } else {
                    db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "2")
                }

            }
        }


        dismissDialog()
    }

    private fun getSalesmanCouponItem() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty(
            "salesman_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.userId.toString()
        )

        ApiClient.getClient(this).getCouponList(
            "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<CouponData> {
            override fun onResponse(
                call: Call<CouponData>,
                response: Response<CouponData>
            ) {
                Utils.logData(
                    this@MasterActivity,
                    "Mer Coupon Response$response"
                )
                if (response.isSuccessful) {
                    Utils.logData(
                        this@MasterActivity,
                        "Mer Coupon Response${response.body().toString()}"
                    )
                    if (response.body()!!.status!!) {
                        db!!.deleteCouponMaster()
                        db!!.insertCouponItem(response.body()!!.data)
                    }
                }

                dismissDialog()

            }

            override fun onFailure(call: Call<CouponData>, error: Throwable) {
                if (error.message != null) {
                    Utils.logData(
                        this@MasterActivity,
                        "Mer Coupon Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@MasterActivity,
                        "Mer Coupon Response API Fail"
                    )
                }
                dismissDialog()
            }
        })
    }

}