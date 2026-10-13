package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SaleMonitoring(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("errors")
    var errors: List<Any> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("pagination")
    var pagination: List<Any> = listOf(),
    @SerializedName("status")
    var status: Boolean = false
) : Serializable {
    data class Data(
        @SerializedName("invoice_id")
        var id: Int = 0,
        @SerializedName("invoice_date")
        var invoiceDate: String = "",
        @SerializedName("invoice_number")
        var invoiceNumber: String = "",
        @SerializedName("grand_total")
        var totalAmount: String = "",
        @SerializedName("user")
        var user: User = User(),
        @SerializedName("credit_note_date")
        var credit_note_date: String = "",
        @SerializedName("credit_note_number")
        var creditNumber: String = "",
        @SerializedName("customer")
        var customer: User = User(),
    ) : Serializable {
        data class User(
            @SerializedName("customer_firstname")
            var firstname: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("customer_lastname")
            var lastname: String = ""
        )
    }
}