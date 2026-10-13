package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryMonitoring(
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
        @SerializedName("cat_id")
        var cat_id: Int = 0,
        @SerializedName("cat_name")
        var cat_name: String = "",
        @SerializedName("sum_of_grand_total")
        var totalAmount: String = ""
    )
}