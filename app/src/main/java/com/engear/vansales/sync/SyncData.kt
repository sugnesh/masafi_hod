package com.engear.vansales.sync

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.Constants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.CardFail
import com.engear.vansales.model.Collection
import com.engear.vansales.model.Invoice
import com.engear.vansales.model.MyErrorMessage
import com.engear.vansales.model.Order
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SyncData : IntentService(TAG) {
    private var mPreferenceManager: SharedPreferenceManager? = null
    var intent: Intent
    var db: DatabaseHandler? = null

    companion object {
        var TAG = "SyncData"
        const val BROADCAST_ACTION = "com.sfa.UPDATE"
        private var instance: SyncData? = null

        @Synchronized
        fun getInstance(): SyncData? {
            if (instance == null) {
                instance = SyncData()
            }
            return instance
        }
    }

    init {
        instance = this
        intent = Intent(BROADCAST_ACTION)
    }

    override fun onCreate() {
        super.onCreate()

        db = DatabaseHandler(this)
        mPreferenceManager = SharedPreferenceManager(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_ID = "com.mobiato.sfasaas"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "sfa", NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification =
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("App is running in background").build()
            startForeground(AppConstants.COUNT, notification)
            AppConstants.COUNT++
        }

    }

    override fun onHandleIntent(intent: Intent?) {
        Log.e(
            "Data1 : ",
            "IntentData" + mPreferenceManager!!.getString(AppConstants.IS_DATA_SYNCING)
        )
        if (mPreferenceManager!!.getString(AppConstants.IS_DATA_SYNCING) != null) {
            if (!(mPreferenceManager!!.getString(AppConstants.IS_DATA_SYNCING))!!.toBoolean()) {
                Log.d(TAG, "syncData: " + true)
                syncData()
            }
        }
    }

    private fun syncData() {

        //Post Load
        if (getSyncCount(AppConstants.LOAD) > 0) {
            if (!AppConstants.isLoadSync) {
                AppConstants.isLoadSync = true
                val jObj = getLoadForPost()
                callLoadConfirmAPI(jObj!!)
            }
        }

        //Post Order
        if (getSyncCount(AppConstants.ORDER) > 0) {
            if (!AppConstants.isOrderSync) {
                AppConstants.isOrderSync = true
                val jObj = getOrderForPost()
                callOrderAPI(jObj!!)
            }
        }

        //Post Load Request Order
        if (getSyncCount(AppConstants.LOAD_REQUEST) > 0) {
            if (!AppConstants.isLoadRequestSync) {
                AppConstants.isLoadRequestSync = true
                val jObj = getLoadOrderForPost()
                callLoadOrderAPI(jObj!!)
            }
        }

        //Post Load Variance Order
        if (getSyncCount(AppConstants.LOAD_VARIANCE_REQUEST) > 0) {
            if (!AppConstants.isLoadVarianceRequestSync) {
                AppConstants.isLoadVarianceRequestSync = true
                val jObj = getUnloadLoadVariForPost()
                callLoadvarUnloadAPI(jObj!!)
//                val jObj = getLoadVarianceRequestForPost()
//                callLoadVarianceAPI(jObj!!)
            }
        }

        //Post Invoice Request Order
        if (getSyncCount(AppConstants.SALES_INVOICE) > 0) {
            if (!AppConstants.isInvoiceSync) {
                AppConstants.isInvoiceSync = true
                val jObj = getInvoiceForPost()
                callInvoiceAPI(jObj!!)
            }
        }

        //Post Fail Request Card
//        if (getSyncCount(AppConstants.CARD_FAIL) > 0) {
//            if (!AppConstants.isCardFailSync) {
//                AppConstants.isCardFailSync = true
//                val jObj = getCardFailPost()
//                callCardFailAPI(jObj!!)
//            }
//        }

        if (getSyncCount(AppConstants.STAMP_INVOICE) > 0) {
            if (!AppConstants.isInvoiceStampSync) {
                AppConstants.isInvoiceStampSync = true
                val jObj = getInvoiceSTampForPost()
                callStampInvoiceAPI(jObj!!)
            }
        }

        //Post Invoice Request Order
        if (getSyncCount(AppConstants.DELIVERY_CANCEL) > 0) {
            if (!AppConstants.isDelvierySync) {
                AppConstants.isDelvierySync = true
                val jObj = getDeliveryForPost()
                callDeliveryAPI(jObj!!)
            }
        }

        //Post Invoice Request Order
//        if (getSyncCount(AppConstants.INVOICE_CANCEL) > 0) {
//            if (!AppConstants.isDelvierySync) {
//                AppConstants.isDelvierySync = true
//                val jObj = getCancelInvoiceForPost()
//               // callInvoiceDeleteAPI(jObj!!)
//                callInvoiceCancelAPI(jObj!!)
//            }
//        }

        //Post Collection Request Order
        if (getSyncCount(AppConstants.COLLECTION) > 0) {
            if (!AppConstants.isCollectionSync) {
                AppConstants.isCollectionSync = true
                var arrHECOllection = db!!.getAllCollectionHeader()
                var isCollect = false
                var mCollection = Collection()
                for (mCollData in arrHECOllection) {
                    if (mCollData.cashType == "1") {
                        if (db!!.getInvoicePosted(mCollData.invNo.toString())) {
                            mCollection = mCollData
                            isCollect = true
                            break
                        }
                    } else {
                        mCollection = mCollData
                        isCollect = true
                        break
                    }
                }

                /* var arrCollection = db!!.getAllCollection()
                 var isCollect = false
                 var mCollection: OutstandingData.CollectionData? = null
                 for (mCollData in arrCollection) {
                     if (mCollData.tempAmountDue == "1") {
                         mCollection = mCollData
                         isCollect = true
                         break
                     } else {
                         if (db!!.getInvoicePosted(mCollData.invoice_number)) {
                             mCollection = mCollData
                             isCollect = true
                             break
                         }
                     }
                 }*/

                if (isCollect) {
                    if (mCollection != null) {
                        val jObj = getCollectionForPost(mCollection!!)
                        callCollectionAPI(jObj!!)
                    } else {
                        AppConstants.isCollectionSync = false
                    }
                } else {
                    AppConstants.isCollectionSync = false
                }
            }
        }

        //Post Return Request Order
        if (getSyncCount(AppConstants.RETURN) > 0) {
            if (!AppConstants.isReturnSync) {
                AppConstants.isReturnSync = true
                val jObj = getReturnForPost()
                callReturnAPI(jObj!!)
            }
        }

        //Post Return Request Order
        if (getSyncCount(AppConstants.RETURN_REQUEST) > 0) {
            if (!AppConstants.isReturnRequestSync) {
                AppConstants.isReturnRequestSync = true
                val jObj = getReturnRequestForPost()
                callReturnCompletePostAPINew(jObj!!)
            }
        }

        if (getSyncCount(AppConstants.RETURN_CANCEL_REQUEST) > 0) {
            if (!AppConstants.isReturnCancelRequestSync) {
                AppConstants.isReturnCancelRequestSync = true
                val jObj = getReturnCancelRequestForPost()
                callReturnCancelAPINew(jObj!!)
            }
        }

        if (getSyncCount(AppConstants.RETURN_PALLET_REQUEST) > 0) {
            if (!AppConstants.isReturnPalletRequestSync) {
                AppConstants.isReturnPalletRequestSync = true
                val jObj = getReturnPalletRequestForPost()
                callReturnPallerAPINew(jObj!!)
            }
        }

        //Post Unload Request Order
        if (getSyncCount(AppConstants.UNLOAD_REQUEST) > 0) {
            if (!AppConstants.isUnloadRequestSync) {
                AppConstants.isUnloadRequestSync = true
                val jObj = getUnloadForPost()
                callUnloadAPI(jObj!!)
            }
        }

        //Post Audit Unload Request Order
        if (getSyncCount(AppConstants.UNLOAD_STOCK_AUDIT) > 0) {
            if (!AppConstants.isUnloadRequestSync) {
                AppConstants.isUnloadRequestSync = true
                val jObj = getUnloadForAuditPost()
                callUnloadAPI(jObj!!)
            }
        }
        //Post Bad Unload Request Order
        if (getSyncCount(AppConstants.UNLOAD_BAD_ITEM) > 0) {
            if (!AppConstants.isUnloadRequestSync) {
                AppConstants.isUnloadRequestSync = true
                val jObj = getUnloadForBadVarPost()
                callUnloadAPI(jObj!!)
            }
        }

        //Post Audit Unload Request Order
        if (getSyncCount(AppConstants.UNLOAD_STOCK_AUDIT_VARIANCE) > 0) {
            if (!AppConstants.isUnloadRequestSync) {
                AppConstants.isUnloadRequestSync = true
                val jObj = getUnloadForAuditVarPost()
                callUnloadAPI(jObj!!)
            }
        }

        //Post Unload Varience Request Order
        if (getSyncCount(AppConstants.UNLOAD_VARIANCE_REQUEST) > 0) {
            if (!AppConstants.isUnloadVarianceRequestSync) {
                AppConstants.isUnloadVarianceRequestSync = true
                val jObj = getUnloadVarieancePost()
                callUnloadVarianceAPI(jObj!!)
            }
        }


        //Post Unload Van Return Request Order
        if (getSyncCount(AppConstants.UNLOAD_VAN_RETURN_REQUEST) > 0) {
            if (!AppConstants.isUnloadVanRetunrRequestSync) {
                AppConstants.isUnloadVanRetunrRequestSync = true
                val jObj = getUnloadVanReturnPost()
                callUnloadVanReturnAPI(jObj!!)
            }
        }

        //Post Complaint Post
        if (getSyncCount(AppConstants.COMPLAIN_POST) > 0) {
            if (!AppConstants.isComplaintSync) {
                AppConstants.isComplaintSync = true
                val jObj = getComplainForPost()
                callComplaintAPI(jObj!!)
            }
        }

        //Post Suggestion Post
        if (getSyncCount(AppConstants.SUGGESTION_POST) > 0) {
            if (!AppConstants.isSuggestionSync) {
                AppConstants.isSuggestionSync = true
                val jObj = getSuggestionForPost()
                callSuggestionAPI(jObj!!)
            }
        }

        //Post Campaign Post
        if (getSyncCount(AppConstants.CAMPAIGN_POST) > 0) {
            if (!AppConstants.isCampaignSync) {
                AppConstants.isCampaignSync = true
                val jObj = getCampaignForPost()
                callCampaignAPI(jObj!!)
            }
        }

        //Post Competitor Post
        if (getSyncCount(AppConstants.COMPETITOR_POST) > 0) {
            if (!AppConstants.isCompititorSync) {
                AppConstants.isCompititorSync = true
                val jObj = getCompetitorForPost()
                callCompetitorAPI(jObj!!)
            }
        }

        //Post Inventory Post
        if (getSyncCount(AppConstants.INVENTORY_POST) > 0) {
            if (!AppConstants.isInventoryPost) {
                AppConstants.isInventoryPost = true
                val jObj = getInventoryForPost()
                callInventoryAPI(jObj!!)
            }
        }

        //Post Display Tool Post
        if (getSyncCount(AppConstants.TOOL_POST) > 0) {
            if (!AppConstants.isStockSync) {
                AppConstants.isStockSync = true
                val jObj = getDistributionForPost()
                callPostDistributionAPI(jObj!!)
            }
        }

        //Post Display Tool Image Post
        if (getSyncCount(AppConstants.TOOL_IMAGE_POST) > 0) {
            if (!AppConstants.isDisImageSync) {
                AppConstants.isDisImageSync = true
                val jObj = getDistributionImageForPost()
                callPostDistributionImageAPI(jObj!!)
            }
        }

        //Post Planogram Post
        if (getSyncCount(AppConstants.PLANOGRAM_POST) > 0) {
            if (!AppConstants.isPlanogramSync) {
                AppConstants.isPlanogramSync = true
                val jObj = getPlanogramForPost()
                callPostPlanogramAPI(jObj!!)
            }
        }

        //Post Assets Post
        if (getSyncCount(AppConstants.ASSETS_POST) > 0) {
            if (!AppConstants.isAssetsSync) {
                AppConstants.isAssetsSync = true
                val jObj = getAssetsForPost()
                callPostAssetsAPI(jObj!!)
            }
        }

        //Post Survey Post
        if (getSyncCount(AppConstants.SURVEY_POST) > 0) {
            if (!AppConstants.isConsumerPost) {
                AppConstants.isConsumerPost = true
                val jObj = getSurveyForPost()
                callPostSurveyAPI(jObj!!)
            }
        }

        //Post Promotional Post
        if (getSyncCount(AppConstants.PROMOTIONAL_POST) > 0) {
            if (!AppConstants.isPromotionalSync) {
                AppConstants.isPromotionalSync = true
                val jObj = getpromotionalForPost()
                callPostPromotionalAPI(jObj!!)
            }
        }

        //Post Visit Post
        if (getSyncCount(AppConstants.VISIT_POST) > 0) {
            if (!AppConstants.isVisitSync) {
                AppConstants.isVisitSync = true
                val jObj = getVisitForPost()
                callPostVisitAPI(jObj!!)
                db!!.markForVisitPost()
            }
        }

        //Post Visit Post
        if (getSyncCount(AppConstants.VISIT_ACTIVITY_POST) > 0) {
            if (!AppConstants.isVisitActivitySync) {
                AppConstants.isVisitActivitySync = true
                val jObj = getVisitActivityForPost()
                callPostVisitActivityAPI(jObj!!)

            }
        }

//        if (mPreferenceManager!!.getString(AppConstants.DAY_START) != null) {
//            if (mPreferenceManager!!.getString(AppConstants.DAY_START).equals("1")) {
//                if (!AppConstants.isTrackSync) {
//                    AppConstants.isTrackSync = true
//                    val jObj = getTrackForPost()
//                    callTrackAPI(jObj!!)
//                }
//            }
//
//        }

    }

    private fun getSyncCount(function: String?): Int {
        var syncCount = 0
        try {
            when (function) {
                AppConstants.LOAD -> {
                    val count = db!!.getPostLoadCount()
                    syncCount = count
                }

                AppConstants.ORDER -> {
                    val count = db!!.getPostOrderCount()
                    syncCount = count
                }

                AppConstants.LOAD_REQUEST -> {
                    val count = db!!.getPostLoadRequestCount()
                    syncCount = count
                }

                AppConstants.LOAD_VARIANCE_REQUEST -> {
                    val count = db!!.getPostUnLoadLoadRequestCount()
                    syncCount = count
                }

                AppConstants.UNLOAD_STOCK_AUDIT -> {
                    val count = db!!.getPostUnLoadAuditCount()
                    syncCount = count
                }

                AppConstants.UNLOAD_STOCK_AUDIT_VARIANCE -> {
                    val count = db!!.getPostUnLoadAuditVarianceCount()
                    syncCount = count
                }

                AppConstants.UNLOAD_BAD_ITEM -> {
                    val count = db!!.getPostUnBadRequestCount()
                    syncCount = count
                }

                AppConstants.SALES_INVOICE -> {
                    val count = db!!.getPostInvoiceCount()
                    syncCount = count
                }

                AppConstants.STAMP_INVOICE -> {
                    val count = db!!.getPostStampInvoiceCount()
                    syncCount = count
                }

                AppConstants.DELIVERY_CANCEL -> {
                    val count = db!!.getPostDelveiryCount()
                    syncCount = count
                }

                AppConstants.INVOICE_CANCEL -> {
                    val count = db!!.getPostInvoiceCancelCount()
                    syncCount = count
                }

                AppConstants.COLLECTION -> {
                    val count = db!!.getPostCollectionCount()
                    syncCount = count
                }

                AppConstants.RETURN -> {
                    val count = db!!.getPostReturnCount()
                    syncCount = count
                }

                AppConstants.RETURN_REQUEST -> {
                    val count = db!!.getPostReturnCompleteCount()
                    syncCount = count
                }

                AppConstants.RETURN_CANCEL_REQUEST -> {
                    val count = db!!.getPostReturnCancelCompleteCount()
                    syncCount = count
                }

//                AppConstants.CARD_FAIL -> {
//                    val count = db!!.getPostCardFail()
//                    syncCount = count
//                }

                AppConstants.RETURN_PALLET_REQUEST -> {
                    val count = db!!.getPostReturnPalletCount()
                    syncCount = count
                }

                AppConstants.COMPLAIN_POST -> {
                    val count = db!!.getPostComplaintCount()
                    syncCount = count
                }

                AppConstants.SUGGESTION_POST -> {
                    val count = db!!.getPostSuggestionCount()
                    syncCount = count
                }

                AppConstants.COMPETITOR_POST -> {
                    val count = db!!.getPostCompetitorCount()
                    syncCount = count
                }

                AppConstants.CAMPAIGN_POST -> {
                    val count = db!!.getPostCampaignCount()
                    syncCount = count
                }

                AppConstants.INVENTORY_POST -> {
                    val count = db!!.getPostInventoryCount()
                    syncCount = count
                }

                AppConstants.TOOL_POST -> {
                    val count = db!!.getPostToolCount()
                    syncCount = count
                }

                AppConstants.TOOL_IMAGE_POST -> {
                    val count = db!!.getPostToolImageCount()
                    syncCount = count
                }

                AppConstants.PLANOGRAM_POST -> {
                    val count = db!!.getPostPlanogramCount()
                    syncCount = count
                }

                AppConstants.ASSETS_POST -> {
                    val count = db!!.getPostAssetsCount()
                    syncCount = count
                }

                AppConstants.SURVEY_POST -> {
                    val count = db!!.getPostSurveyCount()
                    syncCount = count
                }

                AppConstants.PROMOTIONAL_POST -> {
                    val count = db!!.getPostPromotionalCount()
                    syncCount = count
                }

                AppConstants.VISIT_POST -> {
                    val count = db!!.getPostVisitCount()
                    syncCount = count
                }

                AppConstants.VISIT_ACTIVITY_POST -> {
                    val count = db!!.getPostVisitActivityCount()
                    syncCount = count
                }

                AppConstants.UNLOAD_REQUEST -> {
                    if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                        val count = db!!.getPostUnLoadRequestCount()
                        syncCount = count
                    }
                }

                AppConstants.UNLOAD_STOCK_AUDIT -> {
                    if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                        val count = db!!.getPostUnLoadAuditCount()
                        syncCount = count
                    }
                }

                AppConstants.UNLOAD_STOCK_AUDIT_VARIANCE -> {
                    // if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                    val count = db!!.getPostUnAuditVarianceCount()
                    syncCount = count
                    //}
                }

                AppConstants.UNLOAD_BAD_ITEM -> {
                    // if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                    val count = db!!.getPostUnBadRequestCount()
                    syncCount = count
                    //  }
                }
                /* AppConstants.UNLOAD_VAN_EXPIRY_REQUEST -> {
                     if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                         val count = db!!.getPostUnLoadVanExpiryRequestCount()
                         syncCount = count
                     }
                 }*/
                AppConstants.UNLOAD_VARIANCE_REQUEST -> {
                    if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                        val count = db!!.getPostUnLoadVarianceRequestCount()
                        syncCount = count
                    }
                }

                AppConstants.UNLOAD_VAN_RETURN_REQUEST -> {
                    if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                        val count = db!!.getPostUnLoadVanRetunrRequestCount()
                        syncCount = count
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return syncCount
    }

    //Load Post
    private fun getLoadForPost(): JsonObject? {

        val mLoad = db!!.getPostLoadDetail()
        val jsonArray = db!!.getPostConfirmItem(mLoad["LoadNumber"]!!)

        val jObj = JsonObject()

        try {
            jObj.addProperty("load_number", mLoad["LoadNumber"])
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("load_date", Utils.getCurrentDate())
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            if (mLoad["Type"] == "CarryOver Load") {
                jObj.addProperty("load_type", "2")
            } else {
                jObj.addProperty("load_type", "1")
            }
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getTrackForPost(): JsonObject? {

        val jObj = JsonObject()

        try {
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("date", Utils.getCurrentDate())
            jObj.addProperty("time", Utils.get24CurrentTime())
            jObj.addProperty("latitude", AppConstants.Latitude)
            jObj.addProperty("longitude", AppConstants.Longitude)
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())

        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getOrderForPost(): JsonObject? {

        val mOrder: Order = db!!.getPostOrderDetail()

        val jObj = JsonObject()

        val jsonArray = db!!.getPostOrderItem(mOrder.orderNo!!)

        try {
            jObj.addProperty("order_type_id", mOrder.orderType)
            jObj.addProperty("customer_id", mOrder.customerId)
            jObj.addProperty("depot_id", "")
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("order_number", mOrder.orderNo)
            jObj.addProperty("customer_address_id", mOrder.addressId)
            jObj.addProperty("transaction_type", "Payment on Delivery")
            jObj.addProperty("delivery_date", mOrder.deliveryDate)
            jObj.addProperty("payment_term_id", mOrder.payemntId)
            jObj.addProperty("due_date", mOrder.dueDate)
            jObj.addProperty("total_qty", 0)
            jObj.addProperty("total_gross", mOrder.gross)
            jObj.addProperty("total_discount_amount", mOrder.discount)
            jObj.addProperty("total_net", mOrder.net)
            jObj.addProperty("total_vat", mOrder.vat)
            jObj.addProperty("total_excise", mOrder.excAmt)
            jObj.addProperty("grand_total", mOrder.total)
            jObj.addProperty("any_comment", mOrder.comment)
            jObj.addProperty("lob_id", mOrder.lob_id)
            jObj.addProperty("customer_lop", mOrder.lpoNo)
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getLoadOrderForPost(): JsonObject? {

        val mOrder: Order = db!!.getPostLoadOrderDetail()

        val jObj = JsonObject()

        val jsonArray = db!!.getPostLoadOrderItem(mOrder.orderNo!!)

        try {
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("load_number", mOrder.orderNo)
            jObj.addProperty("load_date", mOrder.deliveryDate)
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("load_type", "Van Load")
            jObj.addProperty("status", "pending")
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)

        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getInvoiceSTampForPost(): JsonObject? {

        val mInvoice: Invoice = db!!.getPostStampInvoiceDetail()

        val jObj = JsonObject()


        try {
            jObj.addProperty("invoice_id", mInvoice.invoiceNo)
            jObj.addProperty("stamp", mInvoice.invoiceDate)
            if (mInvoice.orderType!!.isNotEmpty()) {
                var mBase = "data:image/jpeg;base64," + Utils.encoderBase64(mInvoice.orderType!!)
                jObj.addProperty("stamp_image", mBase)
            }

        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getCardFailPost(): JsonObject? {

        val mInvoice: CardFail = db!!.getPostCardFailDetail()

        val jObj = JsonObject()

        try {
            jObj.addProperty("date", Utils.getCurrentDate())
            jObj.addProperty("customer_id", mInvoice.customer_id!!)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("amount", mInvoice.amount)
            jObj.addProperty("transaction_id", mInvoice.transactionId)
            jObj.addProperty("auth_code", "")

        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getInvoiceForPost(): JsonObject? {

        val mInvoice: Invoice = db!!.getPostInvoiceDetail()

        val jObj = JsonObject()

        var deliveryNo = ""
        if (mInvoice.deliveryId!!.isEmpty()) {
            deliveryNo = ""
        } else {
            deliveryNo = db!!.getDelvieryNo(mInvoice.deliveryId!!)
        }

        //val mBatch = db!!.getAllInvoiceBatchItemByID(mInvoice.invoiceNo!!)
        val jsonArray = db!!.getPostInvoiceItem(mInvoice.invoiceNo!!, deliveryNo)

        var fromRange = ""
        var toRange = ""

        if (mInvoice.isCoupon == "1") {
            if (jsonArray.size() > 0) {
                for (mData in jsonArray) {
                    var mObj = mData.asJsonObject
                    fromRange = mObj.get("promotion_id").toString().replace("\\\"", "")
                    toRange = mObj.get("discount_id").toString().replace("\\\"", "")
                    break
                }
            }
        }


        try {
            jObj.addProperty("order_id", mInvoice.orderId)
            jObj.addProperty("customer_id", mInvoice.customerId)
            if (mInvoice.isExchange == "1") {
                jObj.addProperty("order_type_id", "1")
            } else {
                jObj.addProperty("order_type_id", mInvoice.orderType)
            }
            jObj.addProperty("delivery_id", mInvoice.deliveryId)
            jObj.addProperty("deliver_address_id", "")
            jObj.addProperty("invoice_number", mInvoice.invoiceNo)
            if (mInvoice.deliveryId!!.isEmpty()) {
                jObj.addProperty("invoice_type", "1")
            } else {
                jObj.addProperty("invoice_type", "2")
            }
            jObj.addProperty("payment_term_id", mInvoice.payemntId)
            jObj.addProperty("depot_id", "")
            jObj.addProperty("any_comment", mInvoice.comment)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("invoice_date", mInvoice.invoiceDate)
            jObj.addProperty("invoice_due_date", mInvoice.dueDate)
            jObj.addProperty("stamp", "0")
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("total_gross", mInvoice.gross)
            jObj.addProperty("total_vat", mInvoice.vat)
            jObj.addProperty("total_excise", mInvoice.excAmt)
            jObj.addProperty("total_net", mInvoice.net)
            jObj.addProperty("total_discount_amount", mInvoice.discount)
            jObj.addProperty("source", 1)
            jObj.addProperty("status", 1)
            jObj.addProperty("total_qty", 0)
            jObj.addProperty("delivery_charge", mInvoice.mDelvieryCharge)
            jObj.addProperty("grand_total", mInvoice.total)
            jObj.addProperty("is_topup", mInvoice.isTopup)
            jObj.addProperty("is_coupon", mInvoice.isCoupon)
            jObj.addProperty("rewards", mInvoice.mRewards)
            jObj.addProperty("latitude", mInvoice.latitude)
            jObj.addProperty("longitude", mInvoice.longitude)
            jObj.addProperty("punch_time", mInvoice.punchTime)
            jObj.addProperty("payment_type", mInvoice.pType)
            if (mInvoice.isCoupon == "1") {
                jObj.addProperty(
                    "coupon_range_from",
                    fromRange
                )
                jObj.addProperty(
                    "coupon_range_to",
                    toRange
                )
            } else {
                jObj.addProperty("coupon_range_from", "")
                jObj.addProperty("coupon_range_to", "")
            }
            jObj.addProperty("is_coupon_sale", mInvoice.isCouponSale)
            jObj.addProperty("coupon_id", mInvoice.couponId)
            jObj.addProperty("lob_id", mInvoice.lobId)
            jObj.addProperty("customer_lpo", mInvoice.lpoNo)
            jObj.addProperty("is_exchange", mInvoice.isExchange)
            jObj.addProperty("exchange_number", mInvoice.exchangeNo)
            jObj.addProperty("pending_credit", mInvoice.paidAmount)
            jObj.addProperty("rounding_off_amount", mInvoice.roundTo)

            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getDeliveryForPost(): JsonObject? {

        var jObj = JsonObject()

        var mMapData = db!!.getPostDelvieryUUID()

        try {
            jObj.addProperty("uuId", mMapData["uuId"])
            jObj.addProperty("delivery_id", mMapData["delivery_id"])
            jObj.addProperty("cancel_comment", mMapData["cancel_comment"])
            jObj.addProperty("delviery_number", mMapData["delviery_number"])
            if (mMapData["image"]!!.isNotEmpty()) {
                //Log.e("File Post Path",mMapData["image"]!!)
                var mBase = "data:image/jpeg;base64," + Utils.encoderBase64(mMapData["image"]!!)
                jObj.addProperty("image", mBase)
            } else {
                jObj.addProperty("image", "")
            }

        } catch (e: JsonIOException) {
            e.printStackTrace()
        }


        return jObj
    }

    private fun getCancelInvoiceForPost(): JsonObject? {

        var jObj = JsonObject()

        jObj = db!!.getPostInvoiceUUID()

        return jObj
    }


    private fun getReturnForPost(): JsonObject? {

        val mOrder: Order = db!!.getPostReturnDetail()

        val jObj = JsonObject()

        //val mBatch = db!!.getCustomerReturnBatchList(mOrder.orderNo!!)
        val jsonArray = db!!.getPostReturnItem(mOrder.orderNo!!)

        try {
            jObj.add("items", jsonArray)
            jObj.addProperty("invoice_id", mOrder.invoiceId)
            jObj.addProperty("customer_id", mOrder.customerId)
            jObj.addProperty("return_type", mOrder.orderType)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("credit_note_number", mOrder.orderNo)
            jObj.addProperty("credit_note_date", mOrder.orderDate)
            jObj.addProperty("payment_term_id", mOrder.payemntId)
            jObj.addProperty("reason", mOrder.reason)
            jObj.addProperty("total_qty", 0)
            jObj.addProperty("total_gross", mOrder.gross)
            jObj.addProperty("total_discount_amount", mOrder.discount)
            jObj.addProperty("total_net", mOrder.net)
            jObj.addProperty("total_vat", mOrder.vat)
            jObj.addProperty("total_excise", "0")
            jObj.addProperty("grand_total", mOrder.total)
            jObj.addProperty("any_comment", mOrder.comment)
            jObj.addProperty("lob_id", mOrder.lob_id)
            jObj.addProperty("is_exchange", mOrder.isExchange)

            if (mOrder.isExchange == "1") {
                jObj.addProperty("order_type_id", "1")
            } else {
                if (mOrder.payemntId != null) {
                    if (mOrder.payemntId == "7") {
                        jObj.addProperty("order_type_id", "1")
                    } else {
                        jObj.addProperty("order_type_id", "2")
                    }
                } else {
                    jObj.addProperty("order_type_id", "2")
                }
            }
            jObj.addProperty("is_requested", mOrder.isRequested)
            jObj.addProperty("exchange_number", mOrder.exchangeNo)
            jObj.addProperty("source", 1)
            jObj.addProperty("image", mOrder.returnImage)
            jObj.addProperty("image2", mOrder.returnImage2)
            jObj.addProperty("image3", mOrder.returnImage3)

        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getReturnRequestForPost(): JsonObject? {

        val mOrder = db!!.getPostReturnCompletedDetail()

        val jObj = JsonObject()

        //val mBatch = db!!.getCustomerReturnBatchList(mOrder.orderNo!!)
        var mUUID = db!!.getReturnUUID(mOrder)
        val jsonArray = db!!.getPostReturnUpdateItem(mOrder)
        jObj.add("items", jsonArray)
        jObj.addProperty("credit_note_no", mOrder)
        jObj.addProperty("uuid", mUUID)
        return jObj
    }

    private fun getReturnCancelRequestForPost(): JsonObject? {

        val mOrder = db!!.getPostReturnCancelDetail()

        val jObj = JsonObject()

        //val mBatch = db!!.getCustomerReturnBatchList(mOrder.orderNo!!)
        var mUUID = db!!.getReturnUUID(mOrder)
        jObj.addProperty("credit_note_no", mOrder)
        jObj.addProperty("uuid", mUUID)
        return jObj
    }

    private fun getReturnPalletRequestForPost(): JsonObject? {

        val mOrder = db!!.getPostPALLEtVarianceDetail()

        val jObj = JsonObject()

        //val mBatch = db!!.getCustomerReturnBatchList(mOrder.orderNo!!)
        val jsonArray = db!!.getPostPalletReturnItem(mOrder.orderNo!!)
        jObj.addProperty("customer_id", mOrder.customerId)
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
        jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
        jObj.addProperty("date", Utils.getCurrentDate())
        jObj.addProperty("return_number", mOrder.orderNo)
        jObj.add("items", jsonArray)
        return jObj
    }

    private fun getLoadVarianceRequestForPost(): JsonObject? {

        val mOrder: Order = db!!.getPostLoadVarianceDetail()

        val jObj = JsonObject()

        val jsonArray = db!!.getPostLoadVarianceItem(mOrder.orderNo!!)

        try {
            jObj.addProperty("invoice_id", "")
            jObj.addProperty("customer_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("debit_note_number", mOrder.orderNo)
            jObj.addProperty("debit_note_date", Utils.getCurrentDate())
            jObj.addProperty("payment_term_id", "")
            jObj.addProperty("reason", mOrder.reason)
            jObj.addProperty("total_qty", 0)
            jObj.addProperty("total_gross", mOrder.gross)
            jObj.addProperty("total_discount_amount", mOrder.discount)
            jObj.addProperty("total_net", mOrder.net)
            jObj.addProperty("total_vat", mOrder.vat)
            jObj.addProperty("total_excise", "0")
            jObj.addProperty("grand_total", mOrder.total)
            jObj.addProperty("debit_note_comment", "Variance")
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getUnloadForPost(): JsonObject? {

        val jObj = JsonObject()

        val mUnloadNumber = db!!.getPostUnloadHeaderDetail()

        val jsonArray =
            db!!.getPostUnloadItem(mUnloadNumber["UnloadNo"]!!, mUnloadNumber["UnloadDate"]!!)

        try {
            jObj.addProperty("trip_id", mUnloadNumber["TripId"])
            jObj.addProperty("unload_type", mUnloadNumber["unload_type"])
            jObj.addProperty("code", mUnloadNumber["UnloadNo"])
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("transaction_date", mUnloadNumber["UnloadDate"])
            jObj.addProperty("status", "0")
            jObj.addProperty("is_damaged", "0")
            var mCount = db!!.getPostUnloadItemIsFresh(mUnloadNumber["UnloadNo"]!!)
            if (mCount > 0) {
                jObj.addProperty("is_fresh", "0")
            } else {
                jObj.addProperty("is_fresh", "1")
            }
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }


    private fun getUnloadForAuditPost(): JsonObject? {

        val jObj = JsonObject()

        val mUnloadNumber = db!!.getPostUnloadAuditHeaderDetail()

        val jsonArray =
            db!!.getPostUnloadItem(mUnloadNumber["UnloadNo"]!!, mUnloadNumber["UnloadDate"]!!)

        try {
            jObj.addProperty("trip_id", mUnloadNumber["TripId"])
            jObj.addProperty("unload_type", mUnloadNumber["unload_type"])
            jObj.addProperty("code", mUnloadNumber["UnloadNo"])
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("transaction_date", mUnloadNumber["UnloadDate"])
            jObj.addProperty("status", "0")
            jObj.addProperty("is_damaged", "0")
            var mCount = db!!.getPostUnloadItemIsFresh(mUnloadNumber["UnloadNo"]!!)
            if (mCount > 0) {
                jObj.addProperty("is_fresh", "0")
            } else {
                jObj.addProperty("is_fresh", "1")
            }
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getUnloadForBadVarPost(): JsonObject? {

        val jObj = JsonObject()

        val mUnloadNumber = db!!.getPostUnBadVarDetail()

        val jsonArray =
            db!!.getPostUnloadItem(mUnloadNumber["UnloadNo"]!!, mUnloadNumber["UnloadDate"]!!)

        try {
            jObj.addProperty("trip_id", mUnloadNumber["TripId"])
            jObj.addProperty("unload_type", mUnloadNumber["unload_type"])
            jObj.addProperty("code", mUnloadNumber["UnloadNo"])
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("transaction_date", mUnloadNumber["UnloadDate"])
            jObj.addProperty("status", "0")
            jObj.addProperty("is_damaged", "0")
            var mCount = db!!.getPostUnloadItemIsFresh(mUnloadNumber["UnloadNo"]!!)
            if (mCount > 0) {
                jObj.addProperty("is_fresh", "0")
            } else {
                jObj.addProperty("is_fresh", "1")
            }
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getUnloadForAuditVarPost(): JsonObject? {

        val jObj = JsonObject()

        val mUnloadNumber = db!!.getPostUnloadAuditVarDetail()

        val jsonArray =
            db!!.getPostUnloadItem(mUnloadNumber["UnloadNo"]!!, mUnloadNumber["UnloadDate"]!!)

        try {
            jObj.addProperty("trip_id", mUnloadNumber["TripId"])
            jObj.addProperty("unload_type", mUnloadNumber["unload_type"])
            jObj.addProperty("code", mUnloadNumber["UnloadNo"])
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("transaction_date", mUnloadNumber["UnloadDate"])
            jObj.addProperty("status", "0")
            jObj.addProperty("is_damaged", "0")
            var mCount = db!!.getPostUnloadItemIsFresh(mUnloadNumber["UnloadNo"]!!)
            if (mCount > 0) {
                jObj.addProperty("is_fresh", "0")
            } else {
                jObj.addProperty("is_fresh", "1")
            }
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }


    private fun getUnloadLoadVariForPost(): JsonObject? {

        val jObj = JsonObject()

        val mUnloadNumber = db!!.getPostUnloadLoadVaHeaderDetail()

        val jsonArray =
            db!!.getPostUnloadItem(mUnloadNumber["UnloadNo"]!!, mUnloadNumber["UnloadDate"]!!)

        try {
            jObj.addProperty("trip_id", mUnloadNumber["TripId"])
            jObj.addProperty("unload_type", mUnloadNumber["unload_type"])
            jObj.addProperty("code", mUnloadNumber["UnloadNo"])
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("transaction_date", mUnloadNumber["UnloadDate"])
            jObj.addProperty("status", "0")
            jObj.addProperty("is_damaged", "0")
            var mCount = db!!.getPostUnloadItemIsFresh(mUnloadNumber["UnloadNo"]!!)
            if (mCount > 0) {
                jObj.addProperty("is_fresh", "0")
            } else {
                jObj.addProperty("is_fresh", "1")
            }
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }


    private fun getUnloadVanReturnPost(): JsonObject? {

        val jObj = JsonObject()

        val mUnloadNumber = db!!.getPostUnloadBadHeaderDetail()

        val jsonArray =
            db!!.getPostUnloadItem(mUnloadNumber["UnloadNo"]!!, mUnloadNumber["UnloadDate"]!!)

        try {
            jObj.addProperty("trip_id", mUnloadNumber["TripId"])
            jObj.addProperty("unload_type", mUnloadNumber["unload_type"])
            jObj.addProperty("code", mUnloadNumber["UnloadNo"])
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("transaction_date", mUnloadNumber["UnloadDate"])
            jObj.addProperty("status", "0")
            jObj.addProperty("is_damaged", mUnloadNumber["is_damaged"])
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getUnloadVarieancePost(): JsonObject? {

        val jObj = JsonObject()

        val mUnloadNumber = db!!.getPostUnloadVarieanceHeaderDetail()

        val jsonArray =
            db!!.getPostUnloadItem(mUnloadNumber["UnloadNo"]!!, mUnloadNumber["UnloadDate"]!!)
        // System.out.println("PPP--> "+mUnloadNumber["UnloadNo"]!!+ " "+mUnloadNumber)

        try {
            jObj.addProperty("trip_id", mUnloadNumber["TripId"])
            jObj.addProperty("unload_type", mUnloadNumber["unload_type"])
            jObj.addProperty("code", mUnloadNumber["UnloadNo"])
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("transaction_date", mUnloadNumber["UnloadDate"])
            jObj.addProperty("status", "0")
            jObj.addProperty("is_damaged", "0")
            jObj.addProperty("source", 1)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }


    private fun getComplainForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostComplainItem()

        val arrImages = JsonArray()
        if (mComplain.images != null) {
            if (mComplain.images!!.isNotEmpty()) {
                var splitImage = mComplain.images!!.split(",")
                for (mPath in splitImage) {
                    var mBase = Utils.encoder(mPath)
                    arrImages.add(mBase)
                }
            }
        }

        try {
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("complaint_id", mComplain.complaintId)
            jObj.addProperty("customer_id", mComplain.customerId)
            jObj.addProperty("type", "complaint")
            jObj.addProperty("title", mComplain.title)
            jObj.addProperty("item_id", mComplain.item)
            jObj.addProperty("description", mComplain.description)
            jObj.add("images", arrImages)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getSuggestionForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostSuggestionItem()

        val arrImages = JsonArray()
        if (mComplain.images != null) {
            if (mComplain.images!!.isNotEmpty()) {
                var splitImage = mComplain.images!!.split(",")
                for (mPath in splitImage) {
                    var mBase = Utils.encoder(mPath)
                    arrImages.add(mBase)
                }
            }
        }

        try {
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("complaint_id", mComplain.complaintId)
            jObj.addProperty("customer_id", mComplain.customerId)
            jObj.addProperty("type", "suggestion")
            jObj.addProperty("title", mComplain.title)
            jObj.addProperty("item_id", mComplain.item)
            jObj.addProperty("description", mComplain.description)
            jObj.add("images", arrImages)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getCampaignForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostCampaignItem()

        val arrImages = JsonArray()
        if (mComplain.images != null) {
            if (mComplain.images!!.isNotEmpty()) {
                var splitImage = mComplain.images!!.split(",")
                for (mPath in splitImage) {
                    var mBase = Utils.encoder(mPath)
                    arrImages.add(mBase)
                }
            }
        }

        try {
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("campaign_id", mComplain.compaignId)
            jObj.addProperty("customer_id", mComplain.customerId)
            jObj.addProperty("feedback", mComplain.feedback)
            jObj.add("images", arrImages)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getCompetitorForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostCompetitorItem()

        val arrImages = JsonArray()
        if (mComplain.images != null) {
            if (mComplain.images!!.isNotEmpty()) {
                var splitImage = mComplain.images!!.split(",")
                for (mPath in splitImage) {
                    var mBase = Utils.encoder(mPath)
                    arrImages.add(mBase)
                }
            }
        }

        try {
            jObj.addProperty("competitor_id", mComplain.complaintId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("company", mComplain.company)
            jObj.addProperty("brand", mComplain.brand)
            jObj.addProperty("item", mComplain.item)
            jObj.addProperty("price", mComplain.price)
            jObj.addProperty("note", mComplain.note)
            jObj.add("competitor_info_images", arrImages)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getInventoryForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostInventoryDetail()

        val jsonArray = db!!.getPostInventoryItem(mComplain.uniqueId!!)

        try {
            jObj.addProperty("unique_id", mComplain.uniqueId)
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("assign_inventory_id", mComplain.inventoryId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("customer_id", mComplain.customerId)
            //jObj.addProperty("expiry_date", jsonArray[0].asJsonObject["expiry_date"].toString())
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getDistributionForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostDistributionDetail()

        val jsonArray = db!!.getPostDistributionItem(mComplain.uniqueId!!)

        try {
            jObj.addProperty("unique_id", mComplain.uniqueId)
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("distribution_id", mComplain.distributionId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("customer_id", mComplain.customer)
            jObj.add("items", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getDistributionImageForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostDistributionImageItem()

        try {
            jObj.addProperty("unique_id", mComplain.uniqueId)
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("distribution_id", mComplain.distributionId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("customer_id", mComplain.customer)
            if (mComplain.images != null) {
                if (mComplain.images!!.isNotEmpty()) {
                    var splitImage = mComplain.images!!.split(",")
                    var i = 1
                    for (mPath in splitImage) {
                        var mBase = Utils.encoder(mPath)
                        if (i == 1) {
                            jObj.addProperty("image1", mBase)
                        } else if (i == 2) {
                            jObj.addProperty("image2", mBase)
                        } else if (i == 3) {
                            jObj.addProperty("image3", mBase)
                        } else if (i == 4) {
                            jObj.addProperty("image4", mBase)
                        }
                        i++
                    }
                }
            }
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getPlanogramForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostPlanogramDetail()

        val arrImages = JsonArray()
        if (mComplain.beforeImage!!.isNotEmpty()) {
            var mBase = Utils.encoder(mComplain.beforeImage!!)
            arrImages.add(mBase)
        }
        if (mComplain.afterImage!!.isNotEmpty()) {
            var mBase = Utils.encoder(mComplain.afterImage!!)
            arrImages.add(mBase)
        }

        try {
            jObj.addProperty("unique_id", mComplain.uniqueId)
            jObj.addProperty("distribution_id", mComplain.distributionId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("customer_id", mComplain.customer)
            jObj.addProperty("planogram_id", mComplain.planogramId)
            jObj.addProperty("status", "1")
            jObj.add("images", arrImages)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getAssetsForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostAssetsItem()

        val arrImages = JsonArray()
        if (mComplain.postImages != null) {
            if (mComplain.postImages!!.isNotEmpty()) {
                var splitImage = mComplain.postImages!!.split(",")
                for (mPath in splitImage) {
                    var mBase = Utils.encoder(mPath)
                    arrImages.add(mBase)
                }
            }
        }

        try {
            jObj.addProperty("unique_id", mComplain.uniqueId)
            jObj.addProperty("asset_tracking_id", mComplain.assetsId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            // jObj.addProperty("customer_id", mComplain.customer)
            jObj.addProperty("feedback", mComplain.feedBack)
            jObj.add("images", arrImages)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getSurveyForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostSurveyItem()

        val jsonArray = db!!.getPostSurveyQuestion(mComplain.uniqueId!!)

        try {
            jObj.addProperty("unique_id", mComplain.uniqueId)
            jObj.addProperty("survey_id", mComplain.surveyId)
            jObj.addProperty("survey_type_id", mComplain.surveyTypeId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("customer_id", mComplain.customerId)
            jObj.addProperty("customer_name", mComplain.customerName)
            jObj.addProperty("email", mComplain.email)
            jObj.addProperty("phone", mComplain.phone)
            jObj.add("questions", jsonArray)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getpromotionalForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostPromotionalItem()

        var mBase = ""
        if (mComplain.invoiceImage != null) {
            if (mComplain.invoiceImage!!.isNotEmpty()) {
                var mBase = Utils.encoder(mComplain.invoiceImage!!)
            }
        }

        try {
            jObj.addProperty("unique_id", mComplain.promotionId)
            jObj.addProperty("promotional_id", mComplain.promotionAcountId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("cusotmer", mComplain.customerName)
            jObj.addProperty("invoice_code", mComplain.invoiceNo)
            jObj.addProperty("phone", mComplain.customerPhone)
            jObj.addProperty("amount_spend", mComplain.amount)
            jObj.addProperty("image", mBase)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getVisitForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostVisit()

        try {
            jObj.addProperty("visit_id", mComplain.visitedOffId)
            jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("customer_id", mComplain.customerId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            jObj.addProperty("latitude", mComplain.latitude)
            jObj.addProperty("longitude", mComplain.longitude)
            jObj.addProperty("shop_status", mComplain.status)
            jObj.addProperty("journey_plan_id", mComplain.planId)
            jObj.addProperty("start_time", mComplain.startTime)
            jObj.addProperty("end_time", mComplain.endTime)

            // jObj.addProperty("total_task", "6")
            // jObj.addProperty("completed_task", mComplain.completedTask)
            // jObj.addProperty("reason", mComplain.reason)
            //jObj.addProperty("comment ", mComplain.comment)
            jObj.addProperty("is_sequnece", mComplain.isSequence)
            jObj.addProperty("date", Utils.getCurrentDate())
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getVisitActivityForPost(): JsonObject? {

        val jObj = JsonObject()

        val mComplain = db!!.getPostVisitActivity()

        var arrActivity = JsonArray()
        arrActivity = db!!.getPostActivityList(mComplain.visitedId!!)

        try {
            jObj.addProperty("customer_visit_id", mComplain.visitedId)
            jObj.addProperty("customer_id", mComplain.customerId)
            jObj.add("activity", arrActivity)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun getCollectionForPost(mCollectionDetail: Collection): JsonObject? {

        // var mCollectionDetail = db!!.getCollectionHeaderDetail(mCollData.invoice_date!!)

        // var arrColl = db!!.getCollectionDetail(mCollData.invoice_date!!)

        var arrColl = db!!.getCollectionHDDetail(mCollectionDetail.collectionNo.toString())

        val jObj = JsonObject()

        try {
            jObj.addProperty("customer_id", mCollectionDetail.customerId)
            jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
            if (mCollectionDetail.collectionType!! == "3") {
                jObj.addProperty("collection_type", "4")
                jObj.addProperty("transaction_number", mCollectionDetail.rebateVatAmt)
            } else {
                jObj.addProperty("collection_type", mCollectionDetail.collectionType)
                jObj.addProperty("transaction_number", "")
            }

            jObj.addProperty("collection_number", mCollectionDetail.collectionNo)
            jObj.addProperty("payemnt_type", mCollectionDetail.paymentType)
            jObj.addProperty("payment_reference", mCollectionDetail.prepaidRef)
            jObj.addProperty("tra_mpos_id", mCollectionDetail.transactionId)
            jObj.addProperty("lob_id", mCollectionDetail.lobId)
            jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
            jObj.addProperty("discount", mCollectionDetail.discountAmt)
            jObj.addProperty("cash_type", mCollectionDetail.cashType)
            jObj.addProperty("allocate_amount", mCollectionDetail.allocatAmt)
            jObj.addProperty("shelf_rent", mCollectionDetail.shelfAmt)
            jObj.addProperty("rebate_amount", mCollectionDetail.rebateAmt)
            jObj.addProperty("rebate_vat_value", "0")
            jObj.addProperty("is_rebateVat", mCollectionDetail.isRebateVat)
            jObj.addProperty("collection_date", Utils.getCurrentDate())
            jObj.addProperty("is_partial", mCollectionDetail.isPartial)

            var isCouponSale = "0"
            var isCouponRead = "0"
            val list = JsonArray()
            for (mData in arrColl) {
                val mItem = JsonObject()
                mItem.addProperty(
                    "invoice_id",
                    mData.invId
                )
                mItem.addProperty(
                    "invoice_no",
                    mData.invNo
                )

                isCouponSale = db!!.geCouponSalesInvoice(mData.invId!!)

                mItem.addProperty(
                    "amount",
                    mData.paidAmt
                )
                mItem.addProperty(
                    "customer_id",
                    mData.customerId
                )

                if (mData.type == "Invoice") {
                    mItem.addProperty(
                        "type",
                        "1"
                    )
                } else if (mData.type == "Credit") {
                    mItem.addProperty(
                        "type",
                        "3"
                    )
                } else {
                    mItem.addProperty(
                        "type",
                        "2"
                    )
                }

                list.add(mItem)
            }

            jObj.add("items", list)
            jObj.addProperty("is_coupon", isCouponRead)
            jObj.addProperty("is_couponSales", isCouponSale)
            jObj.addProperty("invoice_amount", mCollectionDetail.paidAmt)
            jObj.addProperty("cheque_number", mCollectionDetail.chequeNo)
            jObj.addProperty("cheque_date", mCollectionDetail.chequeDate)
            jObj.addProperty("bank_info", mCollectionDetail.bank)
            jObj.addProperty("collection_note", mCollectionDetail.comment)
            jObj.addProperty("bank_charge", mCollectionDetail.bankCharge)
            jObj.addProperty("vat_amount", mCollectionDetail.taxamount)
            jObj.addProperty("card_type", mCollectionDetail.cardType)
            jObj.addProperty("card_charge", mCollectionDetail.cardCharge)
            jObj.addProperty("card_source", "NFC")
            // jObj.addProperty("card_source", "MPOS")
            //jObj.addProperty("chequeme_image", mCollectionDetail.chequeImage2)
            jObj.addProperty("source", 1)
            jObj.addProperty("on_account", mCollectionDetail.onAccountEnable)
            if (mCollectionDetail.isTopup == "1") {
                jObj.addProperty("topup_value", mCollectionDetail.paidAmt)
                jObj.addProperty("bonus_value", mCollectionDetail.mBonous)
                jObj.addProperty("source_type", "iCash")
                jObj.addProperty("is_topup", "Yes")
            } else {
                jObj.addProperty("topup_value", "0")
                jObj.addProperty("bonus_value", "0")
                jObj.addProperty("source_type", mCollectionDetail.source)
                jObj.addProperty("is_topup", "No")
            }

            if (mCollectionDetail.chequeImage!!.isNotEmpty()) {
                var mBase = Utils.encoder(mCollectionDetail.chequeImage!!)
                jObj.addProperty("image", mBase)
            } else {
                jObj.addProperty("image", "")
            }

            if (mCollectionDetail.chequeImage2!!.isNotEmpty()) {
                var mBase = Utils.encoder(mCollectionDetail.chequeImage2!!)
                jObj.addProperty("chequeme_image", mBase)
            } else {
                jObj.addProperty("chequeme_image", "")
            }


        } catch (e: JsonIOException) {
            e.printStackTrace()
        }
        return jObj
    }

    private fun callLoadConfirmAPI(jObj: JsonObject) {
        Log.e("Load Confrim", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Load Confirm Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postLoadConfirm(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Load Confirm Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {
                                db!!.updateLoadTransaction(
                                    jObj["load_number"].asString, "1", ""
                                )
                                db!!.updateLoadPost(
                                    jObj["load_number"].asString, "1"
                                )
                            } else {
                                // Fail to Post
                                db!!.updateLoadPost(jObj["load_number"].asString, "2")
                                db!!.updateLoadTransaction(
                                    jObj["load_number"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        // Fail to Post
                        db!!.updateLoadPost(jObj["load_number"].asString, "2")
                        db!!.updateLoadTransaction(
                            jObj["load_number"].asString,
                            "2",
                            "Response Blank"
                        )
                        Utils.logData(
                            this@SyncData,
                            "Load Confirm Response fail ${response.errorBody()!!.toString()}"
                        )

                    }
                    AppConstants.isLoadSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateLoadPost(jObj["load_number"].asString, "2")
                    db!!.updateLoadTransaction(
                        jObj["load_number"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isLoadSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Load Confirm Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Load Confirm API Fail"
                        )

                    }
                }
            })
    }

    private fun callTrackAPI(jObj: JsonObject) {
        Log.e("Tracking Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Tracking Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postTrack(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    if (response.isSuccessful) {
                        Utils.logData(
                            this@SyncData,
                            "Tracking Response ${response.body()!!.toString()}"
                        )
                    } else {

                        if (response.errorBody() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Tracking Response fail ${response.errorBody()!!.toString()}"
                            )

                        }

                    }
                    AppConstants.isTrackSync = false
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post

                    AppConstants.isTrackSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Tracking Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Tracking API Fail"
                        )

                    }
                }
            })
    }

    private fun callOrderAPI(jObj: JsonObject) {
        Log.e("Order Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Order Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postOrder(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    if (response.isSuccessful) {
                        Utils.logData(
                            this@SyncData,
                            "Order Response ${response.body()!!.toString()}"
                        )
                        if (response.body() != null) {
                            if (response.body()!!["status"].asBoolean) {
                                db!!.updateOrderTransaction(
                                    jObj["order_number"].asString, "1", ""
                                )
                                db!!.updateOrderPost(
                                    jObj["order_number"].asString, "1"
                                )
                            } else {
                                // Fail to Post
                                db!!.updateOrderPost(jObj["order_number"].asString, "2")
                                db!!.updateOrderTransaction(
                                    jObj["order_number"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {

                        if (response.errorBody() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Order Response fail ${response.errorBody()!!.toString()}"
                            )

                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateOrderPost(jObj["order_number"].asString, "2")
                                db!!.updateOrderTransaction(
                                    jObj["order_number"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )
                            } else {
                                // Fail to Post
                                db!!.updateOrderPost(jObj["order_number"].asString, "2")
                                db!!.updateOrderTransaction(
                                    jObj["order_number"].asString,
                                    "2",
                                    errorResponse!!.message
                                )
                            }
                        } else {
                            // Fail to Post
                            db!!.updateOrderPost(jObj["order_number"].asString, "2")
                            db!!.updateOrderTransaction(
                                jObj["order_number"].asString,
                                "2",
                                "Error while validating"
                            )
                        }

                    }
                    AppConstants.isOrderSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateOrderPost(jObj["order_number"].asString, "2")
                    db!!.updateOrderTransaction(
                        jObj["order_number"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isOrderSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Order Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Order API Fail"
                        )

                    }
                }
            })
    }

    private fun callLoadOrderAPI(jObj: JsonObject) {
        Log.e("Load order Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Load Order Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postLoadRequest(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {


                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "LoadOrder Response  ${response.body()!!.toString()}"
                            )
                            if (response.body()!!["status"].asBoolean) {
                                db!!.updateOrderTransaction(
                                    jObj["load_number"].asString, "1", ""
                                )
                                db!!.updateLoadRequestPost(
                                    jObj["load_number"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateLoadRequestPost(jObj["load_number"].asString, "2")
                                db!!.updateOrderTransaction(
                                    jObj["load_number"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        if (response.code() == 500) {
                            Utils.logData(
                                this@SyncData,
                                "LoadOrder Response  500 API Error"
                            )
                            db!!.updateLoadRequestPost(jObj["load_number"].asString, "2")
                            db!!.updateOrderTransaction(
                                jObj["load_number"].asString,
                                "2",
                                "500 API Error"
                            )
                        } else {
                            if (response.errorBody() != null) {
                                Utils.logData(
                                    this@SyncData,
                                    "LoadOrder Response fail ${response.errorBody()!!.toString()}"
                                )

                                val gson = Gson()
                                val type = object : TypeToken<MyErrorMessage>() {}.type
                                var errorResponse: MyErrorMessage? =
                                    gson.fromJson(response.errorBody()!!.charStream(), type)

                                if (errorResponse!!.errors != null) {
                                    // Fail to Post
                                    db!!.updateLoadRequestPost(jObj["load_number"].asString, "2")
                                    db!!.updateOrderTransaction(
                                        jObj["load_number"].asString,
                                        "2",
                                        errorResponse!!.errors!!.error
                                    )
                                } else {
                                    // Fail to Post
                                    db!!.updateLoadRequestPost(jObj["load_number"].asString, "2")
                                    db!!.updateOrderTransaction(
                                        jObj["load_number"].asString,
                                        "2",
                                        errorResponse!!.message
                                    )
                                }
                            } else {
                                // Fail to Post
                                db!!.updateLoadRequestPost(jObj["load_number"].asString, "2")
                                db!!.updateOrderTransaction(
                                    jObj["load_number"].asString,
                                    "2",
                                    "Error for validating"
                                )
                            }
                        }
                    }
                    AppConstants.isLoadRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateLoadRequestPost(jObj["load_number"].asString, "2")
                    db!!.updateOrderTransaction(
                        jObj["load_number"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isLoadRequestSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "LoadOrder Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "LoadOrder API Fail"
                        )

                    }
                }
            })
    }

    private fun callInvoiceAPI(jObj: JsonObject) {
        Log.e("Invoice Body", jObj.toString())
//        Utils.logData(
//            this,
//            "Invoice Body$jObj"
//        )
        Utils.logData(
            this@SyncData,
            "Invoice Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postInvoice(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            Utils.logData(
                                this@SyncData,
                                "Invoice Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                var invId =
                                    response.body()!!["data"].asJsonObject!!["id"].toString()

                                db!!.updateInvoiceIdCollection(
                                    jObj["invoice_number"].asString,
                                    invId
                                )

                                db!!.updateInvoiceRequestIdPost(
                                    jObj["invoice_number"].asString,
                                    invId
                                )

                                db!!.updateInvoiceTransaction(
                                    jObj["invoice_number"].asString, "1", ""
                                )
                                db!!.updateInvoiceRequestPost(
                                    jObj["invoice_number"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateInvoiceRequestPost(jObj["invoice_number"].asString, "2")
                                db!!.updateInvoiceTransaction(
                                    jObj["invoice_number"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                val gson = Gson()
                                val type = object : TypeToken<MyErrorMessage>() {}.type
                                Utils.logData(
                                    this@SyncData,
                                    "Invoice Response fail ${response.errorBody()!!.toString()}"
                                )

                                if (type != null) {


                                    var errorResponse: MyErrorMessage? =
                                        gson.fromJson(response.errorBody()!!.charStream(), type)

                                    if (errorResponse!!.errors != null) {
                                        // Fail to Post
                                        db!!.updateInvoiceRequestPost(
                                            jObj["invoice_number"].asString,
                                            "2"
                                        )
                                        db!!.updateInvoiceTransaction(
                                            jObj["invoice_number"].asString,
                                            "2",
                                            errorResponse!!.errors!!.error
                                        )
                                    } else {
                                        // Fail to Post
                                        db!!.updateInvoiceRequestPost(
                                            jObj["invoice_number"].asString,
                                            "2"
                                        )
                                        db!!.updateInvoiceTransaction(
                                            jObj["invoice_number"].asString,
                                            "2",
                                            errorResponse!!.message
                                        )
                                    }
                                } else {
                                    db!!.updateInvoiceRequestPost(
                                        jObj["invoice_number"].asString,
                                        "2"
                                    )
                                    db!!.updateInvoiceTransaction(
                                        jObj["invoice_number"].asString,
                                        "2",
                                        "Error while validating"
                                    )
                                }
                            } else {
                                // Fail to Post
                                db!!.updateInvoiceRequestPost(jObj["invoice_number"].asString, "2")
                                db!!.updateInvoiceTransaction(
                                    jObj["invoice_number"].asString,
                                    "2",
                                    "Error while validating"
                                )
                            }
                        } catch (e: Exception) {
                            e.toString()
                        }

                    }
                    AppConstants.isInvoiceSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateInvoiceRequestPost(jObj["invoice_number"].asString, "2")
                    db!!.updateInvoiceTransaction(
                        jObj["invoice_number"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isInvoiceSync = false

                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Invoice Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Invoice API Fail"
                        )

                    }
                }
            })
    }

    private fun callCardFailAPI(jObj: JsonObject) {
        Log.e("CardFail Body", jObj.toString())
//        Utils.logData(
//            this,
//            "Invoice Body$jObj"
//        )
        Utils.logData(
            this@SyncData,
            "CardFail Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postCardFail(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            Utils.logData(
                                this@SyncData,
                                "CardFail Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateCardFailTransaction(
                                    jObj["transaction_id"].asString, "1", ""
                                )
                                db!!.updateCardFailRequestPost(
                                    jObj["transaction_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateCardFailRequestPost(jObj["transaction_id"].asString, "2")
                                db!!.updateCardFailTransaction(
                                    jObj["transaction_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                val gson = Gson()
                                val type = object : TypeToken<MyErrorMessage>() {}.type
                                Utils.logData(
                                    this@SyncData,
                                    "CardFail Response fail ${response.errorBody()!!.toString()}"
                                )

                                if (type != null) {


                                    var errorResponse: MyErrorMessage? =
                                        gson.fromJson(response.errorBody()!!.charStream(), type)

                                    if (errorResponse!!.errors != null) {
                                        // Fail to Post
                                        db!!.updateCardFailRequestPost(
                                            jObj["transaction_id"].asString,
                                            "2"
                                        )
                                        db!!.updateCardFailTransaction(
                                            jObj["transaction_id"].asString,
                                            "2",
                                            errorResponse!!.errors!!.error
                                        )
                                    } else {
                                        // Fail to Post
                                        db!!.updateCardFailRequestPost(
                                            jObj["transaction_id"].asString,
                                            "2"
                                        )
                                        db!!.updateCardFailTransaction(
                                            jObj["transaction_id"].asString,
                                            "2",
                                            errorResponse!!.message
                                        )
                                    }
                                } else {
                                    db!!.updateCardFailRequestPost(
                                        jObj["transaction_id"].asString,
                                        "2"
                                    )
                                    db!!.updateCardFailTransaction(
                                        jObj["transaction_id"].asString,
                                        "2",
                                        "Error while validating"
                                    )
                                }
                            } else {
                                // Fail to Post
                                db!!.updateCardFailRequestPost(jObj["transaction_id"].asString, "2")
                                db!!.updateCardFailTransaction(
                                    jObj["transaction_id"].asString,
                                    "2",
                                    "Error while validating"
                                )
                            }
                        } catch (e: Exception) {
                            e.toString()
                        }

                    }
                    AppConstants.isCardFailSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateCardFailRequestPost(jObj["transaction_id"].asString, "2")
                    db!!.updateCardFailTransaction(
                        jObj["transaction_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isCardFailSync = false

                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "CardFail Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "CardFail API Fail"
                        )

                    }
                }
            })
    }

    private fun callStampInvoiceAPI(jObj: JsonObject) {
        Log.e("InvoiceStamp Body", jObj.toString())
//        Utils.logData(
//            this,
//            "Invoice Body$jObj"
//        )

//        db!!.updateStampInvoiceTransaction(
//            jObj["invoice_id"].asString, "1", ""
//        )
//        db!!.updateInvoiceStampRequestPost(
//            jObj["invoice_id"].asString, "1"
//        )

        Utils.logData(
            this@SyncData,
            "InvoiceStamp Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postStampUpload(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            Utils.logData(
                                this@SyncData,
                                "InvoiceStamp Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateStampInvoiceTransaction(
                                    jObj["invoice_id"].asString, "1", ""
                                )
                                db!!.updateInvoiceStampRequestPost(
                                    jObj["invoice_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateInvoiceStampRequestPost(jObj["invoice_id"].asString, "2")
                                db!!.updateStampInvoiceTransaction(
                                    jObj["invoice_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                val gson = Gson()
                                val type = object : TypeToken<MyErrorMessage>() {}.type
                                Utils.logData(
                                    this@SyncData,
                                    "InvoiceStamp Response fail ${
                                        response.errorBody()!!.toString()
                                    }"
                                )

                                if (type != null) {


                                    var errorResponse: MyErrorMessage? =
                                        gson.fromJson(response.errorBody()!!.charStream(), type)

                                    if (errorResponse!!.errors != null) {
                                        // Fail to Post
                                        db!!.updateInvoiceStampRequestPost(
                                            jObj["invoice_id"].asString,
                                            "2"
                                        )
                                        db!!.updateStampInvoiceTransaction(
                                            jObj["invoice_id"].asString,
                                            "2",
                                            errorResponse!!.errors!!.error
                                        )
                                    } else {
                                        // Fail to Post
                                        db!!.updateInvoiceStampRequestPost(
                                            jObj["invoice_id"].asString,
                                            "2"
                                        )
                                        db!!.updateStampInvoiceTransaction(
                                            jObj["invoice_id"].asString,
                                            "2",
                                            errorResponse!!.message
                                        )
                                    }
                                } else {
                                    db!!.updateInvoiceStampRequestPost(
                                        jObj["invoice_id"].asString,
                                        "2"
                                    )
                                    db!!.updateStampInvoiceTransaction(
                                        jObj["invoice_id"].asString,
                                        "2",
                                        "Error while validating"
                                    )
                                }
                            } else {
                                // Fail to Post
                                db!!.updateInvoiceStampRequestPost(jObj["invoice_id"].asString, "2")
                                db!!.updateStampInvoiceTransaction(
                                    jObj["invoice_id"].asString,
                                    "2",
                                    "Error while validating"
                                )
                            }
                        } catch (e: Exception) {
                            e.toString()
                        }

                    }
                    AppConstants.isInvoiceStampSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateInvoiceStampRequestPost(jObj["invoice_id"].asString, "2")
                    db!!.updateStampInvoiceTransaction(
                        jObj["invoice_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isInvoiceStampSync = false

                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "InvoiceStamp Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "InvoiceStamp API Fail"
                        )

                    }
                }
            })
    }

    private fun callInvoiceDeleteAPI(jsobjects: JsonObject) {
//        Log.e("reasonrequest",jOb.toString())
        val jObj = JsonObject()
        jObj.addProperty("reason", jsobjects["invoice_mesage"].asString)
        var arr = JsonArray()
        arr.add(jsobjects["invoice_number"].asString)
        jObj.add("name", arr)
        Log.e("cancelInvoicerequest", jObj.toString())
        ApiClient.getClient(this)
            .PostReason(
                "Bearer ${BaseActivity.userInfo!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jsobjects["invoice_number"].asString,
                jObj
            )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@SyncData,
                            "invoice delete Response  ${response.body()!!.toString()}"
                        )

                        db!!.updateCancelInvoiceTransaction(
                            jsobjects["invoice_number"].asString, "1", ""
                        )

                    } else {
                        db!!.updateCancelInvoiceTransaction(
                            jsobjects["invoice_number"].asString, "2", ""
                        )

                    }

                }

                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    // Utils.logData(this@DownloadingDataActivity, "Item Failed")
                    db!!.updateCancelInvoiceTransaction(
                        jsobjects["invoice_number"].asString, "2", ""
                    )

                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Invoice delete Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Invoce Delete API Fail"
                        )

                    }
                }
            })
    }

    private fun callInvoiceCancelAPI(jsobjects: JsonObject) {
//        Log.e("reasonrequest",jOb.toString())
        val jObj = JsonObject()
        jObj.addProperty("status", "Canceled")
        Log.e("cancelInvoicerequest", jObj.toString())
        ApiClient.getClient(this)
            .postInvoiceCancel(
                "Bearer ${BaseActivity.userInfo!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jsobjects["invoice_number"].asInt,
                jObj
            )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@SyncData,
                            "invoice delete Response  ${response.body()!!.toString()}"
                        )

                        db!!.updateCancelInvoiceTransaction(
                            jsobjects["invoice_number"].asString, "1", ""
                        )

                    } else {
                        db!!.updateCancelInvoiceTransaction(
                            jsobjects["invoice_number"].asString, "2", ""
                        )

                    }

                }

                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    // Utils.logData(this@DownloadingDataActivity, "Item Failed")
                    db!!.updateCancelInvoiceTransaction(
                        jsobjects["invoice_number"].asString, "2", ""
                    )

                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Invoice delete Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Invoce Delete API Fail"
                        )

                    }
                }
            })
    }

    private fun callDeliveryAPI(jObj: JsonObject) {
        Log.e("Delviery Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Delivery Request ${jObj.toString()}"
        )

//        val uuId: RequestBody =
//            RequestBody.create("text/plain".toMediaTypeOrNull(), jObj.get("uuId").toString())
//        val delivery_id: RequestBody =
//            RequestBody.create("text/plain".toMediaTypeOrNull(), jObj.get("delivery_id").toString())
//        val cancel_comment: RequestBody =
//            RequestBody.create("text/plain".toMediaTypeOrNull(), jObj.get("cancel_comment").toString())
//        val delviery_number: RequestBody =
//            RequestBody.create(
//                "text/plain".toMediaTypeOrNull(),
//                jObj.get("delviery_number").toString()
//            )
//
//        var parts: MultipartBody.Part? = null
//
//        if (!jObj.get("image").toString().equals("", ignoreCase = true)) {
//            // use the FileUtils to get the actual file by uri
//
////            var mSplit = jObj.get("image").toString().split("/")
////            var mLength = mSplit.size
//
//            parts = Utils.prepareFileInvoicePart("image", jObj.get("image").toString())
//        }
//
//        Log.e(
//            "Image Part",parts.toString()
//        )
//        //if (parts != null) {
//        ApiClient.getClient(this)
//            .postDeliveryCancelFile(
//                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
//                MyApplication.X_DOMAIN,
//                parts!!,
//                uuId,
//                delivery_id,
//                cancel_comment,
//                delviery_number
//            )
//            .enqueue(object :
//                Callback<JsonObject> {
//                override fun onResponse(
//                    call: Call<JsonObject>,
//                    response: Response<JsonObject>
//                ) {
//                    if (response.isSuccessful) {
//                        if (response.body() != null) {
//                            Utils.logData(
//                                this@SyncData,
//                                "Delivery Response  ${response.body()!!.toString()}"
//                            )
//
//                            if (response.body()!!["status"].asBoolean) {
//
//                                db!!.updateDelvieryTransaction(
//                                    jObj["delviery_number"].asString, "1", ""
//                                )
//                                db!!.updateDelvieryRequestPost(
//                                    jObj["delviery_number"].asString, "1"
//                                )
//
//                            } else {
//                                // Fail to Post
//                                db!!.updateDelvieryRequestPost(
//                                    jObj["delviery_number"].asString,
//                                    "2"
//                                )
//                                db!!.updateDelvieryTransaction(
//                                    jObj["delviery_number"].asString,
//                                    "2",
//                                    response.body()!!["message"].asString
//                                )
//                            }
//                        }
//                    } else {
//
//                        if (response.code() == 500) {
//
//                            db!!.updateDelvieryRequestPost(
//                                jObj["delviery_number"].asString,
//                                "2"
//                            )
//                            db!!.updateDelvieryTransaction(
//                                jObj["delviery_number"].asString,
//                                "2",
//                                "500 API Error"
//                            )
//                            Utils.logData(
//                                this@SyncData,
//                                "Order Response fail 500 Error"
//                            )
//
//                        } else {
//                            if (response.errorBody() != null) {
//                                Utils.logData(
//                                    this@SyncData,
//                                    "Order Response fail ${response.errorBody()!!.toString()}"
//                                )
//
//                                val gson = Gson()
//                                val type = object : TypeToken<MyErrorMessage>() {}.type
//                                var errorResponse: MyErrorMessage? =
//                                    gson.fromJson(response.errorBody()!!.charStream(), type)
//
//                                if (errorResponse!!.errors != null) {
//                                    // Fail to Post
//                                    db!!.updateDelvieryRequestPost(
//                                        jObj["delviery_number"].asString,
//                                        "2"
//                                    )
//                                    db!!.updateDelvieryTransaction(
//                                        jObj["delviery_number"].asString,
//                                        "2",
//                                        errorResponse!!.errors!!.error
//                                    )
//                                } else {
//                                    // Fail to Post
//                                    db!!.updateDelvieryRequestPost(
//                                        jObj["delviery_number"].asString,
//                                        "2"
//                                    )
//                                    db!!.updateDelvieryTransaction(
//                                        jObj["delviery_number"].asString,
//                                        "2",
//                                        errorResponse!!.message
//                                    )
//                                }
//                            } else {
//                                // Fail to Post
//                                db!!.updateDelvieryRequestPost(
//                                    jObj["delviery_number"].asString,
//                                    "2"
//                                )
//                                db!!.updateDelvieryTransaction(
//                                    jObj["delviery_number"].asString,
//                                    "2",
//                                    "Error while validating"
//                                )
//                            }
//                        }
//
//
//                    }
//                    AppConstants.isDelvierySync = false
//                    sendBroadcast(intent)
//                }
//
//                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
//                    // Fail to Post
//                    AppConstants.isDelvierySync = false
//                    Log.e("Delivery Fail",error.message.toString())
//                    db!!.updateDelvieryRequestPost(jObj["delviery_number"].asString, "2")
//                    db!!.updateDelvieryTransaction(
//                        jObj["delviery_number"].asString,
//                        "2",
//                        "API Fail"
//                    )
//                    AppConstants.isDelvierySync = false
//
//                    if (error.message != null) {
//                        Utils.logData(
//                            this@SyncData,
//                            "Delivery Response Fail${error.message.toString()}"
//                        )
//                    } else {
//                        Utils.logData(
//                            this@SyncData,
//                            "Delivery API Fail"
//                        )
//
//                    }
//                }
//            })
        // } else {
        ApiClient.getClient(this)
            .getPostDeliveryCancel(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Delivery Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateDelvieryTransaction(
                                    jObj["delviery_number"].asString, "1", ""
                                )
                                db!!.updateDelvieryRequestPost(
                                    jObj["delviery_number"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateDelvieryRequestPost(
                                    jObj["delviery_number"].asString,
                                    "2"
                                )
                                db!!.updateDelvieryTransaction(
                                    jObj["delviery_number"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {

                        if (response.code() == 500) {

                            db!!.updateDelvieryRequestPost(
                                jObj["delviery_number"].asString,
                                "2"
                            )
                            db!!.updateDelvieryTransaction(
                                jObj["delviery_number"].asString,
                                "2",
                                "500 API Error"
                            )
                            Utils.logData(
                                this@SyncData,
                                "Order Response fail 500 Error"
                            )

                        } else {
                            if (response.errorBody() != null) {
                                Utils.logData(
                                    this@SyncData,
                                    "Order Response fail ${response.errorBody()!!.toString()}"
                                )

                                val gson = Gson()
                                val type = object : TypeToken<MyErrorMessage>() {}.type
                                var errorResponse: MyErrorMessage? =
                                    gson.fromJson(response.errorBody()!!.charStream(), type)

                                if (errorResponse!!.errors != null) {
                                    // Fail to Post
                                    db!!.updateDelvieryRequestPost(
                                        jObj["delviery_number"].asString,
                                        "2"
                                    )
                                    db!!.updateDelvieryTransaction(
                                        jObj["delviery_number"].asString,
                                        "2",
                                        errorResponse!!.errors!!.error
                                    )
                                } else {
                                    // Fail to Post
                                    db!!.updateDelvieryRequestPost(
                                        jObj["delviery_number"].asString,
                                        "2"
                                    )
                                    db!!.updateDelvieryTransaction(
                                        jObj["delviery_number"].asString,
                                        "2",
                                        errorResponse!!.message
                                    )
                                }
                            } else {
                                // Fail to Post
                                db!!.updateDelvieryRequestPost(
                                    jObj["delviery_number"].asString,
                                    "2"
                                )
                                db!!.updateDelvieryTransaction(
                                    jObj["delviery_number"].asString,
                                    "2",
                                    "Error while validating"
                                )
                            }
                        }


                    }
                    AppConstants.isDelvierySync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateDelvieryRequestPost(jObj["delviery_number"].asString, "2")
                    db!!.updateDelvieryTransaction(
                        jObj["delviery_number"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isDelvierySync = false

                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Delivery Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Delivery API Fail"
                        )

                    }
                }
            })
        // }

    }

    private fun callCollectionAPI(jObj: JsonObject) {
        Utils.logData(
            this@SyncData,
            "Collection Request ${jObj.toString()}"
        )
        Log.e("Collection Body", jObj.toString())
        ApiClient.getClient(this)
            .postCollection(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    Log.e("collection", response.toString())
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Collection Response  ${response.body()!!.toString()}"
                            )
                            Log.e("collection", response.body()!!.toString())

                            if (response.body()!!["status"].asBoolean) {


                                db!!.updateCollectionTransaction(
                                    jObj["collection_number"].asString, "1", ""
                                )
                                db!!.updateCollectionPost(
                                    jObj["collection_number"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateCollectionPost(jObj["collection_number"].asString, "2")
                                db!!.updateCollectionTransaction(
                                    jObj["collection_number"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {

                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "Collection Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type

                            db!!.updateCollectionPost(jObj["collection_number"].asString, "2")
                            db!!.updateCollectionTransaction(
                                jObj["collection_number"].asString,
                                "2",
                                "Error validating"
                            )

//                            if (type != null) {
//
//                                var errorResponse: MyErrorMessage? =
//                                    gson.fromJson(response.errorBody()!!.charStream(), type)
//
//                                if (errorResponse!!.errors != null) {
//                                    // Fail to Post
//                                    if (!jObj["collection_number"].isJsonNull) {
//                                        db!!.updateCollectionPost(
//                                            jObj["collection_number"].asString,
//                                            "2"
//                                        )
//                                        db!!.updateCollectionTransaction(
//                                            jObj["collection_number"].asString,
//                                            "2",
//                                            errorResponse!!.errors!!.error
//                                        )
//                                    }
//                                } else {
//                                    // Fail to Post
//                                    db!!.updateCollectionPost(
//                                        jObj["collection_number"].asString,
//                                        "2"
//                                    )
//                                    db!!.updateCollectionTransaction(
//                                        jObj["collection_number"].asString,
//                                        "2",
//                                        errorResponse!!.message
//                                    )
//
//                                }
//                            } else {
//                                db!!.updateCollectionPost(jObj["collection_number"].asString, "2")
//                                db!!.updateCollectionTransaction(
//                                    jObj["collection_number"].asString,
//                                    "2",
//                                    "Error validating"
//                                )
//                            }
                        } else {
                            // Fail to Post
                            db!!.updateCollectionPost(jObj["collection_number"].asString, "2")
                            db!!.updateCollectionTransaction(
                                jObj["collection_number"].asString,
                                "2",
                                "Error validating"
                            )
                        }
                    }
                    AppConstants.isCollectionSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateCollectionPost(jObj["collection_number"].asString, "2")
                    db!!.updateCollectionTransaction(
                        jObj["collection_number"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isCollectionSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Collection Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Collection API Fail"
                        )

                    }
                }
            })
    }

    private fun callReturnAPI(jObj: JsonObject) {
        Log.e("Return Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Return Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postReturn(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            Utils.logData(
                                this@SyncData,
                                "Return Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                var invId =
                                    response.body()!!["data"].asJsonObject!!["id"].toString()

                                db!!.updateCreditIdCollection(
                                    jObj["credit_note_number"].asString,
                                    invId
                                )

                                db!!.updateReturnTransaction(
                                    jObj["credit_note_number"].asString, "1", ""
                                )
                                db!!.updateReturnRequestPost(
                                    jObj["credit_note_number"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnRequestPost(
                                    jObj["credit_note_number"].asString,
                                    "2"
                                )
                                db!!.updateReturnTransaction(
                                    jObj["credit_note_number"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {

                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "Return Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type

                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                // Fail to Post
                                db!!.updateReturnRequestPost(
                                    jObj["credit_note_number"].asString,
                                    "2"
                                )
                                db!!.updateReturnTransaction(
                                    jObj["credit_note_number"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnRequestPost(
                                    jObj["credit_note_number"].asString,
                                    "2"
                                )
                                db!!.updateReturnTransaction(
                                    jObj["credit_note_number"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {
                            // Fail to Post
                            db!!.updateReturnRequestPost(
                                jObj["credit_note_number"].asString,
                                "2"
                            )
                            db!!.updateReturnTransaction(
                                jObj["credit_note_number"].asString,
                                "2",
                                "Error while validating"
                            )
                        }

                    }
                    AppConstants.isReturnSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateReturnRequestPost(jObj["credit_note_number"].asString, "2")
                    db!!.updateReturnTransaction(
                        jObj["credit_note_number"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isReturnSync = false

                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Return Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Return API Fail"
                        )

                    }
                }
            })
    }

    private fun callReturnCompletePostAPI(jObj: String) {
        Log.e("ReturnPost Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Return Post Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .getReturnAccept(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Return complete Response  ${response.body()!!.toString()}"
                            )

                            var mOrderNo = db!!.getReturnNo(jObj);
                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateReturnRRCTransaction(
                                    mOrderNo, "1", ""
                                )
                                db!!.updateReturnCPRequestPost(
                                    jObj, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnCPRequestPost(
                                    jObj,
                                    "2"
                                )
                                db!!.updateReturnRRCTransaction(
                                    mOrderNo,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        var mOrderNo = db!!.getReturnNo(jObj);
                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "Return Complete Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type

                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                // Fail to Post
                                db!!.updateReturnCPRequestPost(
                                    jObj,
                                    "2"
                                )
                                db!!.updateReturnRRCTransaction(
                                    mOrderNo,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnCPRequestPost(
                                    jObj,
                                    "2"
                                )
                                db!!.updateReturnRRCTransaction(
                                    mOrderNo,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {
                            // Fail to Post
                            db!!.updateReturnCPRequestPost(
                                jObj,
                                "2"
                            )
                            db!!.updateReturnRRCTransaction(
                                mOrderNo,
                                "2",
                                "Error while validating"
                            )
                        }

                    }
                    AppConstants.isReturnRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    var mOrderNo = db!!.getReturnNo(jObj);
                    db!!.updateReturnCPRequestPost(jObj, "2")
                    db!!.updateReturnRRCTransaction(
                        mOrderNo,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isReturnRequestSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Return complete Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Return complete API Fail"
                        )

                    }
                }
            })
    }

    private fun callReturnCompletePostAPINew(jObj: JsonObject) {
        Log.e("ReturnPost Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Return Post Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .getReturnPostAccept(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Return complete Response  ${response.body()!!.toString()}"
                            )

                            var mOrderNo = jObj["credit_note_no"].asString
                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateReturnRRCTransaction(
                                    mOrderNo, "1", ""
                                )
                                db!!.updateReturnCPRequestPost(
                                    mOrderNo, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnCPRequestPost(
                                    mOrderNo,
                                    "2"
                                )
                                db!!.updateReturnRRCTransaction(
                                    mOrderNo,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        var mOrderNo = jObj["credit_note_no"].asString
                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "Return Complete Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type

                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                // Fail to Post
                                db!!.updateReturnCPRequestPost(
                                    mOrderNo,
                                    "2"
                                )
                                db!!.updateReturnRRCTransaction(
                                    mOrderNo,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnCPRequestPost(
                                    mOrderNo,
                                    "2"
                                )
                                db!!.updateReturnRRCTransaction(
                                    mOrderNo,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {
                            // Fail to Post
                            db!!.updateReturnCPRequestPost(
                                mOrderNo,
                                "2"
                            )
                            db!!.updateReturnRRCTransaction(
                                mOrderNo,
                                "2",
                                "Error while validating"
                            )
                        }

                    }
                    AppConstants.isReturnRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    var mOrderNo = jObj["credit_note_no"].asString
                    db!!.updateReturnCPRequestPost(mOrderNo, "2")
                    db!!.updateReturnRRCTransaction(
                        mOrderNo,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isReturnRequestSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Return complete Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Return complete API Fail"
                        )

                    }
                }
            })
    }

    private fun callReturnPallerAPINew(jObj: JsonObject) {
        Log.e("ReturnPallet Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "ReturnPallet Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .getReturnPalletPost(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "ReturnPallet Response  ${response.body()!!.toString()}"
                            )

                            var mOrderNo = jObj["return_number"].asString
                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateReturnTransaction(
                                    mOrderNo, "1", ""
                                )
                                db!!.updateReturnPalletRequestPost(
                                    mOrderNo, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnPalletRequestPost(
                                    mOrderNo,
                                    "2"
                                )
                                db!!.updateReturnTransaction(
                                    mOrderNo,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        var mOrderNo = jObj["return_number"].asString
                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "ReturnPallet Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type

                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                // Fail to Post
                                db!!.updateReturnPalletRequestPost(
                                    mOrderNo,
                                    "2"
                                )
                                db!!.updateReturnTransaction(
                                    mOrderNo,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnPalletRequestPost(
                                    mOrderNo,
                                    "2"
                                )
                                db!!.updateReturnTransaction(
                                    mOrderNo,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {
                            // Fail to Post
                            db!!.updateReturnPalletRequestPost(
                                mOrderNo,
                                "2"
                            )
                            db!!.updateReturnTransaction(
                                mOrderNo,
                                "2",
                                "Error while validating"
                            )
                        }

                    }
                    AppConstants.isReturnPalletRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    var mOrderNo = jObj["return_number"].asString
                    db!!.updateReturnPalletRequestPost(mOrderNo, "2")
                    db!!.updateReturnTransaction(
                        mOrderNo,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isReturnPalletRequestSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "RReturnPallet Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "ReturnPallet API Fail"
                        )

                    }
                }
            })
    }

    private fun callReturnCancelAPINew(jObj: JsonObject) {
        Log.e("ReturnPost Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Return Post Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .getReturnCanelPostAccept(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj.get("uuid").asString
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Return complete Response  ${response.body()!!.toString()}"
                            )

                            var mOrderNo = jObj["credit_note_no"].asString
                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateReturnRRCTransaction(
                                    mOrderNo, "1", ""
                                )
                                db!!.updateReturnCPRequestPost(
                                    mOrderNo, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnCPRequestPost(
                                    mOrderNo,
                                    "2"
                                )
                                db!!.updateReturnRRCTransaction(
                                    mOrderNo,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        var mOrderNo = jObj["credit_note_no"].asString
                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "Return Complete Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type

                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                // Fail to Post
                                db!!.updateReturnCPRequestPost(
                                    mOrderNo,
                                    "2"
                                )
                                db!!.updateReturnRRCTransaction(
                                    mOrderNo,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateReturnCPRequestPost(
                                    mOrderNo,
                                    "2"
                                )
                                db!!.updateReturnRRCTransaction(
                                    mOrderNo,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {
                            // Fail to Post
                            db!!.updateReturnCPRequestPost(
                                mOrderNo,
                                "2"
                            )
                            db!!.updateReturnRRCTransaction(
                                mOrderNo,
                                "2",
                                "Error while validating"
                            )
                        }

                    }
                    AppConstants.isReturnCancelRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    var mOrderNo = jObj["credit_note_no"].asString
                    db!!.updateReturnCPRequestPost(mOrderNo, "2")
                    db!!.updateReturnRRCTransaction(
                        mOrderNo,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isReturnCancelRequestSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Return complete Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Return complete API Fail"
                        )

                    }
                }
            })
    }

    private fun callUnloadAPI(jObj: JsonObject) {
        Log.e("Unload Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Unload Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postUnload(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Unload Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString, "1", ""
                                )
                                db!!.updateUnloadPost(
                                    jObj["code"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateUnloadPost(jObj["code"].asString, "2")
                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {

                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "unload Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type

//                            var errorResponse: MyErrorMessage? =
//                                gson.fromJson(response.errorBody()!!.charStream(), type)
//                            if (errorResponse!!.errors != null) {
//                                // Fail to Post
//                                // Fail to Post
//                                db!!.updateUnloadPost(jObj["code"].asString, "2")
//                                db!!.updateUnloadTransaction(
//                                    jObj["code"].asString,
//                                    "2",
//                                    errorResponse!!.errors!!.error
//                                )
//                            } else {
                            db!!.updateUnloadPost(jObj["code"].asString, "2")
                            db!!.updateUnloadTransaction(
                                jObj["code"].asString,
                                "2",
                                "API Fail"
                            )
                            //}
                        } else {
                            // Fail to Post
                            db!!.updateUnloadPost(jObj["code"].asString, "2")
                            db!!.updateUnloadTransaction(
                                jObj["code"].asString,
                                "2",
                                "Response Blank"
                            )
                        }
                    }
                    AppConstants.isUnloadRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateUnloadPost(jObj["code"].asString, "2")
                    db!!.updateUnloadTransaction(
                        jObj["code"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isUnloadRequestSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Unload Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Unload API Fail"
                        )

                    }
                }
            })
    }

    private fun callLoadvarUnloadAPI(jObj: JsonObject) {
        Log.e("LoadUnload Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "LoadUnload Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postUnload(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "LoadUnload Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString, "1", ""
                                )
                                db!!.updateUnloadPost(
                                    jObj["code"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateUnloadPost(jObj["code"].asString, "2")
                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {

                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "unload Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type

//                            var errorResponse: MyErrorMessage? =
//                                gson.fromJson(response.errorBody()!!.charStream(), type)
//                            if (errorResponse!!.errors != null) {
//                                // Fail to Post
//                                // Fail to Post
//                                db!!.updateUnloadPost(jObj["code"].asString, "2")
//                                db!!.updateUnloadTransaction(
//                                    jObj["code"].asString,
//                                    "2",
//                                    errorResponse!!.errors!!.error
//                                )
//                            } else {
                            db!!.updateUnloadPost(jObj["code"].asString, "2")
                            db!!.updateUnloadTransaction(
                                jObj["code"].asString,
                                "2",
                                "API Fail"
                            )
                            //}
                        } else {
                            // Fail to Post
                            db!!.updateUnloadPost(jObj["code"].asString, "2")
                            db!!.updateUnloadTransaction(
                                jObj["code"].asString,
                                "2",
                                "Response Blank"
                            )
                        }
                    }
                    AppConstants.isLoadVarianceRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateUnloadPost(jObj["code"].asString, "2")
                    db!!.updateUnloadTransaction(
                        jObj["code"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isLoadVarianceRequestSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Unload Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Unload API Fail"
                        )

                    }
                }
            })
    }

    private fun callUnloadVanReturnAPI(jObj: JsonObject) {
        Log.e("Unload Return Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Unload Return Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postUnload(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Unload van return Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString, "1", ""
                                )
                                db!!.updateUnloadPost(
                                    jObj["code"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateUnloadPost(jObj["code"].asString, "2")
                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        // Fail to Post
                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "Unload van return Response fail ${
                                    response.errorBody()!!.toString()
                                }"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type

//                            if (response.errorBody()!!.charStream() != null) {
//                                var errorResponse: MyErrorMessage? =
//                                    gson.fromJson(response.errorBody()!!.charStream(), type)
//                                if (errorResponse!!.errors != null) {
//                                    // Fail to Post
//                                    // Fail to Post
//                                    db!!.updateUnloadPost(jObj["code"].asString, "2")
//                                    db!!.updateUnloadTransaction(
//                                        jObj["code"].asString,
//                                        "2",
//                                        errorResponse!!.errors!!.error
//                                    )
//                                } else {
//                                    db!!.updateUnloadPost(jObj["code"].asString, "2")
//                                    db!!.updateUnloadTransaction(
//                                        jObj["code"].asString,
//                                        "2",
//                                        "Response Blank"
//                                    )
//                                }
//                            } else {
                            db!!.updateUnloadPost(jObj["code"].asString, "2")
                            db!!.updateUnloadTransaction(
                                jObj["code"].asString,
                                "2",
                                "Response Blank"
                            )
                            //  }

                        } else {
                            // Fail to Post
                            db!!.updateUnloadPost(jObj["code"].asString, "2")
                            db!!.updateUnloadTransaction(
                                jObj["code"].asString,
                                "2",
                                "Response Blank"
                            )
                        }
                    }
                    AppConstants.isUnloadVanRetunrRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateUnloadPost(jObj["code"].asString, "2")
                    db!!.updateUnloadTransaction(
                        jObj["code"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isUnloadVanRetunrRequestSync = false

                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Unload van return Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Unload van return API Fail"
                        )

                    }
                }
            })
    }

    private fun callUnloadVarianceAPI(jObj: JsonObject) {
        Log.e("Unload Varieance Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Unload Varieance Request ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postUnload(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Unload variance Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString, "1", ""
                                )
                                db!!.updateUnloadPost(
                                    jObj["code"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateUnloadPost(jObj["code"].asString, "2")
                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        // Fail to Post
                        if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            Utils.logData(
                                this@SyncData,
                                "Unload variance Response fail ${response.errorBody()!!.toString()}"
                            )

                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)
                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                // Fail to Post
                                db!!.updateUnloadPost(jObj["code"].asString, "2")
                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )
                            } else {
                                db!!.updateUnloadPost(jObj["code"].asString, "2")
                                db!!.updateUnloadTransaction(
                                    jObj["code"].asString,
                                    "2",
                                    "Response Blank"
                                )
                            }
                        } else {
                            // Fail to Post
                            db!!.updateUnloadPost(jObj["code"].asString, "2")
                            db!!.updateUnloadTransaction(
                                jObj["code"].asString,
                                "2",
                                "Response Blank"
                            )
                        }
                    }
                    AppConstants.isUnloadVarianceRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateUnloadPost(jObj["code"].asString, "2")
                    db!!.updateUnloadTransaction(
                        jObj["code"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isUnloadVarianceRequestSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Unload variance Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Unload variance API Fail"
                        )

                    }
                }
            })
    }

    private fun callLoadVarianceAPI(jObj: JsonObject) {
        Log.e("Load Variance Body", jObj.toString())
        ApiClient.getClient(this)
            .postDebitNote(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            Utils.logData(
                                this@SyncData,
                                "Load variance Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateUnloadTransaction(
                                    jObj["debit_note_number"].asString, "1", ""
                                )
                                db!!.updateLoadVarianceDPost(
                                    jObj["debit_note_number"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateLoadVarianceDPost(
                                    jObj["debit_note_number"].asString,
                                    "2"
                                )
                                db!!.updateUnloadTransaction(
                                    jObj["debit_note_number"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateUnloadPost(jObj["trip_id"].asString, "2")
                                db!!.updateUnloadTransaction(
                                    jObj["trip_id"].asString,
                                    "2",
                                    errorResponse!!.errors!!.getString("error")
                                )

                            } else {
                                // Fail to Post
                                db!!.updateUnloadPost(jObj["trip_id"].asString, "2")
                                db!!.updateUnloadTransaction(
                                    jObj["trip_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateLoadVarianceDPost(jObj["debit_note_number"].asString, "2")
                        db!!.updateUnloadTransaction(
                            jObj["debit_note_number"].asString,
                            "2",
                            "Response Blank"
                        )
                        //}
                    }
                    AppConstants.isLoadVarianceRequestSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateLoadVarianceDPost(jObj["debit_note_number"].asString, "2")
                    db!!.updateUnloadTransaction(
                        jObj["debit_note_number"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isLoadVarianceRequestSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Load variance Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Load variance API Fail"
                        )

                    }
                }
            })
    }

    private fun callComplaintAPI(jObj: JsonObject) {
        Log.e("Complaint Body", jObj.toString())
        ApiClient.getClient(this)
            .postComplaint(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Complaint Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["complaint_id"].asString, "1", ""
                                )
                                db!!.updateComplaintPost(
                                    jObj["complaint_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateComplaintPost(jObj["complaint_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["complaint_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "Complaint Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateComplaintPost(jObj["complaint_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["complaint_id"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateComplaintPost(jObj["complaint_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["complaint_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )
                            }
                        } else {
                            // Fail to Post
                            db!!.updateComplaintPost(jObj["complaint_id"].asString, "2")
                            db!!.updateComplaintTransaction(
                                jObj["complaint_id"].asString,
                                "2",
                                "Response Blank"
                            )
                        }
                    }
                    AppConstants.isComplaintSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateComplaintPost(jObj["complaint_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["complaint_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isComplaintSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Complaint Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Complaint API Fail"
                        )

                    }
                }
            })
    }

    private fun callSuggestionAPI(jObj: JsonObject) {
        Log.e("Suggestion Body", jObj.toString())
        ApiClient.getClient(this)
            .postComplaint(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Suggestion Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["complaint_id"].asString, "1", ""
                                )
                                db!!.updateSuggestionPost(
                                    jObj["complaint_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateSuggestionPost(jObj["complaint_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["complaint_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            val gson = Gson()
                            Utils.logData(
                                this@SyncData,
                                "Suggetion Response fail ${response.errorBody()!!.toString()}"
                            )

                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateSuggestionPost(jObj["complaint_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["complaint_id"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateSuggestionPost(jObj["complaint_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["complaint_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {
                            // Fail to Post
                            db!!.updateSuggestionPost(jObj["complaint_id"].asString, "2")
                            db!!.updateComplaintTransaction(
                                jObj["complaint_id"].asString,
                                "2",
                                "Response Blank"
                            )
                        }
                    }
                    AppConstants.isSuggestionSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateSuggestionPost(jObj["complaint_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["complaint_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isSuggestionSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Suggestion Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Suggestion API Fail"
                        )

                    }
                }
            })
    }

    private fun callCampaignAPI(jObj: JsonObject) {
        Log.e("Campaign Body", jObj.toString())
        ApiClient.getClient(this)
            .postCampaign(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            Utils.logData(
                                this@SyncData,
                                "Compaign Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["campaign_id"].asString, "1", ""
                                )
                                db!!.updateCampaignPost(
                                    jObj["campaign_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateCampaignPost(jObj["campaign_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["campaign_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateCampaignPost(jObj["campaign_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["campaign_id"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateCampaignPost(jObj["campaign_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["campaign_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateCampaignPost(jObj["campaign_id"].asString, "2")
                        db!!.updateComplaintTransaction(
                            jObj["campaign_id"].asString,
                            "2",
                            "Response Blank"
                        )
                        // }
                    }
                    AppConstants.isCampaignSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateCampaignPost(jObj["campaign_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["campaign_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isCampaignSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Compaign Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Compaign API Fail"
                        )

                    }
                }
            })
    }

    private fun callCompetitorAPI(jObj: JsonObject) {
        Log.e("Competitor Body", jObj.toString())
        ApiClient.getClient(this)
            .postCompetitor(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            if (response.body()!!["status"].asBoolean) {
                                Utils.logData(
                                    this@SyncData,
                                    "Competitor Response  ${response.body()!!.toString()}"
                                )

                                db!!.updateComplaintTransaction(
                                    jObj["competitor_id"].asString, "1", ""
                                )
                                db!!.updateCompetitorPost(
                                    jObj["competitor_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateCompetitorPost(jObj["competitor_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["competitor_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateCompetitorPost(jObj["competitor_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["competitor_id"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateCompetitorPost(jObj["competitor_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["competitor_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateCompetitorPost(jObj["competitor_id"].asString, "2")
                        db!!.updateComplaintTransaction(
                            jObj["competitor_id"].asString,
                            "2",
                            "Response Blank"
                        )
                        // }
                    }
                    AppConstants.isCompititorSync = false
                    sendBroadcast(intent)
                    Utils.logData(
                        this@SyncData,
                        "Compitetor Response fail ${response.errorBody()!!.toString()}"
                    )

                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateCompetitorPost(jObj["competitor_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["competitor_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isCompititorSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Compitetor Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Competitor API Fail"
                        )

                    }
                }
            })
    }

    private fun callInventoryAPI(jObj: JsonObject) {
        Log.e("Inventory Body", jObj.toString())
        ApiClient.getClient(this)
            .postInventory(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Inventory Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString, "1", ""
                                )
                                db!!.updateInventoryPost(
                                    jObj["unique_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateInventoryPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateInventoryPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateInventoryPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateInventoryPost(jObj["unique_id"].asString, "2")
                        db!!.updateComplaintTransaction(
                            jObj["unique_id"].asString,
                            "2",
                            "Response Blank"
                        )
                        Utils.logData(
                            this@SyncData,
                            "Inventory Response fail ${response.errorBody()!!.toString()}"
                        )

                        // }
                    }
                    AppConstants.isInventoryPost = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateInventoryPost(jObj["unique_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["unique_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isInventoryPost = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Inventory Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Inventory API Fail"
                        )

                    }
                }
            })
    }

    private fun callPostDistributionAPI(jObj: JsonObject) {
        Log.e("Tool Body", jObj.toString())
        Utils.logData(
            this@SyncData,
            "Post Distribution Request  ${jObj.toString()}"
        )
        ApiClient.getClient(this)
            .postStock(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Post Distribution Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString, "1", ""
                                )
                                db!!.updateToolPost(
                                    jObj["unique_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateToolPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateToolPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateToolPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateToolPost(jObj["unique_id"].asString, "2")
                        db!!.updateComplaintTransaction(
                            jObj["unique_id"].asString,
                            "2",
                            "Response Blank"
                        )
                        Utils.logData(
                            this@SyncData,
                            "post Distribution Response fail ${response.errorBody()!!.toString()}"
                        )

                        // }
                    }
                    AppConstants.isStockSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateToolPost(jObj["unique_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["unique_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isStockSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Post Distribution Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Post Distribution API Fail"
                        )

                    }
                }
            })
    }

    private fun callPostDistributionImageAPI(jObj: JsonObject) {
        Log.e("ToolImage Body", jObj.toString())
        ApiClient.getClient(this)
            .postStockImage(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Post Distribution Image Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString, "1", ""
                                )
                                db!!.updateToolImagePost(
                                    jObj["unique_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateToolImagePost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateToolImagePost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.errors!!.error
                                )

                            } else {
                                // Fail to Post
                                db!!.updateToolImagePost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateToolImagePost(jObj["unique_id"].asString, "2")
                        db!!.updateComplaintTransaction(
                            jObj["unique_id"].asString,
                            "2",
                            "Response Blank"
                        )

                        Utils.logData(
                            this@SyncData,
                            "Post Distribution Image Response fail ${
                                response.errorBody()!!.toString()
                            }"
                        )

                        // }
                    }
                    AppConstants.isDisImageSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateToolImagePost(jObj["unique_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["unique_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isDisImageSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Post Distribution Image Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Post Distribution Image API Fail"
                        )

                    }
                }
            })
    }

    private fun callPostPlanogramAPI(jObj: JsonObject) {
        Log.e("Planogram Body", jObj.toString())
        ApiClient.getClient(this)
            .postPlanogram(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Post Planogram Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString, "1", ""
                                )
                                db!!.updatePlanogramPost(
                                    jObj["unique_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updatePlanogramPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updatePlanogramPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.errors
                                )

                            } else {
                                // Fail to Post
                                db!!.updatePlanogramPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updatePlanogramPost(jObj["unique_id"].asString, "2")
                        db!!.updateComplaintTransaction(
                            jObj["unique_id"].asString,
                            "2",
                            "Response Blank"
                        )
                        Utils.logData(
                            this@SyncData,
                            "Post Planogram Response fail ${response.errorBody()!!.toString()}"
                        )

                        // }
                    }
                    AppConstants.isPlanogramSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updatePlanogramPost(jObj["unique_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["unique_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isPlanogramSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Post Planogram Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Post Planogram API Fail"
                        )

                    }
                }
            })
    }

    private fun callPostAssetsAPI(jObj: JsonObject) {
        Log.e("Assets Body", jObj.toString())
        ApiClient.getClient(this)
            .postAssets(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Post Assets Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString, "1", ""
                                )
                                db!!.updateAssetsPost(
                                    jObj["unique_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateAssetsPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateAssetsPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.errors
                                )

                            } else {
                                // Fail to Post
                                db!!.updateAssetsPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateAssetsPost(jObj["unique_id"].asString, "2")
                        db!!.updateComplaintTransaction(
                            jObj["unique_id"].asString,
                            "2",
                            "Response Blank"
                        )
                        Utils.logData(
                            this@SyncData,
                            "Post Assets Response fail ${response.errorBody()!!.toString()}"
                        )

                        // }
                    }
                    AppConstants.isAssetsSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateAssetsPost(jObj["unique_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["unique_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isAssetsSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Post Assets Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Post Assetes API Fail"
                        )

                    }
                }
            })
    }

    private fun callPostSurveyAPI(jObj: JsonObject) {
        Log.e("Assets Body", jObj.toString())
        ApiClient.getClient(this)
            .postSurvey(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Post Survay Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString, "1", ""
                                )
                                db!!.updateSurveyPost(
                                    jObj["unique_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.errors
                                )

                            } else {
                                // Fail to Post
                                db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                        db!!.updateComplaintTransaction(
                            jObj["unique_id"].asString,
                            "2",
                            "Response Blank"
                        )
                        Utils.logData(
                            this@SyncData,
                            "Post Survey Response fail ${response.errorBody()!!.toString()}"
                        )

                        // }
                    }
                    AppConstants.isConsumerPost = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["unique_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isConsumerPost = false

                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Post Survey Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Post survey API Fail"
                        )

                    }
                }
            })
    }

    private fun callPostPromotionalAPI(jObj: JsonObject) {
        Log.e("Promotional Body", jObj.toString())
        ApiClient.getClient(this)
            .postPromotional(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Post Promotion Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString, "1", ""
                                )
                                db!!.updatePromotionalPost(
                                    jObj["unique_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updatePromotionalPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    response.body()!!["message"].asString
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.errors
                                )

                            } else {
                                // Fail to Post
                                db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updatePromotionalPost(jObj["unique_id"].asString, "2")
                        db!!.updateComplaintTransaction(
                            jObj["unique_id"].asString,
                            "2",
                            "Response Blank"
                        )
                        Utils.logData(
                            this@SyncData,
                            "Post Promotion Response fail ${response.errorBody()!!.toString()}"
                        )

                        // }
                    }
                    AppConstants.isPromotionalSync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updatePromotionalPost(jObj["unique_id"].asString, "2")
                    db!!.updateComplaintTransaction(
                        jObj["unique_id"].asString,
                        "2",
                        "API Fail"
                    )
                    AppConstants.isPromotionalSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Post Promotion Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Post Promotion API Fail"
                        )

                    }
                }
            })
    }


    private fun callPostVisitAPI(jObj: JsonObject) {
        Log.e("Visit Body", jObj.toString())
        ApiClient.getClient(this)
            .postVisit(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Post Visit Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                var jData = response.body()!!["data"].asJsonObject
                                var mVisitId = jData.get("id").asString

                                db!!.updateVisitPost(
                                    jObj["visit_id"].asString, "1", mVisitId
                                )

                            } else {
                                // Fail to Post
                                db!!.updateVisitPost(jObj["visit_id"].asString, "2", "")
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.errors
                                )

                            } else {
                                // Fail to Post
                                db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateVisitPost(jObj["visit_id"].asString, "2", "")
                        Utils.logData(
                            this@SyncData,
                            "Post Visit Response fail ${response.errorBody()!!.toString()}"
                        )

                        // }
                    }
                    AppConstants.isVisitSync = false
                    sendBroadcast(intent)

                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateVisitPost(jObj["visit_id"].asString, "2", "")
                    AppConstants.isVisitSync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Post Visit Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Post Visit API Fail"
                        )

                    }
                }
            })
    }

    private fun callPostVisitActivityAPI(jObj: JsonObject) {
        Log.e("Visit Activity Body", jObj.toString())
        ApiClient.getClient(this)
            .postVisitActivity(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SyncData,
                                "Post Visit Activity Response  ${response.body()!!.toString()}"
                            )

                            if (response.body()!!["status"].asBoolean) {

                                db!!.updateVisitActivityPost(
                                    jObj["customer_visit_id"].asString, "1"
                                )

                            } else {
                                // Fail to Post
                                db!!.updateVisitActivityPost(
                                    jObj["customer_visit_id"].asString,
                                    "2"
                                )
                            }
                        }
                    } else {
                        /*if (response.errorBody() != null) {
                            val gson = Gson()
                            val type = object : TypeToken<MyErrorMessage>() {}.type
                            var errorResponse: MyErrorMessage? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)

                            if (errorResponse!!.errors != null) {
                                // Fail to Post
                                db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.errors
                                )

                            } else {
                                // Fail to Post
                                db!!.updateSurveyPost(jObj["unique_id"].asString, "2")
                                db!!.updateComplaintTransaction(
                                    jObj["unique_id"].asString,
                                    "2",
                                    errorResponse!!.message
                                )

                            }
                        } else {*/
                        // Fail to Post
                        db!!.updateVisitActivityPost(jObj["customer_visit_id"].asString, "2")
                        Utils.logData(
                            this@SyncData,
                            "Post Visit Activity Response fail ${response.errorBody()!!.toString()}"
                        )

                        // }
                    }
                    AppConstants.isVisitActivitySync = false
                    sendBroadcast(intent)
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    db!!.updateVisitActivityPost(jObj["customer_visit_id"].asString, "2")
                    AppConstants.isVisitActivitySync = false
                    if (error.message != null) {
                        Utils.logData(
                            this@SyncData,
                            "Post Visit Activity Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SyncData,
                            "Post Visit Activity API Fail"
                        )

                    }
                }
            })
    }
}