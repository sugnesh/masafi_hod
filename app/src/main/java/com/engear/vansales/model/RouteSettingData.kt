package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RouteSettingData(
    @SerializedName("data")
    var `data`: RouteData,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
){
    data class RouteData(
        @SerializedName("invoice_range_next_number")
        var invoice_range_from: String,
        @SerializedName("order_range_next_number")
        var order_range_next_number: String,
        @SerializedName("collection_range_next_number")
        var collection_range_from: String,
        @SerializedName("return_range_next_number")
        var return_range_from: String,
        @SerializedName("unload_range_next_number")
        var unload_range_from: String,
        @SerializedName("load_range_next_number")
        var load_range_from: String,
    )
}
