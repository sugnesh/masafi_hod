package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class CustomerItemReturnData(
    @SerializedName("data")
    val `data`: List<DataCustomerItemReturn>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class DataCustomerItemReturn(

        @SerializedName("lot_no")
        var lot_no: String,
        @SerializedName("qty")
        var qty: Int,
        @SerializedName("invoice_id")
        var invoice_id: Int,
        @SerializedName("invoice_no")
        var invoice_no: String,
        @SerializedName("expiry_date")
        var expiry_date: String

        )
}