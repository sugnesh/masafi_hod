package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class PalletReturnData(
    @SerializedName("data")
    var `data`: List<PalletReturn>,
    @SerializedName("errors")
    var errors: List<Any>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
) {
    data class PalletReturn(

        @SerializedName("item_id")
        var item_id: String,
        @SerializedName("item_code")
        var item_code: String,
        @SerializedName("item_name")
        var item_name: String,
        @SerializedName("item_qty")
        var item_qty: String,
        @SerializedName("invoice_id")
        var invoice_id: String,
        @SerializedName("invoice_number")
        var invoice_number: String,
        var enterQty: String = "",
    )
}




