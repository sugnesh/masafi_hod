package com.engear.vansales.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityDownloadingDataBinding
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class DownloadingDataActivity : BaseActivity() {
    lateinit var binding: ActivityDownloadingDataBinding
    var sLobId = ""
    var arrData: List<PDPData.PriceData> = ArrayList()
    var arrDataDiscount: List<PDPData.PriceData> = ArrayList()
    var arrOutstandingData: List<OutstandingData.CollectionData> = ArrayList()

    companion object {

        class MyAsyncInsertPromoTask internal constructor(context: DownloadingDataActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<DownloadingDataActivity> =
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

        class MyAsyncInsertDiscTask internal constructor(context: DownloadingDataActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<DownloadingDataActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                if (activity!!.arrDataDiscount.isNotEmpty()) {
                    activity!!.db!!.deleteDiscount()
                    activity!!.db!!.insertPDPDiscount(activity!!.arrDataDiscount)
                }

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                activity.dismissDialog()
                activity.openOrdertype()
            }

        }

        class MyAsyncInsertOutstandingTask internal constructor(context: DownloadingDataActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<DownloadingDataActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                if (activity!!.arrOutstandingData.isNotEmpty()) {
                    activity!!.db!!.insertOutStandingInvoice(activity!!.arrOutstandingData, "")
                }

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                activity.dismissDialog()
                activity.openReasonNext()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadingDataBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@DownloadingDataActivity,
            "On Download Screen "
        )
        setFullScreen()
        startAnim()
        downloadData()
    }

    private fun openOtherTask() {
        if (db!!.getDiscountCount()) {
            getOrderType()
        } else {
            getCustomerDiscount()
        }
    }

    private fun openOrdertype() {
        getOrderType()
    }

    private fun openReasonNext() {
        if (db!!.getReasonCount()) {
            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
               // getDeliveryLoad()
                getPromotionGroupItem()
            } else {
                getSalesRestricData()
            }
        } else {
            getReason()
        }

    }

    private fun downloadData() {

        if (mPreferenceManager!!.getUserInfo() != null) {
            var salesLOB: List<UserData.Data.UserInfo.SalesmanInfo.SalesmanLob>? = null
            if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList != null) {
                salesLOB = mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList
            }

            if (salesLOB!!.isNotEmpty()) {
                for (mSLob in salesLOB!!) {
                    sLobId = mSLob.lob_id.toString()
                }
            }
        }

        if (db!!.getItemCOunt()) {
            if (db!!.getUOMCOunt()) {
                if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
                    getDeliveryCustomer()
                } else {
                    if (db!!.getCustomerCOunt()) {
                        getJourneyPlan()
                    } else {
                        getCustomer()
                    }
                }

            } else {
                getAllUOM()
            }
        } else {
            getBrandList()
        }

    }

    fun startAnim() {
        binding.avi.show()
        // or avi.smoothToShow();
    }

    private fun getBrandList() {

        Log.e("API CALL", "Brand Call")
        ApiClient.getClient(this).getBrandList(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN
        ).enqueue(object : Callback<NodeBrand> {
            override fun onResponse(
                call: Call<NodeBrand>,
                response: Response<NodeBrand>
            ) {
                Utils.logData(
                    this@DownloadingDataActivity,
                    "Brand Response$response"
                )
                if (response.isSuccessful) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Brand Response${response.body().toString()}"
                    )
                    if (response.body()!!.status!!) {
                        db!!.insertBrand(response.body()!!.data)
                    }
                }

                getCategoryList()
            }

            override fun onFailure(call: Call<NodeBrand>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                if (error.message != null) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Brand Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Brand Response API Fail"
                    )
                }

                getCategoryList()
            }
        })
    }

    private fun getCategoryList() {
        Log.e("API CALL", "Category Call")
        ApiClient.getClient(this).getCategoryList(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN
        ).enqueue(object : Callback<NodeBrand> {
            override fun onResponse(
                call: Call<NodeBrand>,
                response: Response<NodeBrand>
            ) {
                Utils.logData(
                    this@DownloadingDataActivity,
                    "Category Response$response"
                )
                if (response.isSuccessful) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Category Response${response.body().toString()}"
                    )
                    if (response.body()!!.status!!) {
                        db!!.insertCategory(response.body()!!.data)
                    }
                }

                getItem()
            }

            override fun onFailure(call: Call<NodeBrand>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                if (error.message != null) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Category Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Category Response API Fail"
                    )
                }

                getItem()
            }
        })
    }

    private fun getItem() {
        Log.e("API CALL", "Item Call")
        ApiClient.getClient(this)
            .getAllItem("Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN)
            .enqueue(object : Callback<ItemData> {
                override fun onResponse(
                    call: Call<ItemData>,
                    response: Response<ItemData>
                ) {
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@DownloadingDataActivity,
                            "Item Response${response.body().toString()}"
                        )
                        if (response.body()!!.status!!) {
                            db!!.insertItem(response.body()!!.data)
                        }
                    }
                    if (db!!.getUOMCOunt()) {

                        if (db!!.getCustomerCOunt()) {
                            getJourneyPlan()
                        } else {
                            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
                                getDeliveryCustomer()
                            } else {
                                getCustomer()
                            }
                        }
                    } else {
                        getAllUOM()
                    }
                }

                override fun onFailure(call: Call<ItemData>, error: Throwable) {
                    // Utils.logData(this@DownloadingDataActivity, "Item Failed")
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "ItemFail Response${error.toString()}"
                    )
                    if (db!!.getUOMCOunt()) {
                        if (db!!.getCustomerCOunt()) {
                            getJourneyPlan()
                        } else {
                            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
                                getDeliveryCustomer()
                            } else {
                                getCustomer()
                            }
                        }
                    } else {
                        getAllUOM()
                    }
                }
            })
    }

    private fun getAllUOM() {
        Log.e("API CALL", "UOM Call")
        ApiClient.getClient(this)
            .getAllUOM("Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN)
            .enqueue(object : Callback<AllUOMData> {
                override fun onResponse(
                    call: Call<AllUOMData>,
                    response: Response<AllUOMData>
                ) {
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@DownloadingDataActivity,
                            "UOM Response${response.body().toString()}"
                        )
                        //Utils.logData(this@DownloadingDataActivity, "Item Response")
                        if (response.body()!!.status!!) {
                            db!!.insertUOM(response.body()!!.data!!)
                        }
                    }

                    if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
                        getDeliveryCustomer()
                    } else {
                        getCustomer()
                    }


                }

                override fun onFailure(call: Call<AllUOMData>, error: Throwable) {
                    // Utils.logData(this@DownloadingDataActivity, "Item Failed")
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "UOM Response${error.toString()}"
                    )
                    if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
                        getDeliveryCustomer()
                    } else {
                        getCustomer()
                    }
                }
            })
    }

    private fun getCustomer() {
        Log.e("API CALL", "Customer Call")
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        Log.e("CustomerData", jObj.toString())
        ApiClient.getClient(this).getAllCustomer(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<CustomerData> {
                override fun onResponse(
                    call: Call<CustomerData>,
                    response: Response<CustomerData>
                ) {
                    Log.e("API CALL", "CUstomer  Response")
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                        if (response.body()!!.status!!) {
//                            Log.e("CustomerData",Gson().toJson(response.body()!!.data))
                            db!!.insertCustomer(response.body()!!.data, sLobId)
                        }
                    }

                    if (mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
                        getDeliveryCustomer()
                    }else{
                        getJourneyPlan()
                    }

                }

                override fun onFailure(call: Call<CustomerData>, error: Throwable) {
                    Utils.logData(this@DownloadingDataActivity, "Customer Failed")
                    if (mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
                        getDeliveryCustomer()
                    }else{
                        getJourneyPlan()
                    }
                }
            })
    }

    private fun getDeliveryCustomer() {
        Log.e("API CALL", "Customer Call")
        val jObj = JsonObject()
        jObj.addProperty("salesman_id", userInfo!!.data.userInfo.salesmanInfo.userId)
        Log.e("CustomerData", jObj.toString())
        ApiClient.getClient(this).getDeliveryCustomer(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<CustomerData> {
                override fun onResponse(
                    call: Call<CustomerData>,
                    response: Response<CustomerData>
                ) {
                    Log.e("API CALL", "CUstomer  Response")
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                        if (response.body()!!.status!!) {
//                            Log.e("CustomerData",Gson().toJson(response.body()!!.data))
                            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {

                            }else{
                                db!!.deleteCUstomer()
                            }

                            db!!.insertCustomer(response.body()!!.data, sLobId)
                        }
                    }
                    getJourneyPlan()
                }

                override fun onFailure(call: Call<CustomerData>, error: Throwable) {
                    Utils.logData(this@DownloadingDataActivity, "Customer Failed")
                    getJourneyPlan()
                }
            })
    }


    private fun getJourneyPlan() {
        Log.e("API CALL", "Jounrey Plan Call")
        ApiClient.getClient(this).getJourneyPlan(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            userInfo!!.data.userInfo.salesmanInfo.routeId
        ).enqueue(object : Callback<JourneyPlanData> {
            override fun onResponse(
                call: Call<JourneyPlanData>,
                response: Response<JourneyPlanData>
            ) {
//                Log.e("JourneyPlan",Gson().toJson(response.body()!!.data))
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    Log.e("JourneyPlan", Gson().toJson(response.body()!!.data))
                    if (response.body()!!.status!!) {
                        db!!.deleteJounreyPlan()
                        db!!.insertJourneyPlan(response.body()!!.data!!)
                    }
                }

                if (db!!.getPriceCount()) {
                    if (db!!.getPromotionCount()) {
                        if (db!!.getDiscountCount()) {
                            getOrderType()
                        } else {
                            getCustomerDiscount()
                        }
                    } else {
                        getCustomerPromotion()
                    }
                } else {
                    getPDPDATA()
                }

            }

            override fun onFailure(call: Call<JourneyPlanData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                if (db!!.getPriceCount()) {
                    if (db!!.getPromotionCount()) {
                        if (db!!.getDiscountCount()) {
                            getOrderType()
                        } else {
                            getCustomerDiscount()
                        }
                    } else {
                        getCustomerPromotion()
                    }
                } else {
                    getPDPDATA()
                }
            }
        })
    }


    private fun getPDPDATA() {

        Log.e("API CALL", "Pricing Call")
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
                        db!!.insertPriceMasafi(response.body()!!.data, "Site")
                    }
                }
                getPDPParentDATA()
            }

            override fun onFailure(call: Call<PriceMFData>, error: Throwable) {
                Utils.logData(this@DownloadingDataActivity, "Customer Failed")
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
                Utils.logData(this@DownloadingDataActivity, "Customer Failed")
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
                Utils.logData(this@DownloadingDataActivity, "Customer Failed")
                getPDPSubChannelDATA()
            }
        })

    }

    private fun getPDPSubChannelDATA() {

        Log.e("API CALL", "PricingSubChanne Call")

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

                getOrderType()
//                if (db!!.getPromotionCount()) {
//                    if (db!!.getDiscountCount()) {
//                        getOrderType()
//                    } else {
//                        getCustomerDiscount()
//                    }
//                } else {
//                    getCustomerPromotion()
//                }
            }

            override fun onFailure(call: Call<PriceMFData>, error: Throwable) {
                Utils.logData(this@DownloadingDataActivity, "Customer Failed")
//                if (db!!.getPromotionCount()) {
//                    if (db!!.getDiscountCount()) {
//                        getOrderType()
//                    } else {
//                        getCustomerDiscount()
//                    }
//                } else {
//                    getCustomerPromotion()
//                }

                getOrderType()
            }
        })

    }
    private fun getCustomerPromotion() {
        Log.e("API CALL", "Promotion Call")
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
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                        if (response.body()!!.status!!) {

                            arrData = ArrayList()
                            arrData = response.body()!!.data
                            //db!!.insertPDPPromotion(response.body()!!.data)
                        }
                    }

                    val task = MyAsyncInsertPromoTask(this@DownloadingDataActivity)
                    task.execute(10)


                }

                override fun onFailure(call: Call<PDPData>, error: Throwable) {
                    Utils.logData(this@DownloadingDataActivity, "Customer Failed")
                    if (db!!.getDiscountCount()) {
                        getOrderType()
                    } else {
                        getCustomerDiscount()
                    }
                }
            })
    }

    private fun getCustomerDiscount() {

        Log.e("API CALL", "Discount Call")
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
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                        if (response.body()!!.status!!) {
                            arrDataDiscount = ArrayList()
                            arrDataDiscount = response.body()!!.data
                            // db!!.insertPDPDiscount(response.body()!!.data)
                        }
                    }

                    val task = MyAsyncInsertDiscTask(this@DownloadingDataActivity)
                    task.execute(10)

                    // getOrderType()
                }

                override fun onFailure(call: Call<PDPData>, error: Throwable) {
                    Utils.logData(this@DownloadingDataActivity, "Customer Failed")
                    getOrderType()
                }
            })
    }

    private fun getOrderType() {
        Log.e("API CALL", "Order Type Call")
        ApiClient.getClient(this).getOrderType(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN
        )
            .enqueue(object : Callback<OrderTypeData> {
                override fun onResponse(
                    call: Call<OrderTypeData>,
                    response: Response<OrderTypeData>
                ) {
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                        if (response.body()!!.status) {
                            db!!.insertOrderType(response.body()!!.data)
                        }
                    } else {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Blank")
                    }

                    if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                        getOutstandingInvoice()
                    } else {
                        getDelivery()
                    }

                }

                override fun onFailure(call: Call<OrderTypeData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                    if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                        getOutstandingInvoice()
                    } else {
                        getDelivery()
                    }
                }
            })
    }

    private fun getDelivery() {
        Log.e("API CALL", "Delviery Call")
        ApiClient.getClient(this).getDelivery(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            userInfo!!.data.userInfo.salesmanInfo.userId
        ).enqueue(object : Callback<DeliveryData> {
            override fun onResponse(
                call: Call<DeliveryData>,
                response: Response<DeliveryData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status) {
                        db!!.insertDelivery(response.body()!!.data)
                    }
                } else {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Blank")
                }
                getReturn()
                //getOutstandingInvoice()
            }

            override fun onFailure(call: Call<DeliveryData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                getReturn()
                //getOutstandingInvoice()
            }
        })
    }

    private fun getReturn() {

        ApiClient.getClient(this).getReturnData(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            userInfo!!.data.userInfo.salesmanInfo.userId
        ).enqueue(object : Callback<CreditData> {
            override fun onResponse(
                call: Call<CreditData>,
                response: Response<CreditData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status) {
                        db!!.insertReturnData(response.body()!!.data)
                    }
                } else {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Blank")
                }
                getOutstandingInvoice()
            }

            override fun onFailure(call: Call<CreditData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                getOutstandingInvoice()
            }
        })
    }

    /*
        private fun getOutstandingInvoice() {

            ApiClient.getClient(this).getOutStandingInvoice(
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
                            db!!.insertOutStandingInvoice(response.body()!!.data)
                        }
                    } else {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Blank")
                    }
                    getRoutePendingInvoice()
                }

                override fun onFailure(call: Call<OutstandingData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                    getRoutePendingInvoice()
                }
            })
        }
    */

    private fun getOutstandingInvoice() {
        Log.e("API CALL", "Outstanding Call")
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
                    Utils.logData(this@DownloadingDataActivity, "Outstanding Response")
                    if (response.body()!!.status!!) {
                        arrOutstandingData = ArrayList()
                        arrOutstandingData = response.body()!!.data
                        //db!!.insertOutStandingInvoice(response.body()!!.data, sLobId)
                    }
                }

                val task = MyAsyncInsertOutstandingTask(this@DownloadingDataActivity)
                task.execute(10)
                // getReason()
            }

            override fun onFailure(call: Call<OutstandingData>, error: Throwable) {
                Utils.logData(this@DownloadingDataActivity, "Outstanding Failed")
                //getReason()
                if (db!!.getReasonCount()) {
                    if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4" || mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
                        //getDeliveryLoad()
                        getPromotionGroupItem()
                    } else {
                        getSalesRestricData()
                    }
                } else {
                    getReason()
                }
            }
        })
    }

    private fun getReason() {
        Log.e("API CALL", "Reason Call")
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
                        db!!.insertReason(response.body()!!.data)
                    }
                }

                if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4" || mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
                    //getDeliveryLoad()
                    getPromotionGroupItem()
                } else {
                    getSalesRestricData()
                }

            }

            override fun onFailure(call: Call<ReasonData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4" || mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
                    getPromotionGroupItem()
                } else {
                    getSalesRestricData()
                }

            }
        })
    }

    private fun getSalesRestricData() {
        Log.e("API CALL", "SalesRest Call")

        val jObj = JsonObject()
        jObj.addProperty("salesman_id", userInfo!!.data.userInfo.salesmanInfo.userId)

        ApiClient.getClient(this).getSalesRestrictData(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<SalesRestrictData> {
            override fun onResponse(
                call: Call<SalesRestrictData>,
                response: Response<SalesRestrictData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertSalesRestrictData(response.body()!!.data)
                    }
                }

                getSalesTargetData()

            }

            override fun onFailure(call: Call<SalesRestrictData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                getSalesTargetData()

            }
        })
    }

    private fun getSalesTargetData() {
        Log.e("API CALL", "SalesTarget Call")

        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId.toString())

        ApiClient.getClient(this).getSalesTargetData(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<SalesTargetData> {
            override fun onResponse(
                call: Call<SalesTargetData>,
                response: Response<SalesTargetData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertSalesTargetData(response.body()!!.data)
                    }
                }

                getPromotionGroupItem()
//                if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4" || mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
//                   // getDeliveryLoad()
//                    getPromotionGroupItem()
//                } else {
//                    getLoad()
//                }

            }

            override fun onFailure(call: Call<SalesTargetData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                getPromotionGroupItem()
//                if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
//                   // getDeliveryLoad()
//                    getPromotionGroupItem()
//                } else {
//                    getLoad()
//                }

            }
        })
    }

    private fun getLoad() {
        Log.e("API CALL", "Load Call")
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        jObj.addProperty("load_date", Utils.getCurrentDate())

        ApiClient.getClient(this).getLoadList(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN, jObj
        )
            .enqueue(object : Callback<LoadData> {
                override fun onResponse(
                    call: Call<LoadData>,
                    response: Response<LoadData>
                ) {
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                        if (response.body()!!.status!!) {
                            db!!.insertLoad(response.body()!!.data!!)
                        }
                    }

                    getPromotionGroupItem()
                }

                override fun onFailure(call: Call<LoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    getPromotionGroupItem()
                }
            })
    }

    private fun getDeliveryLoad() {
        Log.e("API CALL", "Load Call")
        val jObj = JsonObject()
        jObj.addProperty("salesman_id", userInfo!!.data.userInfo.salesmanInfo.userId)
        jObj.addProperty("load_date", Utils.getCurrentDate())

        ApiClient.getClient(this).getDeliveryLoadList(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN, jObj
        )
            .enqueue(object : Callback<LoadData> {
                override fun onResponse(
                    call: Call<LoadData>,
                    response: Response<LoadData>
                ) {
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                        if (response.body()!!.status!!) {
                            db!!.insertLoad(response.body()!!.data!!)
                        }
                    }

                    getPromotionGroupItem()
                }

                override fun onFailure(call: Call<LoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    getPromotionGroupItem()
                }
            })
    }

    private fun getDataCollection() {
        Log.e("API CALL", "DataCollection Call")
        val jObj = JsonObject()

        var arr = JsonArray()
        var cahne = "channel"
        arr.add(cahne!!)
        arr.add("region")
        arr.add("sales_organisation")
        arr.add("customer_category")
        jObj.add("list_data", arr)
        Log.e("dataclctn", jObj.toString())
        ApiClient.getClient(this).getDatCollection(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN, jObj
        ).enqueue(object : Callback<DataCollection> {
            override fun onResponse(
                call: Call<DataCollection>,
                response: Response<DataCollection>
            ) {

                if (response.isSuccessful) {
//                    var strv = response.body()!!.string()
//                    Log.e("saleOrg", strv)

                    db!!.insertChannel(response.body()!!.data!!.channel)
                    db!!.insertRegion(response.body()!!.data!!.region)
                    db!!.insertSaleOrg(response.body()!!.data!!.salesOrganisation)
                    db!!.insertCustomerCategory(response.body()!!.data!!.customerCategory)
                } else {
                }
                getPromotionGroupItem()

            }

            override fun onFailure(call: Call<DataCollection>, error: Throwable) {
                Utils.logData(this@DownloadingDataActivity, "Customer Failed")
                getPromotionGroupItem()
            }
        })
    }

    private fun getDistribution() {
        Log.e("API CALL", "Sheld Display Call")
        ApiClient.getClient(this).getCustomerDistribution(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            userInfo!!.data.userInfo.salesmanInfo.routeId
        ).enqueue(object : Callback<DistributionData> {
            override fun onResponse(
                call: Call<DistributionData>,
                response: Response<DistributionData>
            ) {
                Utils.logData(
                    this@DownloadingDataActivity,
                    "Distribution Response$response"
                )
                if (response.isSuccessful) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Distribution Response${response.body().toString()}"
                    )
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertCustomerDistribution(response.body()!!.data)
                    }
                }

                if (db!!.getCustomerItemCount().toInt() > 0) {
                    getMerchandiserPortfolio()
                } else {
                    getCustomePortfolio()
                }
            }

            override fun onFailure(call: Call<DistributionData>, error: Throwable) {
                if (error.message != null) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Distribution Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Distribution Response API Fail"
                    )
                }
                if (db!!.getCustomerItemCount().toInt() > 0) {
                    getMerchandiserPortfolio()
                } else {
                    getCustomePortfolio()
                }
            }
        })
    }

    private fun getCustomePortfolio() {
        Log.e("API CALL", "Portfolio Call")
        ApiClient.getClient(this).getRouteCustomerPortfolio(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            userInfo!!.data.userInfo.salesmanInfo.routeId
        ).enqueue(object : Callback<RoutePortfolioData> {
            override fun onResponse(
                call: Call<RoutePortfolioData>,
                response: Response<RoutePortfolioData>
            ) {
                Utils.logData(
                    this@DownloadingDataActivity,
                    "Portfolio Response$response"
                )
                if (response.isSuccessful) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Portfolio Response${response.body().toString()}"
                    )
                    if (response.body()!!.status!!) {
                        db!!.insertCustomerROutePoretfolioItem(response.body()!!.data)
                    }
                }

                getMerchandiserPortfolio()
            }

            override fun onFailure(call: Call<RoutePortfolioData>, error: Throwable) {
                if (error.message != null) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "CompetitorBrand Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "CompetitorBrand Response API Fail"
                    )
                }

                getMerchandiserPortfolio()
            }
        })
    }

    private fun getMerchandiserPortfolio() {

        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        // jObj.addProperty("route_id",userInfo!!.data.userInfo.salesmanInfo.routeId )

        ApiClient.getClient(this).getRouteItemPortfolio(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<RouteItemData> {
            override fun onResponse(
                call: Call<RouteItemData>,
                response: Response<RouteItemData>
            ) {
                Utils.logData(
                    this@DownloadingDataActivity,
                    "Mer Portfoloio Response$response"
                )
                if (response.isSuccessful) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Mer Portfolio Response${response.body().toString()}"
                    )
                    if (response.body()!!.status!!) {
                        db!!.insertRouteItemPortfolio(response.body()!!.data, "merchandising")
                    }
                }
                getPromotionGroupItem()
            }

            override fun onFailure(call: Call<RouteItemData>, error: Throwable) {
                if (error.message != null) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Mer Portfolio Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Mer Portfolio Response API Fail"
                    )
                }
                getPromotionGroupItem()
            }
        })
    }

    private fun getPromotionGroupItem() {

        ApiClient.getClient(this).getPromotionGroupItem(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN
        ).enqueue(object : Callback<PromotionGroupData> {
            override fun onResponse(
                call: Call<PromotionGroupData>,
                response: Response<PromotionGroupData>
            ) {
                Utils.logData(
                    this@DownloadingDataActivity,
                    "Mer Portfoloio Response$response"
                )
                if (response.isSuccessful) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Mer Portfolio Response${response.body().toString()}"
                    )
                    if (response.body()!!.status!!) {
                        db!!.insertPromotionGroupItem(response.body()!!.data)
                    }
                }
                getSalesmanCouponItem()
            }

            override fun onFailure(call: Call<PromotionGroupData>, error: Throwable) {
                if (error.message != null) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Mer Portfolio Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Mer Portfolio Response API Fail"
                    )
                }
                getSalesmanCouponItem()
            }
        })
    }

    private fun getSalesmanCouponItem() {

        val jObj = JsonObject()
        jObj.addProperty("salesman_id", userInfo!!.data.userInfo.salesmanInfo.userId.toString())

        ApiClient.getClient(this).getCouponList(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<CouponData> {
            override fun onResponse(
                call: Call<CouponData>,
                response: Response<CouponData>
            ) {
                Utils.logData(
                    this@DownloadingDataActivity,
                    "Mer Portfoloio Response$response"
                )
                if (response.isSuccessful) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Mer Portfolio Response${response.body().toString()}"
                    )
                    if (response.body()!!.status!!) {
                        db!!.insertCouponItem(response.body()!!.data)
                    }
                }
                launchScreen()
            }

            override fun onFailure(call: Call<CouponData>, error: Throwable) {
                if (error.message != null) {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Mer Portfolio Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@DownloadingDataActivity,
                        "Mer Portfolio Response API Fail"
                    )
                }
                launchScreen()
            }
        })
    }

    private fun launchScreen() {
        Handler().postDelayed({
            startActivity(
                Intent(
                    this@DownloadingDataActivity,
                    DashboardActivity::class.java
                ).apply {
                    this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
        }, 1000)
    }
}