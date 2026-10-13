package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class RouteItemData(
    @SerializedName("data")
    val `data`: List<RouteActivity>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class RouteActivity(
        @SerializedName("route_id")
        var route_id: Int,
        @SerializedName("route_item_grouping_details")
        var routeItem: List<RouteItem>
    ) {
        data class RouteItem(
            @SerializedName("item_id")
            var item_id: String
        )
    }
}