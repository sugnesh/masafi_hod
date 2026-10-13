package com.engear.vansales.common

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.digitsecure.dunncpplugin.handlers.DunnPay
import com.engear.vansales.R
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump


class MyApplication : Application(), LifecycleObserver {
    private val TAG = "MyApplication"
    private var mPreferenceManager: SharedPreferenceManager? = null
    var dialog: AlertDialog? = null
    var dunnPay: DunnPay? = null
    override fun onCreate() {
        /*   if (BuildConfig.DEBUG) {
               StrictMode.setVmPolicy(
                   StrictMode.VmPolicy.Builder()
                       .detectLeakedSqlLiteObjects()
                       .detectLeakedClosableObjects()
                       .penaltyLog()
                       .penaltyDeath()
                       .build()
               )
           }*/
        dunnPay = DunnPay.getInstance()
        super.onCreate()
        mInstance = this

//        val db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java, "vansales.db"
//        ).build()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        //set Custom fonts
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath(getString(R.string.regular_font))
                            .setFontAttrId(io.github.inflationx.calligraphy3.R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
        init()
    }

    private fun init() {
        mPreferenceManager = SharedPreferenceManager(this)
    }

    companion object {
        var mInstance: MyApplication? = null
        var isUpload: Boolean = false
        var decimalRate = 2

        //Collection Type
        val COLLECTION_INVOICE = "INVOICE"
        val INVOICE_COMPLETE = "C"
        val INVOICE_INCOMPLETE = "I"
        val ADD_INDICATOR = "S"
        val ADD_INDICATOR_CREDIT = "H"
        const val LOCUS_CLIENT = "hattan"

        const val PAYMENT_LINK_BASE_URL = "https://alke.masafi.com/ssotest/pl_mobiato/api.php"
        //const val PAYMENT_LINK_BASE_URL = "https://alke.masafi.com/ssotest/pl_mobiato/uat-api.php"

        const val ENVIRONMENT = "Production"
        const val BASE_URL = "https://pulse.masafi.com"

//        const val ENVIRONMENT = "Development"
//
//        const val BASE_URL = "https://mobidev.masafi.com"
//       // const val BASE_URL = "https://mobiuat.masafi.com"


        private const val COMMON_URL = "msfa-backend/public/api/"
        const val X_VANSALES_DOMAIN = "engear-msfa.com"


        //        const val ENVIRONMENT = "Production"
//        const val BASE_URL = "http://engear-msfa.com/"
//        private const val COMMON_URL = "msfa-backend/public/api/"
//        const val X_VANSALES_DOMAIN = "engear-msfa.com"
        const val X_DOMAIN = X_VANSALES_DOMAIN


        private const val AUTH_URL = "auth/"
        private const val ROUTE_URL = "route/"
        private const val ITEM_URL = "item/"
        private const val UOM_URL = "item-uom/"
        private const val CUSTOMER_URL = "customer/"
        private const val JOURNEY_PLAN_URL = "journey-plan/"
        private const val VANTRANSFER_URL = "vantransfer/"
        private const val ORDER_URL = "order/"
        private const val LOAD_REQUEST_URL = "loadrequest/"
        private const val INVOICE_URL = "invoice/"
        private const val RETURN_URL = "creditnotes/"
        private const val DEBIT_URL = "debit-notes/"
        private const val COLLECTION_URL = "collection/"
        private const val LOAD_URL = "salesman-load/"
        private const val ORDER_TYPE = "order-type/"
        private const val DELIVERY = "getdeliveries"
        private const val DELIVERY_DATE = "getdeliveries_date"
        private const val WAREHOUSE_DETAIL = "getwarehousedetail"
        private const val ITEM_STOCK = "getitemstock"
        private const val OUTSTANDING_INVOICE = "pending-invoice/list/"
        private const val GROUP_COLLECTION = "collection/routependinginvoice"
        private const val REASON = "reason-type/list"
        private const val CUSTOMER_INVOICE = "getcustomerinvoice"
        private const val INVOICE_ITEM = "getinvoiceitem"
        private const val BEGIN_DAY = "beginday/add"
        private const val END_DAY = "endday/add"
        private const val UNLOAD = "salesman-unload/add"
        private const val COMPLAINT = "complaint-feedback/add"
        private const val CAMPAIGN = "campaign-picture/add"
        private const val COMPETITOR = "competitor-info/add"
        private const val INVENTORY_LIST = "assign-inventory-by-customer-route"
        private const val INVENTORY_POST = "assign-inventory-post/add"
        private const val DISTRIBUTION_LIST = "distribution/customer-by-route"
        private const val DISTRIBUTION_POST = "distribution-all-in-one-item/add"
        private const val DISTRIBUTION_IMAGE_POST = "distribution-post-image/add"
        private const val PLANOGRAM_LIST = "planogram/merchandiser/customer"
        private const val PLANOGRAM_POST = "planogram-post/add"
        private const val ASSETS_LIST = "asset-tracking/merchandiser/customer"

        const val PENDING_LOAD = COMMON_URL + "check_eod_load"

        private const val ASSETS_POST = "asset-tracking-post/add"
        private const val TOOL_SURVEY = "distribution-survey/merchandiser"
        private const val SURVEY_POST = "survey-question-answer/add"
        private const val PROMOTIONAL_POST = "promotional-post/add"
        private const val CONSUMER_SURVEY = "consumer-survey/merchandiser"
        private const val SENSORY_SURVEY = "sensory-survey"
        private const val ASSET_SURVEY = "asset-tracking-survey/merchandiser"
        private const val VISIT_POST = "customer-visit/add"
        private const val VISIT_ACTIVITY_POST = "customer-activity/add"
        private const val MERCH_ACTIVITY_PROFILE = "salesman-activity-profile"
        private const val CUSTOMER_ACTIVITY_PROFILE = "activity-profile/customer"
        private const val CUSTOMER_PROTFOLIO_ITEM = "portfolio/customer"
        private const val COUNT_PER_MONTH = "salesman-total-count-per-month"
        private const val ROUTE_ITEM = "route-item-grouping-by-merchandiser"
        private const val REGION = "region/"
        private const val RESET_PWD = "salesman-change-password"
        private const val UPLOAD_FILE = "log-upload"
        private const val INVOICE_ = "invoice"
        private const val BRAND_LIST = "brand/list"
        private const val CATEGORY_LIST = "major-category/list"

        //customer
        const val ADD_CUSTOMER = COMMON_URL + "customer/add"

        //Login
        const val AUTH_LOGIN = COMMON_URL + AUTH_URL + "login"
        const val LOGIN = COMMON_URL + AUTH_URL + "supervisor-login"
        const val SALESMAN_LOGIN = COMMON_URL + AUTH_URL + "salesman-login"
        const val SALESMAN_AD_LOGIN = COMMON_URL + AUTH_URL + "salesman-ad-login"

        //Post Invoice
        const val CHANGE_PASSWORD = COMMON_URL + RESET_PWD

        //Post Log
        const val LOG_UPLOAD = COMMON_URL + UPLOAD_FILE

        const val INVOICE = COMMON_URL + INVOICE_
        const val INVOICE_CANCEL = "cancel"
        const val INVOICE_REASON = "reason"

        //Get Begin Day
        const val GET_CHECKLIST_DAY = COMMON_URL + "driver-check-list/add"

        //Route
        const val GET_ROUTE_CUSTOMER = COMMON_URL + ROUTE_URL + "customer"
        const val GET_ROUTE_LIST = COMMON_URL + ROUTE_URL + "list"
        const val GET_SUPERVISOR_ROUTE_LIST = COMMON_URL + "get-sp-route/" + "list"
        const val GET_SUPERVISOR_SALE_ORDER_CREDIT_AMOUNT =
            COMMON_URL + CUSTOMER_URL + "sales-order-credit-amount/" + "list"
        const val GET_LOAD_QUANITY_LIST = COMMON_URL + "load-quantity/list"
        const val GET_INVOICE_VISIT_DETAILS =
            COMMON_URL + CUSTOMER_URL + "invoice-visit-item/detail"
        const val GET_ORDER_INVOICE_CREDIT = COMMON_URL + CUSTOMER_URL + "order-invoice-credit/list"
        const val GET_CUSTOMER_LIST = COMMON_URL + CUSTOMER_URL + "list"
        const val GET_DAILY_FEILD = COMMON_URL + "daily-activity/add"
        const val APPROVE_REQUEST = COMMON_URL + "loadrequest/approve/"
        const val REJECT_REQUEST = COMMON_URL + "loadrequest/reject/"
        const val REQUEST_ENDROSSE = COMMON_URL + "session-endrossment/request-by-supervisor"
        const val ENDROSEMENT_REQUEST =
            COMMON_URL + "session-endrossment/request-list"

        //data collectionB
        const val GET_DATA_COLLECTION = COMMON_URL + "data-collection"

        //Item
        const val GET_ITEM = COMMON_URL + ITEM_URL + "list"

        //Get Brand
        const val GET_BRAND_LIST = COMMON_URL + BRAND_LIST

        //Get Category
        const val GET_CATEGORY_LIST = COMMON_URL + CATEGORY_LIST

        //Item
        const val GET_PRODUCT_CATELOG = COMMON_URL + ITEM_URL + "list?product_catalog=true"

        //Promotional
        const val GET_PROMOTIONAL_LIST = COMMON_URL + "promotional/mobile/list"

        const val GET_PROMOTION_GROUP = COMMON_URL + "promotion-group/list"

        const val GET_COUPON_LIST = COMMON_URL + "coupon/salesman_wise_list"

        //UOM
        const val GET_UOM = COMMON_URL + UOM_URL + "list"

        //Channel
        const val GET_CHANNEL = COMMON_URL + "get/channels"

        //REGION
        const val GET_REGION = COMMON_URL + REGION + "region/list"

        //SALE ORGANIZATION
        const val GET_SALE_ORG = COMMON_URL + "sales-organisation/list"

        //Customer
        const val GET_CUSTOMER = COMMON_URL + "route/customer_hod"

        const val GET_ROUTE_OTP_SETTING = COMMON_URL + "route/setting"

        //Customer
        const val GET_ROUTE_LIST_SALESMAN = COMMON_URL + "salesman-route/list"

        const val GET_DELIVERY_CUSTOMER = COMMON_URL + "getSalesmanDeliveryCustomer"

        //Customer
        const val GET_MER_CUSTOMER = COMMON_URL + "merchandiser/customer"

        //Customer
        const val GET_SUPERVISOR_DASH = COMMON_URL + "supervisor-salesman"

        //Customer
        const val GET_DELIVERY_CANCEL = COMMON_URL + "delivery/cancel"

        //Customer
        const val GET_DELIVERY_POST_CANCEL = COMMON_URL + "delivery-cancel"

        //Journey Plan
        const val GET_JOURNEY_PLAN = COMMON_URL + JOURNEY_PLAN_URL + "route"

        //Customer Balance
        const val GET_CUSTOMER_BALANCE = COMMON_URL + "customerbalance"

        //Journey Plan
        const val GET_CUSTOMER_BLOCK = COMMON_URL + "customer-block-type"

        //Return Item
        const val GET_CUSTOMER_RETURN_ITEM = COMMON_URL + "customer-data/list"

        //Journey Plan
        const val GET_MER_JOURNEY_PLAN = COMMON_URL + "journey-plan/merchandiser"

        //Route Van list
        const val GET_ROUTE_VANLIST = COMMON_URL + VANTRANSFER_URL + "routelist"

        //Route Van Item list
        const val GET_VANTRANSFER_ITEM_LIST = COMMON_URL + VANTRANSFER_URL + "routecurrentstock"

        //Route Van Item Add
        const val GET_VANTRANSFER_ITEM_ADD = COMMON_URL + VANTRANSFER_URL + "itemrequest"

        //Stamp Upload
        const val GET_STAMP_UPLOAD = COMMON_URL + "invoiceStamp"

        //Pricing
        const val GET_PDP_DATA = COMMON_URL + "pdp-mobile"

        //Pricing
        const val GET_PDP_DATA_ROUTE = COMMON_URL + "pdp-mobile-other"

        //Pricing
        const val GET_PDP_CUSTOMER_PRICE = COMMON_URL + "pdp-mobile-customer-price"

        const val GET_PDP_PARENT_PRICE = COMMON_URL + "pdp-mobile-perent-customer-price"

        const val GET_PDP_CHANNEL_PRICE = COMMON_URL + "pdp-mobile-channel-price"

        const val GET_PDP_SUB_CHANNEL_PRICE = COMMON_URL + "sub_channel_price"

        //Pricing
        const val GET_GRV_DATA = COMMON_URL + "grv/details"

        //Pricing
        const val GET_PDP_DATA_PRICING_ROUTE = COMMON_URL + "pdp-mobile-pricing"

        //Pricing
        const val GET_PDP = COMMON_URL + "pdp/mobile-by-route"

        //Post Order
        const val POST_ORDER = COMMON_URL + ORDER_URL + "add"

        //Post Order
        const val POST_TRACKER = COMMON_URL + "salesman-tracking-store"

        //Post Load Request
        const val POST_LOAD_REQUEST = COMMON_URL + LOAD_REQUEST_URL + "add"

        //Post Invoice
        const val POST_INVOICE = COMMON_URL + INVOICE_URL + "add"

        //Post Card
        const val POST_CARDFAIL = COMMON_URL + "store_payment_fails"

        //Post Return
        const val POST_RETURN = COMMON_URL + RETURN_URL + "add"

        //Journey Plan
        const val POST_RETURN_ACCEPT = COMMON_URL + "creditnotes/requested-accepted"

        //Journey Plan
        const val POST_RETURN_POST_ACCEPT = COMMON_URL + "creditnotes/update"

        //Journey Plan
        const val POST_RETURN_CANCEL_ACCEPT = COMMON_URL + "creditnotes/cancel"
        const val POST_RETURN_PALLET = COMMON_URL + "addReturnPallet"

        //Post Debit Note
        const val POST_DEBIT_NOTE = COMMON_URL + DEBIT_URL + "add"

        //Post Collection
        const val POST_COLLECTION = COMMON_URL + COLLECTION_URL + "add"

        //Load
        const val GET_LOAD_LIST = COMMON_URL + LOAD_URL + "loadlist"
        const val GET_SALESMAN_LOAD_LIST = COMMON_URL + LOAD_URL + "listbysalesman"
        const val LOAD_CONFIRM = COMMON_URL + LOAD_URL + "confirm"

        //Order Type
        const val GET_ORDER_TYPE = COMMON_URL + ORDER_TYPE + "list"

        //Order Type
        const val SEND_ENDO_REUQEST = COMMON_URL + "session-endrossment/request-by-salesman"

        //Order Type
        const val SEND_DEVIATION_REQUEST = COMMON_URL + "salesman-route-approval/add"

        //Order Type
        const val CREDIT_LIMIT_REQUEST = COMMON_URL + "salesman-approval-request-credit-limit"

        const val HOLD_Delviery_REQUEST = COMMON_URL + "delivery-update-date"

        const val RESCHUDLE_Delviery_REQUEST = COMMON_URL + "reschdule-order"

        const val HOLDOON_Delviery_REQUEST = COMMON_URL + "update-order-status-assigned-to-on-hold"

        const val CUSTOMER_LOCATION_CAPTURE = COMMON_URL + "customer-location"

        //Notification Type
        const val GET_NOTIFICATION = COMMON_URL + "notifications"

        //Notification Type
        const val GET_LOAD_REQUEST_DETAIL = COMMON_URL + "loadrequest/edit"

        const val GET_APPROVAL_NOTI_STATUS = COMMON_URL + "approval/list"

        //Delivery
        const val GET_DELIVERY = COMMON_URL + DELIVERY

        //Delivery
        const val GET_DELIVERY_DATE = COMMON_URL + DELIVERY_DATE

        const val TOPUP_PAYMENT_BITCASH_LINK = COMMON_URL + "bitcash_delivery_driver_message"

        const val TOPUP_PAYMENT_LINK = COMMON_URL + "delivery_driver_device_message"

        //Return
        const val GET_RETURN = COMMON_URL + "creditnotes/requested"

        const val GET_CUSTOMER_ORACLE = COMMON_URL + "post-price-customer-code-portal"

        //Return
        const val GET_Pallet_RETURN = COMMON_URL + "getCustomerPallet"

        const val GET_CUSTOMER_HOD_PRICE = COMMON_URL + "order/item-with-discount-new"

        const val GET_CUSTOMER_HOD_PROMOTION = COMMON_URL + "order/item-apply-promotion"

        const val GET_CUSTOMER_ADDRESS = COMMON_URL + "get_customer_address_based_id"

        //Warehouse Detail
        const val GET_WAREHOUSE_DETAIL = COMMON_URL + WAREHOUSE_DETAIL

        //Item Stock
        const val GET_ITEM_STOCK = COMMON_URL + ITEM_STOCK

        //Get OutStanding
        const val GET_OUTSTANDING_INVOICE = COMMON_URL + OUTSTANDING_INVOICE

        //Get OutStanding
        const val GET_GROUP_COLLLECTION = COMMON_URL + GROUP_COLLECTION

        //Get Reason
        const val GET_REASON = COMMON_URL + REASON

        //Get INVOICE
        const val GET_INVOICE = COMMON_URL + CUSTOMER_INVOICE

        //Get INVOICE ITEM
        const val GET_INVOICE_ITEM = COMMON_URL + INVOICE_ITEM

        //Get Begin Day
        const val GET_BEGIN_DAY = COMMON_URL + BEGIN_DAY

        //Get End Day
        const val GET_END_DAY = COMMON_URL + END_DAY

        //Get End Day
        const val GET_RESTRICT_DATA = COMMON_URL + "get-salesman-sales-restrict"

        const val GET_TARGET_DATA = COMMON_URL + "salestarget/RouteMonth"

        //Get End Day
        const val GET_SESSION_LIST = COMMON_URL + "session-endrossment/salesman-request-list"

        //Get UNLOAD
        const val GET_UNLOAD = COMMON_URL + UNLOAD

        //ADD Complaint
        const val ADD_COMPLAINT = COMMON_URL + COMPLAINT

        //Add Campaign
        const val ADD_CAMPAIGN = COMMON_URL + CAMPAIGN

        //Add Campaign
        const val ADD_COMPETITOR = COMMON_URL + COMPETITOR

        //Get Customer Inventory
        const val GET_CUSTOMER_INVENTORY = COMMON_URL + INVENTORY_LIST

        //Get Customer DIstribution
        const val GET_CUSTOMER_DISTRIBUTION = COMMON_URL + DISTRIBUTION_LIST

        //Add Inventory Post
        const val ADD_INVENTORY = COMMON_URL + INVENTORY_POST

        //Add Tool STOCK  Post
        const val ADD_TOOL_STOCK = COMMON_URL + DISTRIBUTION_POST

        //Add Tool IMAGE  Post
        const val ADD_TOOL_IMAGE = COMMON_URL + DISTRIBUTION_IMAGE_POST

        //Get Customer Planogram
        const val GET_CUSTOMER_PLANOGRAM = COMMON_URL + PLANOGRAM_LIST

        //Add Planogram  Post
        const val ADD_PLANOGRAM = COMMON_URL + PLANOGRAM_POST

        //Get Customer Assets
        const val GET_CUSTOMER_ASSETS = COMMON_URL + ASSETS_LIST

        //Add Assets  Post
        const val ADD_ASSETS = COMMON_URL + ASSETS_POST

        //Get Tool Survey
        const val GET_DISTRIBUTION_SURVEY = COMMON_URL + TOOL_SURVEY

        const val GET_CUSTOMER_EDETAILS = COMMON_URL + "customer/editcustomer_code"

        //Add Survey  Post
        const val ADD_SURVEY = COMMON_URL + SURVEY_POST

        //Add Promotional  Post
        const val ADD_PROMOTIONAL = COMMON_URL + PROMOTIONAL_POST

        //Get Consumer Survey
        const val GET_CONSUMER_SURVEY = COMMON_URL + CONSUMER_SURVEY

        //Get Sensory Survey
        const val GET_SENSORY_SURVEY = COMMON_URL + SENSORY_SURVEY

        //Get Assets Survey
        const val GET_ASSET_SURVEY = COMMON_URL + ASSET_SURVEY

        //Add Visit  Post
        const val ADD_VISIT = COMMON_URL + VISIT_POST

        //Add Visit ACtivity Post
        const val ADD_VISIT_ACTIVITY = COMMON_URL + VISIT_ACTIVITY_POST

        //Get Activity Profile
        const val GET_ACTIVITY_PROFILE = COMMON_URL + MERCH_ACTIVITY_PROFILE

        //Get Customer Activity Profile
        const val GET_CUSTOMER_PROFILE = COMMON_URL + CUSTOMER_ACTIVITY_PROFILE

        //Get Customer Portfolio Item
        const val GET_CUSTOMER_PORTFOLIO = COMMON_URL + CUSTOMER_PROTFOLIO_ITEM

        //Get Customer Portfolio Item
        const val GET_ROUTE_CUSTOMER_PORTFOLIO = COMMON_URL + "portfolio/customer-by-route"

        //Get MTD
        const val GET_MTD = COMMON_URL + COUNT_PER_MONTH

        //Get Portfolio Item
        const val GET_ROUTE_PORTFOLIO = COMMON_URL + ROUTE_ITEM

        const val SEND_GEO_APPROVAL = COMMON_URL + "salesman-geo-approval-request"

        const val LOAD_SEND_GEO_APPROVAL = COMMON_URL + "salesman-geo-approval-request-load"
        const val SUPERVISOR_SEND_GEO_APPROVAL = COMMON_URL + "salesman-geo-approval-request"
        const val CHECK_INVOICE_EOD = COMMON_URL + "invoice/check-eod"
        const val CHECK_COLLECTION_EOD = COMMON_URL + "collection/check-eod"

        const val INVOICE_CANCEL_REQUEST = COMMON_URL + "invoice/request-to-cancel"

        const val APPROVE_GEO_REQUEST = COMMON_URL + "supervisor-geo-approval-request"

        const val SEND_OVERDUE_APPROVAL = COMMON_URL + "salesman-overdue-limits-approval-request"

        const val APPROVE_OVERDUE_REQUEST =
            COMMON_URL + "supervisor-overdue-limits-approval-request"

        //Order Type
        const val GET_SALESMAN_DATA = COMMON_URL + "get-salesman-sales"

        //Order Type
        const val GET_TOTAL_SALES = COMMON_URL + "total-sales"

        //Order Type
        const val GET_TOTAL_RETURN = COMMON_URL + "returns"

        //Order Type
        const val GET_ITEM_SALES = COMMON_URL + "item-wise-sales"

        //Order Type
        const val GET_LOADED_ITEM = COMMON_URL + "loaded-item"

        //Order Type
        const val GET_UNLOADED_ITEM = COMMON_URL + "unload-item"

        //Order Type
        const val GET_SLOW_ITEM = COMMON_URL + "slow-moving-item"

        //Order Type
        const val GET_FAST_ITEM = COMMON_URL + "fast-moving-item"

        //Order Type
        const val GET_CATEGORY_SALES = COMMON_URL + "category-wise-sales"

        //Order Type
        const val GET_BRAND_SALES = COMMON_URL + "brand-wise-sales"

        //Picker Load
        const val GET_PICKER_LOAD_LIST = COMMON_URL + "load-request/picker/list"
        const val GET_PICKER_ASSIGNED_LIST = COMMON_URL + "load-request/picker/show-assigned"
        const val GET_PICKER_ASSIGNED = COMMON_URL + "load-request/picker/assign"
        const val GET_PICKER_UPDATE_LOAD = COMMON_URL + "load-request/picker/item-qty"

        //Checker Load
        const val GET_CHECKER_LOAD_LIST = COMMON_URL + "load-request/checker/list"
        const val GET_CHECKER_ASSIGNED_LIST = COMMON_URL + "load-request/checker/show-assigned"
        const val GET_CHECKER_ASSIGNED = COMMON_URL + "load-request/checker/assign"
        const val GET_CHECKER_UPDATE_LOAD = COMMON_URL + "load-request/checker/item-qty"

        //Loader Load
        const val GET_LOADER_LOAD_LIST = COMMON_URL + "load-request/loader/list"
        const val GET_LOADER_ASSIGNED_LIST = COMMON_URL + "load-request/loader/show-assigned"
        const val GET_LOADER_ASSIGNED = COMMON_URL + "load-request/loader/assign"
        const val GET_LOADER_CONFIRM = COMMON_URL + "loadrequest/generate"

        const val GET_ROUTE_PICKINGLIST = COMMON_URL + "route/list"

        const val GET_NEXT_COMING_CUSTOMER = BASE_URL + "/" + COMMON_URL + "get-next-comming-code"
        const val GET_CHANNEL_LIST = BASE_URL + "/" + COMMON_URL + "channel/list"

        //const val GET_TRADE_SUB_CHANNEL_LIST = BASE_URL + "/" + COMMON_URL + "customer-category/list"
        const val GET_TRADE_SEC_SUB_CHANNEL_LIST =
            BASE_URL + "/" + COMMON_URL + "SUBSecondarychannel/list"
        const val GET_PAYMENT_TERM_LIST = BASE_URL + "/" + COMMON_URL + "payment-term/list"
        const val GET_LANDMARK_LIST = BASE_URL + "/" + COMMON_URL + "landmarks/region-wise/list"
        const val GET_REGION_LIST = BASE_URL + "/" + COMMON_URL + "data-collection"
        const val SAVE_ADD_NEW_CUSTOMER = BASE_URL + "/" + COMMON_URL + "customer/add"
        const val GET_TRADE_SUB_CHANNEL_LIST = BASE_URL + "/" + COMMON_URL + "customer/subchannel"
        const val GET_DISCOUNT_CARD_LIST =
            BASE_URL + "/" + COMMON_URL + "card_types/sub_channel_wise_list"
        const val SALESMAN_APPROVAL_DETAILS =
            BASE_URL + "/" + COMMON_URL + "salesman-approval-details"
        const val SALESMAN_HIRARCHY_DETAIL = BASE_URL + "/" + COMMON_URL + "approval-history"

        const val ICASH_DETAIL = BASE_URL + "/" + COMMON_URL + "listing/icash/customer"
        const val ONBALANCE_DETAIL = BASE_URL + "/" + COMMON_URL + "listing/icash/customer_balance"
        const val ICASH_BONUS = BASE_URL + "/" + COMMON_URL + "list/icash/bonus"
        const val ICASH_TOPUP_POST = COMMON_URL + "icash/transactions"
        const val ONBALACE_REDUM_POST = COMMON_URL + "icash/balance_transactions"
        const val ONBALANCE_DISPENCER =
            BASE_URL + "/" + COMMON_URL + "customer-dispenser-order-balance"
        const val ONDISPENSERBALACE_REDUM_POST = COMMON_URL + "icash/dispenser_transactions"

        //Printing
        const val REQUEST = "REQUEST"
        const val LOAD_SUMMARY_REQUEST = "LoadSummaryRequest"
        const val LOAD_REQUEST = "LoadRequest"
        const val ORDER_REQUEST = "OrderRequest"
        const val SALES_INVOICE = "Invoice"
        const val DELIVERY_PRINT = "Delivery"
        const val COLLECTION = "Collection"
        const val DRIVER_COLLECTION = "driverCollection"
        const val UNLOAD_PRINT = "unload"
        const val DEPOSIT_REPORT = "depositreport"
        const val ROUTE_SALES = "routeSales"
        const val SALES_SUMMARY = "salessummary"
        const val BAD_RETURN_REPORT = "badreturnreport"
        const val VAN_STOCK = "vanstock"
        const val ENDING_SUMMERY = "endsummery"

        fun getInstance(): MyApplication? {
            return mInstance
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    fun showDialog() {
        dialog!!.show()
    }

    fun dismissDialog() {
        dialog!!.dismiss()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        Log.d(TAG, "onDestroy: stop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Log.d(TAG, "onDestroy: stop")
    }

}
