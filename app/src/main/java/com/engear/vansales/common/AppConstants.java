package com.engear.vansales.common;

public class AppConstants {

    public static String Accuracy = "0";
    public static String Latitude = "25.0000";
    public static String Longitude = "72.000";
    public static int countNoti = 0;
    //Static keys to store in shared preference due to global usage during posting
    public static final String SALESMAN = "salesman_data";
    public static final String SALESMANID = "salesman_id";
    public static final String SALESMANINO = "salesman_no";
    public static final String SALESMANNAME = "salesman_name";
    public static final String ISLOGIN = "isLogin";
    public static final String TOKEN = "token";
    public static final String FIREBASE_TOKEN = "firebase_token";
    public static final String LAST_VISITED_SEQ = "last_seq";
    public static final String CARD_FAIL = "card_fail";
    public static final String TOTAL_VISITED_CUSTOMER_SEQ = "total_visited_seq";
    public static final String VAN_CODE = "van_code";
    public static final String VAN_ID = "van_id";
    public static final String LAST_ODOMETER = "last_reading";
    public static final String START_ODOMETER = "start_reading";
    public static final String IS_DATA_SYNCING = "isDataSync";
    public static final String IS_LOAD_VERIFY = "isLoad_verify";
    public static final String IS_STARTDAY = "isStartDay";
    public static final String IS_ENDDAY = "isEndDay";
    public static final String LOCUS_PASSWORD = "locus_password";

    public static final String LOCUS_TRACKING = "locus_tracking";

    public static final String IS_AUDIT_STOCK = "isAuditStock";
    public static final String IS_ENDO_REQUEST = "isEndoRequest";
    public static final String IS_ENDO_REQUEST_APPROVE = "isEndoRequestApprove";
    public static final String IS_BADCAPTURE = "isBadReturn";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String ROUTEID = "routeId";
    public static final String ROUTENAME = "routeName";
    public static final String DRIVERNAME = "driverName";
    public static final String SUPERVISOR_ID = "supervisor_id";

    public static final String DAY_START = "day_start";

    //Warehouse
    public static final String WAREHOUSE_LAT = "warehousee_lat";
    public static final String WAREHOUSE_LONG = "warehousee_lng";
    public static final String WAREHOUSE_RADIUS = "warehousee_radius";

    public static final String SALESMENCURRENTRADIUS = "salesmen_radius";

    //Salesmen rights enable or disable
    public static final String SALESENABLE = "sales_enable";
    public static final String RETURNENABLE = "return_enable";
    public static final String COLLECTIONENABLE = "collection_enable";
    public static final String PRINTENABLE = "print_enable";

    //Routing
    public static final String VANSTOCKPRINTENABLE = "vanstock_enable";
    public static final String UNSCHEDULEPRINTENABLE = "unschedule_enable";
    public static final String CUSTOMERSKIPENABLE = "customerskip_enable";

    public static final String CUSTOMERORIGINALSKIPENABLE = "customerskip_original_enable";
    public static final String CUSTOMERPRINTENABLE = "customer_print_enable";

    public static final String FORCECHECKINENABLE = "force_checkin_enable";

    public static final String FORCECHECKOUTENABLE = "force_checkout_enable";

    public static final String NOTRANCETIONENABLE = "notransection_enable";

    public static final String JCPCUSTOMERENABLE = "jcp_enable";

    public static final String INVOICECANCELENABLE = "invoicecancel_enable";

    public static final String TOTALSEQCUSTOMER = "total_seq_cust";


    public static final String ASSOCIATED_ROUTEID = "asso_routeId";
    public static final String DEPOTID = "depotId";
    public static final String DEPOTNAME = "depotName";
    public static final String DEPOTTIN = "depotTIN";
    public static final String DEPOTVILLAGE = "depotVillage";
    public static final String DEPOTCITY = "depotCity";
    public static final String DEPOTSTREET = "depotStreet";
    public static final String STARTTIME = "startTime";
    public static final String STARTDATE = "startDate";
    public static final String ENDTIME = "endTime";
    public static final String DEPOTPHONE = "depotPhone";
    public static final String AGENTID = "agentId";
    public static final String ROLE = "role";
    public static final String TYPE = "type";
    public static final String CURRENCY = "currency";
    public static final String CURRENCY_DECIMAL = "currency_decimal";
    public static final String CURRENCY_FORMATE = "currency_formate";
    public static final String ITEM_DECIMAL = "itemDecimal";
    public static final int REQUEST_BARCODE = 2;
    public static final String INVOICE_LAST = "Invoice_LastNo";
    public static final String ORDER_LAST = "Order_LastNo";
    public static final String COLLECTION_LAST = "Collection_LastNo";
    public static final String RETURN_LAST = "Retrun_LastNo";
    public static final String LOAD_LAST = "Load_Request_LastNo";
    public static final String UNLOAD_LAST = "Unload_LastNo";
    public static final String CUSTOMER_LAST = "Customer_LastNo";
    public static final String EXCHANGE_LAST = "Exchange_LastNO";
    public static final String SALE_TARGET = "sale_target";
    public static final String ACHIVE_TARGET = "achive_target";
    public static final String IS_SALE_POSTED = "isSalePost";
    public static final String OLD_PWD = "old_password";
    public static final String NEW_PWD = "new_password";


