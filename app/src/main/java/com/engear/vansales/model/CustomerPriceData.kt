package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class CustomerPriceData(
    @SerializedName("data")
    val `data`: List<CustomerItemPrice>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class CustomerItemPrice(
        @SerializedName("id")
        var itemId: Int,
        @SerializedName("item_code")
        var item_code: String,
        @SerializedName("item_name")
        var item_name: String,
        @SerializedName("itm_uom_id")
        var item_uom_id: String,
        @SerializedName("item_discount")
        var itemDiscount: ItemDiscount? = null
    ) {

        data class ItemDiscount(
            @SerializedName("item_price")
            var item_price: String
        )

    }
}