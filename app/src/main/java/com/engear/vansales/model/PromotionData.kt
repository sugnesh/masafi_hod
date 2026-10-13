package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class PromotionData(
    @SerializedName("data")
    val `data`: List<Promotion>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Promotion(
        @SerializedName("id")
        var id: String,
        @SerializedName("item_id")
        var item_id: String,
        @SerializedName("amount")
        var amount: String,
        @SerializedName("item")
        var item: ItemPromo
    ) {
        data class ItemPromo(
            @SerializedName("id")
            var id: String,
            @SerializedName("item_name")
            var name: String
        )
    }
}