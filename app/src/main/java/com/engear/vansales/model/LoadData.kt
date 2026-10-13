package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class LoadData(
    @SerializedName("data")
    var `data`: DataDetail,
    @SerializedName("errors")
    var errors: List<Any>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)

data class DataDetail(
    @SerializedName("LoadData")
    var loadList: List<LoadList>?,

    @SerializedName("UnloadData")
    var unloadList: List<LoadList>?
)

data class LoadList(
    @SerializedName("id")
    var id: String,
    @SerializedName("load_date")
    var loadDate: String?,
    @SerializedName("load_number")
    var loadNumber: String?,
    var loadStatus: String?,
    @SerializedName("salesman_load_details")
    var salesmanLoadDetails: List<SalesmanLoadDetail>?,
    @SerializedName("load_type")
    var loadType: String?
)

data class SalesmanLoadDetail(

    @SerializedName("id")
    var id: Int?,
    @SerializedName("item_id")
    var itemId: String,
    @SerializedName("item_uom")
    var itemUom: String,
    @SerializedName("load_qty")
    var loadQty: String,
    var itemName: String,
    var uomName: String,
    var batch: String,
    var itemCode: String,
    var uomQty: String,
    var actualQty: String,
    var enterQty: String,
     @SerializedName("requested_qty")
    var requestedQty: String,
    var displayUomId: String,
    var displayUOMName: String,
    @SerializedName("requested_item_uom_id")
    var requestedUOMId: String,
    @SerializedName("warehouse_approve_qty")
    var warehouseApproveQty: String,
    @SerializedName("superviser_approve_qty")
    var superviserApproveQty: String,
    @SerializedName("variance_qty")
    var varianceQty: String,
    @SerializedName("primary_qty")
    var primary_qty: String,
    @SerializedName("batchdetails")
    var batchdetailsList: List<BatchdetailsList>?,
    var vanBatch: ArrayList<BatchdetailsList>?,
    var varianceBatch: List<BatchdetailsList>?,
    var differanceQty: String,

)

data class BatchdetailsList(

    @SerializedName("id")
    var batchId: Int?,
    @SerializedName("batch_number")
    var batchNumber: String,
    @SerializedName("expiry_date")
    var expiryDate: String,
    @SerializedName("qty")
    var qty: String,
    var price: String,
    var invoiceId: String,
    var invoiceNo: String

)