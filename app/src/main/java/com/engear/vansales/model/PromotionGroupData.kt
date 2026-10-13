package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class PromotionGroupData(
    @SerializedName("data")
    val `data`: List<PromoGroup>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class PromoGroup(
        @SerializedName("id")
        var id: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("promotiongroup_item")
        var groupItem: List<PromoGroupItem>

    ) {

        data class PromoGroupItem(
            @SerializedName("item_id")
            var item_id: String
        )

    }
}