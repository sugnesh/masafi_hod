package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class SalesRestrictData(
    @SerializedName("data")
    val `data`: List<RestrictItem>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class RestrictItem(
        @SerializedName("item_id")
        var id: String,
        @SerializedName("item_code")
        var item_code: String,
        @SerializedName("restrict_qty")
        var restrict_qty: String,
        @SerializedName("sale_qty")
        var sale_qty: String
    )
}