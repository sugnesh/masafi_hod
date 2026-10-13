package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class SalesTargetData(
    @SerializedName("data")
    val `data`: List<TargetData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class TargetData(
        @SerializedName("category_id")
        var id: String,
        @SerializedName("category_name")
        var category_name: String,
        @SerializedName("month_target")
        var month_target: String,
        @SerializedName("MTD")
        var MTD: String,
        @SerializedName("month_value_target")
        var month_value_target: String,
        @SerializedName("MTD_value")
        var MTD_value: String
    )
}