    public static final String CUSTOMER_CREDIT = "cust_credit";

    public static final String CUSTOMER_AVAIL_BALANCE = "cust_balance";
    public static int COUNT = 2;


    //Printing
    public static final String REQUEST = "REQUEST";
    public static final String LOAD_SUMMARY_REQUEST = "LoadSummaryRequest";
    public static final String VAN_STOCK = "vanstock";
    public static final String SALES_INVOICE = "Invoice";
    public static final String STAMP_INVOICE = "Stamp_Invoice";
    public static final String DELIVERY_CANCEL = "Delviery_CC";
    public static final String INVOICE_CANCEL = "Invoice_CC";
    public static final String LOAD = "Load";
    public static final String ORDER = "order";
    public static final String COLLECTION = "collection";

    public static final String SOA_COLLECTION = "soa_collection";
    public static final String RETURN = "return";
    public static final String RETURN_REQUEST = "return_request";
    public static final String PALLET_RETURN_REQUEST = "pallet_return_request";

    public static final String RETURN_PALLET_REQUEST = "return_pallet_request";
    public static final String RETURN_CANCEL_REQUEST = "return_cancel_request";
    public static final String LOAD_REQUEST = "load_request";
    public static final String LOAD_VARIANCE_REQUEST = "load_variance_request";

    public static final String UNLOAD_STOCK_AUDIT = "unload_stock_audit";
    public static final String UNLOAD_STOCK_AUDIT_VARIANCE = "unload_stock_audit_variance";
    public static final String UNLOAD_REQUEST = "unload_request";

    public static final String UNLOAD_BAD_ITEM = "unload_request_bad";
    public static final String UNLOAD_VAN_EXPIRY_REQUEST = "unload_van_expiry_request";
    public static final String UNLOAD_VARIANCE_REQUEST = "unload_variance_request";
    public static final String UNLOAD_VAN_RETURN_REQUEST = "unload_van_retunr_request";
    public static final String UNLOAD_DAMAGED_REQUEST = "unload_damaged_request";
    public static final String UNLOAD_EXPIRY_REQUEST = "unload_expiry_request";
    public static final String VISIT_REQUEST = "visit_request";
    public static final String SALES_SUMMARY = "sales_summary";
    public static final String DEPOSITE_SUMMARY = "deposite_summary";
    public static final String DAILY_OTHER_SUMMARY = "daily_other_summary";
    public static final String DAILY_RTC_SUMMARY = "daily_rtc_summary";
    public static final String EXCHANGE = "exchange";
    public static final String DELIVERY_REQUEST = "delivery_delete";
    public static final String CUSTOMER_REQUEST = "add_customer";
    public static final String END_INVENTORY = "end_inventory";
    public static final String SALES_POST = "sale_post_request";
    public static final String COMPLAIN_POST = "complaint_post";
    public static final String SUGGESTION_POST = "suggestion_post";
    public static final String CAMPAIGN_POST = "campaign_post";
    public static final String COMPETITOR_POST = "competitor_post";
    public static final String INVENTORY_POST = "inventory_post";
    public static final String TOOL_POST = "tool_post";
    public static final String TOOL_IMAGE_POST = "tool_image_post";
    public static final String PLANOGRAM_POST = "planogram_post";
    public static final String ASSETS_POST = "assets_post";
    public static final String SURVEY_POST = "survey_post";
    public static final String PROMOTIONAL_POST = "promotional_post";
    public static final String VISIT_POST = "visit_post";
    public static final String VISIT_ACTIVITY_POST = "visit_activity_post";

    //Transaction Types
    public static final String LoadConfirmation_TR = "LCON";

    //Connectivity Messages
    public static final String WIFI_CONNECTED = "Wifi Enabled";
    public static final String MOBILE_DATA_CONNECTED = "Mobile Data Enabled";
    public static final String NO_CONNECTION = "Not connected to Internet";

    //Collection Type
    public static final String COLLECTION_INVOICE = "INVOICE";
    public static final String COLLECTION_RETURN = "RETURN";
    public static final String COLLECTION_INVOICE_CASH = "INVOICE_CASH";
    public static final String COLLECTION_DELIVERY = "DELIVERY";

    //is API Sync
    public static boolean isDelvierySync = false;
    public static boolean isInvoiceSync = false;
    public static boolean isCardFailSync = false;
    public static boolean isInvoiceStampSync = false;
    public static boolean isLoadSync = false;
    public static boolean isOrderSync = false;

