package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class NotificationData(
    @SerializedName("data")
    val `data`: List<NotificationView>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class NotificationView(
        @SerializedName("id")
        var id: String,
        @SerializedName("uuid")
        var uuid: String,
        @SerializedName("type")
        var type: String,
        @SerializedName("message")
        var message: String,
        @SerializedName("created_at")
        var date: String,
        @SerializedName("salesman_lat")
        var salesman_lat: String,
        @SerializedName("salesman_long")
        var salesman_long: String,
        @SerializedName("customer_lat")
        var customer_lat: String,
        @SerializedName("customer_long")
        var customer_long: String,
        @SerializedName("approval_status")
        var approval_status: String,
        @SerializedName("reason")
        var reason: String
    )
}