package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class VanTransferRouteListData(
    @SerializedName("data")
    val `data`: List<DataVan>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class DataVan(
        @SerializedName("id")
        var id: Int,
        @SerializedName("route_id")
        var route_id: String?,
        @SerializedName("route")
        var route: RouteDetail? = null
    ) {

        data class RouteDetail(
            @SerializedName("id")
            var id: Int,
            @SerializedName("route_name")
            var route_name: String,
            @SerializedName("route_code")
            var route_code: String
        )

    }
}