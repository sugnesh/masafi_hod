package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class OrderTypeData(
    @SerializedName("data")
    var `data`: List<OrderType>,
    @SerializedName("errors")
    var errors: List<Any>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
) {
    data class OrderType(

        @SerializedName("id")
        var id: Int,

        @SerializedName("name")
        var name: String,

        @SerializedName("use_for")
        var useFor: String
    )
}