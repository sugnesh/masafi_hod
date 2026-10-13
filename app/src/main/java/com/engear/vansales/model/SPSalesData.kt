package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class SPSalesData(
    @SerializedName("data")
    var data: Data,
    @SerializedName("errors")
    var errors: List<Any> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Boolean = false
) {
    data class Data(
        @SerializedName("invoice_total")
        var invoice_total: String = "",
        @SerializedName("credit_note_total")
        var credit_note_total: String = "",
        @SerializedName("collection_total")
        var collection_total: String = "",
        @SerializedName("cusotmer_visit_count")
        var cusotmer_visit_count: String = "",
        @SerializedName("details")
        var invDetail: List<InvDetails> = listOf()
    ) {
        data class InvDetails(
            @SerializedName("invoice_number")
            var invoice_number: String = "",
            @SerializedName("grand_total")
            var grand_total: String = "",
            @SerializedName("created_at")
            var created_at: String = "",
            @SerializedName("invoice_date")
            var invoice_date: String = ""
        )
    }
}