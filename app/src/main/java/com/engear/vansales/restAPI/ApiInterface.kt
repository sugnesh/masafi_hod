package com.engear.vansales.restAPI

import CusEditMonitoring
import com.engear.vansales.common.MyApplication
import com.engear.vansales.model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    @GET
    fun getAPICall(@Url url: String?): Call<JsonObject?>?

    @POST(MyApplication.SALESMAN_LOGIN)
    fun salesmanLogin(@Body body: MutableMap<String, String>?): Call<UserData>

    @POST(MyApplication.SALESMAN_AD_LOGIN)
    fun salesmanADLogin(@Body body: MutableMap<String, String>?): Call<UserData>

    @POST(MyApplication.LOGIN)
    fun supervisorLogin(@Body body: MutableMap<String, String>?): Call<SupervisorUserData>

    @POST(MyApplication.AUTH_LOGIN)
    fun checkerLogin(@Body body: MutableMap<String, String>?): Call<SupervisorUserData>

    @GET(MyApplication.GET_SUPERVISOR_DASH + "/{supervisorId}")
    fun getSupervisorDashboardData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("supervisorId") supervisorId: Int
    ): Call<SPSalesData>

    @FormUrlEncoded
    @POST(MyApplication.GET_ROUTE_CUSTOMER)
    fun getRouteCustomerList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body body: MutableMap<String, String>?
    ): Call<ResponseBody>

    @POST(MyApplication.PENDING_LOAD)
    fun getPendingLoad(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PLoadResponse>
    @POST(MyApplication.CHANGE_PASSWORD)
    fun changePassword(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @GET(MyApplication.GET_BRAND_LIST)
    fun getBrandList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<NodeBrand>

    @GET(MyApplication.GET_CATEGORY_LIST)
    fun getCategoryList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<NodeBrand>

    @POST(MyApplication.GET_ITEM)
    fun getAllItem(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<ItemData>

    @POST(MyApplication.GET_DATA_COLLECTION)
    fun getDatCollection(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<DataCollection>


    @GET(MyApplication.GET_UOM)
    fun getAllUOM(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<AllUOMData>

    @POST(MyApplication.GET_CUSTOMER)
    fun getAllCustomer(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<CustomerData>

    @POST(MyApplication.GET_ROUTE_OTP_SETTING)
    fun getOtpMaster(
        @Header("Authorization") token: String,
        @Header("X-Domain") domain: String,
        @Body requestBody: Map<String, String>
    ): Call<RouteSettingApi>

    @POST(MyApplication.GET_ROUTE_LIST_SALESMAN)
    fun getAllRouteSetting(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<RouteSettingData>

    @POST(MyApplication.GET_DELIVERY_CUSTOMER)
    fun getDeliveryCustomer(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<CustomerData>

    @POST(MyApplication.GET_CUSTOMER)
    fun getAllCustomerjson(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<ResponseBody>

    @GET(MyApplication.GET_MER_CUSTOMER + "/{salesmanId}")
    fun getMerchandiserCustomer(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesmanId") routeId: Int
    ): Call<CustomerData>

    @GET(MyApplication.GET_DELIVERY_CANCEL + "/{uuid}")
    fun getDeliveryCancel(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("uuid") routeId: String
    ): Call<JsonObject>

    @POST(MyApplication.GET_DELIVERY_POST_CANCEL)
    fun getPostDeliveryCancel(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @Multipart
    @POST(MyApplication.GET_DELIVERY_POST_CANCEL) // Change the endpoint as needed
    fun postDeliveryCancelFile(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Part file: MultipartBody.Part,
        @Part("uuId") uuId: RequestBody,
        @Part("delivery_id") delivery_id: RequestBody,
        @Part("cancel_comment") cancel_comment: RequestBody,
        @Part("delviery_number") delviery_number: RequestBody
    ): Call<JsonObject>

    @GET(MyApplication.GET_ROUTE_VANLIST)
    fun getVanRoute(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<VanTransferRouteListData>

    @POST(MyApplication.GET_VANTRANSFER_ITEM_LIST)
    fun getVanTransferItem(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<VanTransferItemListData>

    @GET(MyApplication.GET_JOURNEY_PLAN + "/{routeId}")
    fun getJourneyPlan(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("routeId") routeId: Int
    ): Call<JourneyPlanData>

    @GET(MyApplication.GET_CUSTOMER_BALANCE + "/{customerId}")
    fun getCustomerBalance(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("customerId") customerId: Int
    ): Call<CustomerBalanceData>

    @POST(MyApplication.GET_CUSTOMER_BLOCK)
    fun customerBlock(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<BlockData>

    @POST(MyApplication.GET_CUSTOMER_RETURN_ITEM)
    fun customerReturnItem(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<CustomerItemReturnData>

    @GET(MyApplication.GET_PROMOTION_GROUP)
    fun getPromotionGroupItem(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PromotionGroupData>

    @POST(MyApplication.GET_COUPON_LIST)
    fun getCouponList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<CouponData>

    @POST(MyApplication.SEND_GEO_APPROVAL)
    fun sendGeoApprovalRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<GeoApprovalData>

    @POST(MyApplication.LOAD_SEND_GEO_APPROVAL)
    fun sendLoadGeoApprovalRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<GeoApprovalData>

    @POST(MyApplication.SUPERVISOR_SEND_GEO_APPROVAL)
    fun sendSupervisorGeoApprovalRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<GeoApprovalData>

    @POST(MyApplication.CHECK_INVOICE_EOD)
    fun checkInvoicePost(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<EODData>

    @POST(MyApplication.CHECK_COLLECTION_EOD)
    fun checkCollectionPost(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<EODData>

    @POST(MyApplication.INVOICE_CANCEL_REQUEST)
    fun sendInvoiceCancel(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<InvoiceCancelData>

    @POST(MyApplication.APPROVE_GEO_REQUEST)
    fun acceptGeoApprovalRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<BlockData>

    @POST(MyApplication.SEND_OVERDUE_APPROVAL)
    fun sendOverDueApprovalRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<BlockData>

    @POST(MyApplication.APPROVE_OVERDUE_REQUEST)
    fun acceptOverDueApprovalRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<BlockData>

    @GET(MyApplication.GET_MER_JOURNEY_PLAN + "/{salesmanId}")
    fun getMerchandiserJourneyPlan(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesmanId") routeId: Int
    ): Call<JourneyPlanData>

    @GET(MyApplication.POST_RETURN_ACCEPT + "/{uuid}")
    fun getReturnAccept(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("uuid") routeId: String
    ): Call<JsonObject>

    @POST(MyApplication.POST_RETURN_POST_ACCEPT)
    fun getReturnPostAccept(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.POST_RETURN_CANCEL_ACCEPT + "/{uuid}")
    fun getReturnCanelPostAccept(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("uuid") routeId: String
    ): Call<JsonObject>

    @POST(MyApplication.POST_RETURN_PALLET)
    fun getReturnPalletPost(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.GET_PDP_DATA)
    fun getPDPData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PDPData>

    @POST(MyApplication.GET_PDP_DATA_ROUTE)
    fun getPDPRouteData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PDPData>

    @POST(MyApplication.GET_PDP_CUSTOMER_PRICE)
    fun getPDPCustomerPriceData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PriceMFData>

    @POST(MyApplication.GET_PDP_PARENT_PRICE)
    fun getPDPParentPriceData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PriceMFData>

    @POST(MyApplication.GET_PDP_CHANNEL_PRICE)
    fun getPDPChannelPriceData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PriceMFData>

    @GET(MyApplication.GET_PDP_SUB_CHANNEL_PRICE)
    fun getPDPSubChannelPriceData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PriceMFData>

    @POST(MyApplication.GET_GRV_DATA)
    fun getGRVData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<GRVDumpData>

    @POST(MyApplication.GET_PDP_DATA_PRICING_ROUTE)
    fun getPDPRoutePricingData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PDPPricingData>

    @POST(MyApplication.GET_PDP)
    fun getRoutePricing(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PricingData>

    @POST(MyApplication.GET_PDP)
    fun getRoutePromotion(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PromoData>

    @POST(MyApplication.GET_PDP)
    fun getRouteDiscount(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<DiscountData>

    @POST(MyApplication.POST_LOAD_REQUEST)
    fun postLoadRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.POST_ORDER)
    fun postOrder(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.POST_TRACKER)
    fun postTrack(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.POST_INVOICE)
    fun postInvoice(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.POST_CARDFAIL)
    fun postCardFail(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.POST_RETURN)
    fun postReturn(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.POST_DEBIT_NOTE)
    fun postDebitNote(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.POST_COLLECTION)
    fun postCollection(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.GET_LOAD_LIST)
    fun getLoadList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<LoadData>

    @POST(MyApplication.GET_SALESMAN_LOAD_LIST)
    fun getDeliveryLoadList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<LoadData>

    @GET(MyApplication.GET_ORDER_TYPE)
    fun getOrderType(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<OrderTypeData>

    @POST(MyApplication.SEND_ENDO_REUQEST)
    fun sendEndorsmentRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<EndoRequestData>

    @POST(MyApplication.GET_SALESMAN_DATA)
    fun getSalesmanData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<SalesmanRequestData>

    @POST(MyApplication.SEND_DEVIATION_REQUEST)
    fun sendDeviationRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.CREDIT_LIMIT_REQUEST)
    fun sendUnblockRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.HOLDOON_Delviery_REQUEST)
    fun holdDelivery(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>


    @POST
    fun getPaymentLink(
        @Url url: String,
        @Header("Authorization") authHeader: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.CUSTOMER_LOCATION_CAPTURE)
    fun captureLocation(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.CREDIT_LIMIT_REQUEST)
    fun sendInvoiceCancelRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.GET_NOTIFICATION)
    fun getNotificationList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<NotificationData>

    @GET(MyApplication.GET_LOAD_REQUEST_DETAIL + "/{uuId}")
    fun getLoadRequestDetail(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("uuId") salesmanId: String
    ): Call<LoadRequestData>

    @POST(MyApplication.GET_APPROVAL_NOTI_STATUS)
    fun getApprovalStatusNoti(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body payload: JsonObject
    ): Call<GeoApprovalStatusModel>

    @GET(MyApplication.GET_DELIVERY + "/{salesman_id}")
    fun getDelivery(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<DeliveryData>

    @POST(MyApplication.GET_DELIVERY_DATE)
    fun getDeliveryDate(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<DeliveryData>


    @POST(MyApplication.TOPUP_PAYMENT_BITCASH_LINK)
    fun sendPaymentBitCashLink(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PaymentLinkData>

    @POST(MyApplication.TOPUP_PAYMENT_LINK)
    fun sendPaymentLink(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PaymentLinkData>

    @GET(MyApplication.GET_RETURN + "/{salesman_id}")
    fun getReturnData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<CreditData>

    @GET(MyApplication.GET_CUSTOMER_ORACLE + "/{customer_code}")
    fun getCustomerOraclePrice(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("customer_code") customerCode: String
    ): Call<CreditData>

    @POST(MyApplication.GET_CUSTOMER_HOD_PRICE)
    fun getCustomerHODPrice(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<CustomerPriceData>

    @POST(MyApplication.GET_CUSTOMER_HOD_PROMOTION)
    fun getCustomerHODPromotion(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<CustomerPromotionData>

    @POST(MyApplication.GET_CUSTOMER_ADDRESS)
    fun getCustomerHAddress(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<CustomerHODAddress>

    @POST(MyApplication.GET_Pallet_RETURN)
    fun getPalletReturnData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<PalletReturnData>

    @GET(MyApplication.GET_WAREHOUSE_DETAIL + "/{route_id}/{depot_id}")
    fun getWarehouseDetail(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("route_id") depotId: Int,
        @Path("depot_id") routeId: Int
    ): Call<StockData>

    @GET(MyApplication.GET_ITEM_STOCK + "/{depot_id}/{item_Id}")
    fun getItemStock(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("depot_id") depotId: Int,
        @Path("item_Id") routeId: Int
    ): Call<StockData>

    @POST(MyApplication.GET_CHECKLIST_DAY)
    fun postCheckList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.LOAD_CONFIRM)
    fun postLoadConfirm(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.GET_VANTRANSFER_ITEM_ADD)
    fun postVanTransfer(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>


    @POST(MyApplication.GET_STAMP_UPLOAD)
    fun postStampUpload(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @GET(MyApplication.GET_OUTSTANDING_INVOICE + "/{route_id}")
    fun getOutStandingInvoice(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("route_id") routeId: Int
    ): Call<OutstandingData>

    @GET(MyApplication.GET_GROUP_COLLLECTION + "/{route_id}")
    fun getRoutePendingInvoice(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("route_id") routeId: Int
    ): Call<OutstandingData>

    @GET(MyApplication.GET_REASON)
    fun getReason(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<ReasonData>

    @POST(MyApplication.GET_RESTRICT_DATA)
    fun getSalesRestrictData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<SalesRestrictData>

    @POST(MyApplication.GET_TARGET_DATA)
    fun getSalesTargetData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<SalesTargetData>

    @GET(MyApplication.GET_INVOICE + "/{customer_id}")
    fun getCustomerInvoice(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("customer_id") customerId: Int
    ): Call<InvoiceData>

    @GET(MyApplication.GET_INVOICE_ITEM + "/{invoice_id}")
    fun getInvoiceItem(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("invoice_id") invoiceId: Int
    ): Call<InvoiceItemData>

    @POST(MyApplication.GET_BEGIN_DAY)
    fun postBeginDay(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.GET_SESSION_LIST)
    fun checkSessionList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<JsonObject>

    @POST(MyApplication.GET_END_DAY)
    fun postEndDay(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.GET_UNLOAD)
    fun postUnload(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_COMPLAINT)
    fun postComplaint(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_CAMPAIGN)
    fun postCampaign(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_COMPETITOR)
    fun postCompetitor(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @GET(MyApplication.GET_CUSTOMER_INVENTORY + "/{salesman_id}")
    fun getCustomerInventory(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<InventoryData>

    @GET(MyApplication.GET_CUSTOMER_DISTRIBUTION + "/{salesman_id}")
    fun getCustomerDistribution(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<DistributionData>

    @GET(MyApplication.GET_CUSTOMER_PLANOGRAM + "/{salesman_id}")
    fun getCustomerPlanogram(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<PlanogramData>

    @GET(MyApplication.GET_CUSTOMER_ASSETS + "/{salesman_id}")
    fun getCustomerAssets(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<AsstesData>

    @GET(MyApplication.GET_DISTRIBUTION_SURVEY + "/{salesman_id}")
    fun getDistributionSurvey(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<ToolSurveyData>

    @GET(MyApplication.GET_CUSTOMER_EDETAILS + "/{customer_code}")
    fun getCustomerEDetails(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("customer_code") customer_code: String,
    ): Call<CusEditMonitoring>

    @POST(MyApplication.GET_CUSTOMER_EDETAILS + "/{customer_code}")
    fun saveCustomerEDetails(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("customer_code") customer_code: String,
        @Body requestBody: RequestBody
    ): Call<ResponseBody>

    @GET(MyApplication.GET_CONSUMER_SURVEY + "/{salesman_id}")
    fun getConsumerSurvey(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<ConsumerSurveyData>

    @GET(MyApplication.GET_ACTIVITY_PROFILE + "/{salesman_id}")
    fun getMerchandiserActivity(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<ActivityProfileData>

    @GET(MyApplication.GET_CUSTOMER_PROFILE + "/{salesman_id}")
    fun getMerchandiserCustomerActivity(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<ActivityCustProfileData>

    @GET(MyApplication.GET_CUSTOMER_PORTFOLIO + "/{salesman_id}")
    fun getCustomerPortfolio(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<PortfolioData>

    @GET(MyApplication.GET_ROUTE_CUSTOMER_PORTFOLIO + "/{route_id}")
    fun getRouteCustomerPortfolio(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("route_id") salesmanId: Int
    ): Call<RoutePortfolioData>

    @GET(MyApplication.GET_MTD + "/{salesman_id}")
    fun enableMtdApi(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<MTDdata>

    @POST(MyApplication.GET_ROUTE_PORTFOLIO)
    fun getRouteItemPortfolio(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<RouteItemData>

    @GET(MyApplication.GET_SENSORY_SURVEY)
    fun getSensorySurvey(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<SensorySurveyData>

    @GET(MyApplication.GET_PRODUCT_CATELOG)
    fun getProductCatelog(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<ProductCatelogData>

    @GET(MyApplication.GET_PROMOTIONAL_LIST)
    fun getPromotionalList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PromotionData>

    @GET(MyApplication.GET_ASSET_SURVEY + "/{salesman_id}")
    fun getAssetSurvey(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("salesman_id") salesmanId: Int
    ): Call<AssetSurveyData>

    @POST(MyApplication.ADD_INVENTORY)
    fun postInventory(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_TOOL_STOCK)
    fun postStock(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_TOOL_IMAGE)
    fun postStockImage(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_PLANOGRAM)
    fun postPlanogram(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_ASSETS)
    fun postAssets(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_SURVEY)
    fun postSurvey(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_PROMOTIONAL)
    fun postPromotional(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>


    @POST(MyApplication.ADD_CUSTOMER)
    fun addCustomer(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<ResponseBody>


    @POST(MyApplication.ADD_VISIT)
    fun postVisit(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @POST(MyApplication.ADD_VISIT_ACTIVITY)
    fun postVisitActivity(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<JsonObject>

    @GET(MyApplication.GET_ROUTE_LIST)
    fun getRouteList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<SupervisorRoute>

    @GET(MyApplication.GET_SUPERVISOR_ROUTE_LIST)
    fun getSupervisorRouteList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("supervisor_id") id: String
    ): Call<SupervisorRoute>


    @GET(MyApplication.GET_LOAD_QUANITY_LIST)
    fun getLoadQunatity(
        @Header("Authorization") auth: String,
        @Query("route_id") route_id: String
    ): Call<LoadQuantity>

    @GET(MyApplication.GET_INVOICE_VISIT_DETAILS)
    fun getInvoiceVisitItemEndrose(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("route_id") route_id: String,
        @Query("type") type: String

    ): Call<InvoiceEndrose>

    @GET(MyApplication.GET_INVOICE_VISIT_DETAILS)
    fun getInvoiceVisitItemCall(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("route_id") route_id: String,
        @Query("type") type: String

    ): Call<CallEndrose>

    @GET(MyApplication.GET_SUPERVISOR_SALE_ORDER_CREDIT_AMOUNT)
    fun getSaleOrderCreditAmount(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("route_id") route_id: String
    ): Call<SaleOrderReturnAmount>

    @POST(MyApplication.ENDROSEMENT_REQUEST)
    fun getEndrosementRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<EndroseRequest>

    @GET(MyApplication.GET_INVOICE_VISIT_DETAILS)
    fun getInvoiceVisitItemCarryOver(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("route_id") route_id: String,
        @Query("type") type: String

    ): Call<CarryOverEndrose>

    @GET(MyApplication.GET_INVOICE_VISIT_DETAILS)
    fun getInvoiceVisitItemDetails(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("route_id") route_id: String,
        @Query("type") type: String

    ): Call<FocEndrose>

    @GET(MyApplication.GET_ORDER_INVOICE_CREDIT)
    fun getOrderInvoiceCreditDetails(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("route_id") route_id: String,
        @Query("type") type: String
    ): Call<SaleRouteMonitoring>

    @GET(MyApplication.GET_TOTAL_SALES)
    fun getTotalSales(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("user_id") user_id: String,
        @Query("role_id") role_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String
    ): Call<SaleMonitoring>

    @GET(MyApplication.GET_TOTAL_RETURN)
    fun getTotalReturn(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("user_id") user_id: String,
        @Query("role_id") role_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String
    ): Call<SaleMonitoring>

    @GET(MyApplication.GET_ITEM_SALES)
    fun getItemWiseSales(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("user_id") user_id: String,
        @Query("role_id") role_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String
    ): Call<ItemMonitoring>

    @GET(MyApplication.GET_CATEGORY_SALES)
    fun getCategoryWiseSales(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("user_id") user_id: String,
        @Query("role_id") role_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String
    ): Call<CategoryMonitoring>

    @GET(MyApplication.GET_BRAND_SALES)
    fun getBrandWiseSales(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("user_id") user_id: String,
        @Query("role_id") role_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String
    ): Call<BrandMonitoring>

    @GET(MyApplication.GET_LOADED_ITEM)
    fun getLoadedItem(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("user_id") user_id: String,
        @Query("role_id") role_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String
    ): Call<ItemLoadSPMonitoring>

    @GET(MyApplication.GET_UNLOADED_ITEM)
    fun getUnLoadedItem(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("user_id") user_id: String,
        @Query("role_id") role_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String
    ): Call<ItemSPMonitoring>

    @GET(MyApplication.GET_SLOW_ITEM)
    fun getSlowMoveItem(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("user_id") user_id: String,
        @Query("role_id") role_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String
    ): Call<ItemMonitoring>

    @GET(MyApplication.GET_FAST_ITEM)
    fun getFastMoveItem(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("user_id") user_id: String,
        @Query("role_id") role_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String
    ): Call<ItemMonitoring>

    @GET(MyApplication.GET_ORDER_INVOICE_CREDIT)
    fun getOrderDetails(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("route_id") route_id: String,
        @Query("type") type: String
    ): Call<OrderRouteMonitoring>

    @GET(MyApplication.GET_ORDER_INVOICE_CREDIT)
    fun getVisitDetail(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("route_id") route_id: String,
        @Query("type") type: String
    ): Call<VisitData>

    @GET(MyApplication.GET_ORDER_INVOICE_CREDIT)

    fun getCreditDetails(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("route_id") route_id: String,
        @Query("type") type: String
    ): Call<CreditRouteMonitoring>


    @POST(MyApplication.GET_CUSTOMER_LIST)
    fun getCustomerList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String

    ): Call<NewCustomer>

    @POST(MyApplication.GET_DAILY_FEILD)
    fun postDailyFeild(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: DailyFeildRequestModel?
    ): Call<ResponseBody>


    @GET(MyApplication.APPROVE_REQUEST + "/{id}")
    fun approveRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("id") id: String
    ): Call<ResponseBody>

    @GET(MyApplication.REJECT_REQUEST + "/{id}")
    fun rejectRequest(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("id") id: String
    ): Call<ResponseBody>


    @POST(MyApplication.REQUEST_ENDROSSE)
    fun requestEndross(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<ResponseBody>

    @Multipart
    @POST(MyApplication.LOG_UPLOAD)
    fun uploadLog(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Part("salesman_code") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<JsonObject>

    @POST(MyApplication.INVOICE + "/{invoice_id}/" + MyApplication.INVOICE_REASON)
    fun PostReason(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("invoice_id") invoiceId: String,
        @Body data: JsonObject?

    ): Call<ResponseBody>

    @POST(MyApplication.INVOICE + "/{invoice_id}/" + MyApplication.INVOICE_CANCEL)
    fun postInvoiceCancel(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("invoice_id") invoiceId: Int,
        @Body data: JsonObject?
    ): Call<ResponseBody>

    @POST(MyApplication.GET_ROUTE_PICKINGLIST)
    fun getPickingRoute(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PickingRouteListData>

    @POST(MyApplication.GET_PICKER_LOAD_LIST)
    fun getPickerLoadList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PickerLoadData>

    @POST(MyApplication.GET_PICKER_ASSIGNED_LIST)
    fun getAssignedPickerLoadList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PickerLoadData>

    @POST(MyApplication.GET_PICKER_ASSIGNED)
    fun assignedPicker(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<AssignData>

    @POST(MyApplication.GET_PICKER_UPDATE_LOAD)
    fun PostLoadEdit(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?

    ): Call<JsonObject>

    @POST(MyApplication.GET_CHECKER_LOAD_LIST)
    fun getCheckerLoadList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PickerLoadData>

    @POST(MyApplication.GET_CHECKER_ASSIGNED_LIST)
    fun getAssignedCheckerLoadList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PickerLoadData>

    @POST(MyApplication.GET_CHECKER_ASSIGNED)
    fun assignedChecker(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<AssignData>

    @POST(MyApplication.GET_CHECKER_UPDATE_LOAD)
    fun PostCheckerLoadEdit(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?

    ): Call<JsonObject>


    @POST(MyApplication.GET_LOADER_LOAD_LIST)
    fun getLoaderLoadList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PickerLoadData>

    @POST(MyApplication.GET_LOADER_ASSIGNED_LIST)
    fun getAssignedLoaderLoadList(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PickerLoadData>

    @POST(MyApplication.GET_LOADER_ASSIGNED)
    fun assignedLoader(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body data: JsonObject?
    ): Call<AssignData>

    @GET(MyApplication.GET_LOADER_CONFIRM + "/{id}")
    fun loaderLoadConfirm(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Path("id") id: String
    ): Call<JsonObject>

    @GET(MyApplication.GET_CHANNEL_LIST)
    fun getNewCusChannelResponse(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<ChannelListResponse>

    @GET(MyApplication.GET_TRADE_SUB_CHANNEL_LIST)
    fun getCusTradeSubResponse(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<ChannelListResponse>

    @GET(MyApplication.GET_TRADE_SEC_SUB_CHANNEL_LIST)
    fun getCusTradeSubSecResponse(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<SubSecTradeChannelResponse>

    @GET(MyApplication.GET_PAYMENT_TERM_LIST)
    fun getPaymentTermListResponse(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String
    ): Call<PaymentTermListResponse>

    @POST(MyApplication.GET_LANDMARK_LIST)
    fun getLandmarkListResponse(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body requestBody: JsonObject
    ): Call<LandmarkListResponse>

    @POST(MyApplication.GET_REGION_LIST)
    fun getRegionListResponse(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body requestBody: JsonObject
    ): Call<RegionResponse>

    @POST(MyApplication.SAVE_ADD_NEW_CUSTOMER)
    fun saveNewCustomerFormData(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Body requestBody: RequestBody
    ): Call<CustomerResponseData>

    @POST(MyApplication.GET_TRADE_SUB_CHANNEL_LIST)
    fun getCusTradeSubResponse(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("channel_id") channelId: Int
    ): Call<SubChannelListResponse>

    @POST(MyApplication.GET_DISCOUNT_CARD_LIST)
    fun getDiscountCardResponse(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("sub_channel_id") channelId: Int
    ): Call<DiscountCardListResponse>

    //    +"?salesman_id={id}"
    @POST(MyApplication.SALESMAN_APPROVAL_DETAILS)
    fun getUsers(
        @Header("Authorization") auth: String,
        @Query("salesman_id") type: String

    ): Call<ApprovalRes>


    @POST(MyApplication.SALESMAN_HIRARCHY_DETAIL)
    fun getApprovalHistory(
        @Header("Authorization") auth: String,
        @Header("x-domain") domain: String,
        @Query("object_id") type: String

    ): Call<Hirearchytabledata>

    @POST(MyApplication.ICASH_DETAIL)
    fun getICashDetail(
        @Header("Authorization") auth: String,
        @Body requestBody: JsonObject,
    ): Call<IcashDetail>

    @POST(MyApplication.ONBALANCE_DETAIL)
    fun getOnAccountBalanceDetail(
        @Header("Authorization") auth: String,
        @Body requestBody: JsonObject,
    ): Call<IcashDetail>

    @POST(MyApplication.ONBALANCE_DISPENCER)
    fun getDispenserBalanceDetail(
        @Header("Authorization") auth: String,
        @Body requestBody: JsonObject,
    ): Call<IcashDetail>

    @POST(MyApplication.ICASH_TOPUP_POST)
    fun postTopup(
        @Header("Authorization") auth: String,
        @Body requestBody: JsonObject,
    ): Call<JsonObject>

    @POST(MyApplication.ONBALACE_REDUM_POST)
    fun postBalanceRedium(
        @Header("Authorization") auth: String,
        @Body requestBody: JsonObject,
    ): Call<JsonObject>

    @POST(MyApplication.ONDISPENSERBALACE_REDUM_POST)
    fun postDispenserBalanceRedium(
        @Header("Authorization") auth: String,
        @Body requestBody: JsonObject,
    ): Call<JsonObject>

    @POST(MyApplication.ICASH_BONUS)
    fun getICashBonus(
        @Header("Authorization") auth: String
    ): Call<IcashBounsl>
}