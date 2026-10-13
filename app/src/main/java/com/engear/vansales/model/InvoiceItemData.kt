package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class InvoiceItemData(
    @SerializedName("data")
    val `data`: List<InvoicesItem>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class InvoicesItem(
        @SerializedName("item_id")
        var item_id: String,

        @SerializedName("item_uom_id")
        var item_uom_id: String,

        @SerializedName("item_qty")
        var item_qty: String,

        @SerializedName("item_price")
        var item_price: String,

        @SerializedName("batch_number")
        var batch_number: String,
        @SerializedName("item_net")
        var item_net: String,
        @SerializedName("item_discount_amount")
        var item_discount_amount: String
    )
}