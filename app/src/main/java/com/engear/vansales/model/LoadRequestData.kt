package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class LoadRequestData(
    @SerializedName("data")
    val `data`: LoadRequestView,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class LoadRequestView(
        @SerializedName("id")
        var id: String,
        @SerializedName("load_number")
        var load_number: String,
        @SerializedName("load_request_detail")
        var itemDetil: List<ItemDetail>
    ) {
        data class ItemDetail(
            @SerializedName("item_id")
            var item_id: String,
            @SerializedName("item_uom_id")
            var item_uom_id: String,
            @SerializedName("qty")
            var qty: String,
            @SerializedName("requested_qty")
            var requested_qty: String,
            @SerializedName("requested_item_uom_id")
            var item_req_uom_id: String
        )
    }
}