    public static boolean isTrackSync = false;
    public static boolean isCollectionSync = false;
    public static boolean isReturnSync = false;
    public static boolean isReturnRequestSync = false;
    public static boolean isReturnCancelRequestSync = false;
    public static boolean isReturnPalletRequestSync = false;
    public static boolean isLoadRequestSync = false;
    public static boolean isLoadVarianceRequestSync = false;
    public static boolean isUnloadRequestSync = false;
    public static boolean isUnloadExpiryRequestSync = false;
    public static boolean isUnloadVarianceRequestSync = false;
    public static boolean isUnloadVanRetunrRequestSync = false;
    public static boolean isVisitRequestSync = false;
    public static boolean isDeleteRequestSync = false;
    public static boolean isAddRequestSync = false;
    public static boolean isStockSync = false;
    public static boolean isComplaintSync = false;
    public static boolean isSuggestionSync = false;
    public static boolean isExpiryItemSync = false;
    public static boolean isSurveyPost = false;
    public static boolean isDamagedSync = false;
    public static boolean isPlanogramSync = false;
    public static boolean isCompititorSync = false;
    public static boolean isCampaignSync = false;
    public static boolean isPromotionalSync = false;
    public static boolean isInventoryPost = false;
    public static boolean isAssetsSync = false;
    public static boolean isSensoryPost = false;
    public static boolean isConsumerPost = false;
    public static boolean isAssetsPost = false;
    public static boolean isDisImageSync = false;
    public static boolean isVisitSync = false;
    public static boolean isVisitActivitySync = false;
    public static boolean isSalePostRequestSync = false;


    public static class TRANSACTION_TYPES {
        public static final String TT_LOAD_CONF = "tt_load_conf";
        public static final String TT_STOCK_CAP = "tt_stock_cap";
        public static final String TT_SALES_CREATED = "tt_sales_created";
        public static final String TT_DELIVERY_CANCEL = "tt_dd_cancel";
        public static final String TT_INVOICE_CANCEL = "tt_inv_cancel";
        public static final String TT_ADD_CUSTOEMR_CREATED = "tt_cust_created";
        public static final String TT_CREDIT_COLLECTION = "tt_credit_collection";
        public static final String TT_CASH_COLLECTION = "tt_cash_collection";
        public static final String TT_ICCASH_COLLECTION = "tt_icash_collection";
        public static final String TT_CARD_FAIL_COLLECTION = "tt_card_fail_collection";
        public static final String TT_ONBALANCECASH_COLLECTION = "tt_onBalancecash_collection";
        public static final String TT_ONDISPENSERECASH_COLLECTION = "tt_onDispensercash_collection";
        public static final String TT_PR_COLLECTION = "tt_pr_collection";
        public static final String TT_STAMP_INVOICE = "tt_stamp_invoice";
        public static final String TT_PAYMENT_BY_CASH = "tt_payment_by_cash";
        public static final String TT_PAYMENT_BY_CHEQUE = "tt_payment_by_cheque";
        public static final String TT_PAYMENT_BY_CARD = "tt_payment_by_card";
        public static final String TT_CUST_CREATED = "tt_cust_created";
        public static final String TT_UNLOAD = "tt_unload";
        public static final String TT_LOAD_VARIANCE = "tt_load_varieance";
        public static final String TT_UNLOAD_EXPIRY = "tt_unload_expiry";
        public static final String TT_UNLOAD_VARIANCE = "tt_unload_variance";
        public static final String TT_UNLOAD_VAN_RETURN = "tt_unload_van_return";
        public static final String TT_OREDER_CREATED = "tt_order_created";
        public static final String TT_RETURN_CREATED = "tt_return_created";
        public static final String TT_RETURN_REQUEST = "tt_return_request";
        public static final String TT_END_DAY = "tt_end_day";
        public static final String TT_LOAD_CREATE = "tt_load_create";
        public static final String TT_SAP_LOAD_CREATE = "tt_SAP_load_create";
        public static final String TT_EXCHANGE_CREATED = "tt_exchange_created";
        public static final String TT_COMPLAINT_FEEDBACK = "tt_complaint";
        public static final String TT_COMPAIGN_FEEDBACK = "tt_compaign";
        public static final String TT_PLANOGRAM = "tt_planogram";
        public static final String TT_COMPATITOR = "tt_compatitor";
        public static final String TT_PRAMOTION = "tt_pramotion";
        public static final String TT_INVENTORY = "tt_inventory";
        public static final String TT_DISPLAY_TOOL = "tt_displayTool";
        public static final String TT_PALLET_RETURN = "tt_pallet_return";
        public static final String TT_DISPLAY_TOOL_IMAGE = "tt_displayToolImage";
        public static final String TT_ASSETS = "tt_assets";
        public static final String TT_CONS_SURVEY = "tt_consumer_servey";
        public static final String TT_SENSURY_SURVEY = "tt_sensury_servey";
        public static final String TT_ASSETS_SURVEY = "tt_assets_servey";
        public static final String TT_DISTRIBUTION_SURVEY = "tt_distribution_servey";
        public static final String TT_EXPIRYITEM = "tt_expiryItem";
        public static final String TT_DERITEM = "tt_derItem";
    }

}
