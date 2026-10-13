package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class InvoiceData(
    @SerializedName("data")
    val `data`: List<Invoices>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Invoices(
        @SerializedName("id")
        var invoice_id: String,

        @SerializedName("invoice_number")
        var invoice_number: String,
        @SerializedName("grand_total")
        var grand_total: String
    )
}