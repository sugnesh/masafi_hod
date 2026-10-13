package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

data class EndroseRequest(
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
) {
    data class Data(
        @SerializedName("route_id")
        var routeId: Int = 0,
        @SerializedName("uuid")
        var uuid: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("salesman_id")
        var salesmanId: Int = 0
    )
}