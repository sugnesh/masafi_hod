package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

 data class SaleOrderReturnAmount(
    @SerializedName("data")
    var `data`: Data = Data(),
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
        @SerializedName("collection_amount")
        var collectionAmount: Float = 0f,
        @SerializedName("order_amount")
        var orderAmount: Float = 0f,
        @SerializedName("return_amount")
        var returnAmount: Float = 0f,
        @SerializedName("sales_amount")
        var salesAmount: Float = 0f,
        @SerializedName("visit_count")
        var visitCount: Float = 0f
    )
}