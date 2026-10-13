package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class OutstandingData(
    @SerializedName("data")
    val `data`: List<CollectionData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class CollectionData(
        @SerializedName("invid")
        var invoice_id: String,
        @SerializedName("customer_id")
        var customer_id: String,
        @SerializedName("invoicenumber")
        var invoice_number: String,
        @SerializedName("invdate")
        var invoice_date: String,
        @SerializedName("invduedate")
        var invoice_due_date: String,
        @SerializedName("grand_total")
        var grandTotal: String,
        @SerializedName("pending_amount")
        var pendingAmount: String,
        var amountPay: String,
        var invoiceComplete: String,
        var isInvoiceSplit: Boolean,
        var isSplitChecked: Boolean,
        var tempAmountDue: String,
        var amountEnter: String,
        var chequeDate: String,
        var chequeNo: String,
        var bank: String,
        @SerializedName("lob_id")
        var lob_id: String?,
        @SerializedName("customer_group_id")
        var customer_group_id: String?,
        @SerializedName("customername")
        var firstname: String?,
        @SerializedName("customer_code")
        var customer_code: String?,
        @SerializedName("itype")
        var itype: String?,
        var days: String?,
        var isOverDue: String?,
        @SerializedName("pdcAmount")
        var pdc_amount: String?,
        @SerializedName("netamount")
        var netamount: String?,
        @SerializedName("vatamount")
        var vatamount: String?,
        @SerializedName("debittype")
        var debittype: String?
    )
}