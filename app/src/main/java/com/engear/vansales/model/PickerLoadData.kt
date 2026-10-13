package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class PickerLoadData(
    @SerializedName("data")
    val `data`: List<PickerDataDetail>,
    @SerializedName("errors")
    var errors: List<Any>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
){
    data class PickerDataDetail(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("uuid")
        var uuid: String = "",
        @SerializedName("organisation_id")
        var organisation_id: Int = 0,
        @SerializedName("route_id")
        var route_id: Int = 0,
        @SerializedName("salesman_id")
        var salesman_id: Int = 0,
        @SerializedName("trip_id")
        var trip_id: Int = 0,
        @SerializedName("load_number")
        var load_number: String = "",
        @SerializedName("load_type")
        var load_type: String = "",
        @SerializedName("current_stage")
        var current_stage: String = "",
        @SerializedName("load_date")
        var load_date: String = "",
        @SerializedName("vehicle_number")
        var truck: String = "",
        @SerializedName("approval_status")
        var status: String = "",
        @SerializedName("load_request_detail")
        val load_request_detail: List<LoadRequest>,
        @SerializedName("salesman")
        var salesman: SalesmanDetail? = null,
        @SerializedName("route")
        var route: RouteDetails? = null,
    )  {
        data class SalesmanDetail(
            @SerializedName("id")
            var id: Int,
            @SerializedName("firstname")
            var firstname: String,
            @SerializedName("lastname")
            var lastname: String,
            @SerializedName("salesman_info")
            var salesmanInfo: SalesmanInfo
        ){
            data class SalesmanInfo(
                @SerializedName("salesman_code")
                var salesman_code: String
            )
        }
        data class RouteDetails(
            @SerializedName("id")
            var id: Int,
            @SerializedName("route_name")
            var route_name: String,
            @SerializedName("route_code")
            var route_code: String
        )
        data class LoadRequest(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("item_id")
            var item_id: Int = 0,
            @SerializedName("uuid")
            var uuid: String = "",
            @SerializedName("load_request_id")
            var load_request_id: Int = 0,
            @SerializedName("qty")
            var qty: String = "",
            @SerializedName("requested_qty")
            var requested_qty: String = "",
            @SerializedName("approval_qty_by_supervisor")
            var approval_qty_by_supervisor: String = "",
            @SerializedName("approval_qty_by_warehouse")
            var approval_qty_by_warehouse: String = "",
            @SerializedName("item_uom_id")
            var item_uom_id: String = "",
            @SerializedName("item")
            var item: ItemDetail? = null,
            @SerializedName("item_uom")
            var itemUom: ItemDetail.ItemUOM? = null,
            var uomQty: String = "0"
        ){
            data class ItemDetail(
                @SerializedName("id")
                var id: Int,
                @SerializedName("item_code")
                var itemCode: String,
                @SerializedName("item_name")
                var item_name: String
            ) {
                data class ItemUOM(
                    @SerializedName("id")
                    var id: Int,
                    @SerializedName("name")
                    var uomName: String,
                    @SerializedName("code")
                    var uomCode: String
                )

            }

        }

    }

}



/*
data class PickerLoadRequestDetail(

    @SerializedName("id")
    var id: Int?,
    @SerializedName("item_id")
    var itemId: String,
    @SerializedName("item_uom")
    var itemUom: String,
    @SerializedName("qty")
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
    @SerializedName("approval_qty_by_warehouse")
    var warehouseApproveQty: String,
    @SerializedName("approval_qty_by_supervisor")
    var superviserApproveQty: String,
    @SerializedName("variance_qty")
    var varianceQty: String
   */
/* @SerializedName("batchdetails")
    var batchdetailsList: List<PickerBatchdetailsList>?,
    var vanBatch: ArrayList<PickerBatchdetailsList>?,
    var varianceBatch: List<PickerBatchdetailsList>?,
    var differanceQty: String,*//*


)

data class PickerBatchdetailsList(

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

)*/
