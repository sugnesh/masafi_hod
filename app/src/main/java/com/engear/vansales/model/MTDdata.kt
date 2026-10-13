package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

data class MTDdata(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("errors")
    var errors: List<Any> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("pagination")
    var pagination: List<Any> = listOf(),
    @SerializedName("status")
    var status: Boolean = false
) {
    data class Data(
        @SerializedName("collection_count")
        var collectionCount: Int = 0,
        @SerializedName("collection_total")
        var collectionTotal: Int = 0,
        @SerializedName("credit_note_count")
        var creditNoteCount: Int = 0,
        @SerializedName("credit_note_total")
        var creditNoteTotal: Int = 0,
        @SerializedName("cusotmer_visit_count")
        var cusotmerVisitCount: Int = 0,
        @SerializedName("details")
        var details: List<Any> = listOf(),
        @SerializedName("invoice_count")
        var invoiceCount: Int = 0,
        @SerializedName("invoice_total")
        var invoiceTotal: Int = 0
    )
}