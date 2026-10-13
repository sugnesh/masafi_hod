package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class GRVDumpData(
    @SerializedName("data")
    val `data`: List<INVDATA>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class INVDATA(
        @SerializedName("invoice_no")
        var invoice_no: String,
        @SerializedName("item_code")
        var item_code: String,
        @SerializedName("item_name")
        var item_name: String,
        @SerializedName("sales_qty")
        var sales_qty: String,
        @SerializedName("pending_qty")
        var pending_qty: String,
        @SerializedName("net_price")
        var net_price: String,
        var itemQty: String = "0",
        var unitPrice: String = "0",

        )

}