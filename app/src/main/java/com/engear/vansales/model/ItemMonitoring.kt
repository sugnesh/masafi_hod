package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ItemMonitoring(
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
        @SerializedName("item_id")
        var item_id: Int = 0,
        @SerializedName("item_name")
        var item_name: String = "",
        @SerializedName("total_count")
        var total_count: String = "",
        @SerializedName("sum_of_grand_total")
        var totalAmount: String = "",
        @SerializedName("sum_item_grand_total")
        var itemTotal: String = "",
        @SerializedName("item")
        var item: ITEMDATA = ITEMDATA()
    ) : Serializable {
        data class ITEMDATA(
            @SerializedName("item_name")
            var item_name: String = "",
            @SerializedName("item_code")
            var itemCode: String = ""
        )
    }
}