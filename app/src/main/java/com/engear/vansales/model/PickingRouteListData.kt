package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class PickingRouteListData(
    @SerializedName("data")
    val `data`: List<DataPickingRoute>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class DataPickingRoute(
        @SerializedName("id")
        var id: Int,
        @SerializedName("route_code")
        var route_code: String?,
        @SerializedName("route_name")
        var route_name: String?

        )
}