package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BrandMonitoring(
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
        @SerializedName("brand_id")
        var brand_id: Int = 0,
        @SerializedName("brand_name")
        var brand_name: String = "",
        @SerializedName("sum_of_grand_total")
        var totalAmount: String = ""
    )
}