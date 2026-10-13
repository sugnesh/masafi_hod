package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RouteSettingApi(
    @SerializedName("status")
    var status: Boolean = false,
    @SerializedName("data")
    var data: RouteSettingData = RouteSettingData(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("errors")
    var errors: List<Any> = listOf(),
    @SerializedName("pagination")
    var pagination: List<Any> = listOf()
) : Serializable {
    data class RouteSettingData(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("uuid")
        var uuid: String = "",
        @SerializedName("organisation_id")
        var organisationId: Int = 0,
        @SerializedName("route_id")
        var routeId: Int = 0,
        @SerializedName("customer_skip_enable")
        var customerSkipEnable: Int = 0,
        @SerializedName("force_check_in_enable")
        var forceCheckInEnable: Int = 0,
        @SerializedName("force_check_out_enable")
        var forceCheckOutEnable: Int = 0,
        @SerializedName("good_sellable_returns_enable")
        var goodSellableReturnsEnable: Int = 0,
        @SerializedName("invoice_cancellation_void_enable")
        var invoiceCancellationVoidEnable: Int = 0,
        @SerializedName("customer_creation_enable")
        var customerCreationEnable: Int = 0,
        @SerializedName("van_to_van_transfer_enable")
        var vanToVanTransferEnable: Int = 0,
        @SerializedName("unscheduled_visits_enable")
        var unscheduledVisitsEnable: Int = 0,
        @SerializedName("no_transaction_enable")
        var noTransactionEnable: Int = 0,
        @SerializedName("jcp_customers_enable")
        var jcpCustomersEnable: Int = 0,
        @SerializedName("quality_returns_enable")
        var qualityReturnsEnable: Int = 0,
        @SerializedName("van_stock_print_enable")
        var vanStockPrintEnable: Int = 0,
        @SerializedName("prices_enable")
        var pricesEnable: Int = 0,
        @SerializedName("promotions_enable")
        var promotionsEnable: Int = 0,
        @SerializedName("asset_salesman")
        var assetSalesman: Int = 0,
        @SerializedName("sales_enable")
        var salesEnable: Int = 0,
        @SerializedName("return_enable")
        var returnEnable: Int = 0,
        @SerializedName("collection")
        var collection: Int = 0,
        @SerializedName("print")
        var print: Int = 0,
        @SerializedName("edit_qty_timeof_delivery")
        var editQtyTimeofDelivery: Int = 0,
        @SerializedName("samples_applicable")
        var samplesApplicable: Int = 0,
        @SerializedName("skipping_customers")
        var skippingCustomers: Int = 0,
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("updated_at")
        var updatedAt: String = "",
        @SerializedName("deleted_at")
        var deletedAt: String? = null
    ) : Serializable
}
