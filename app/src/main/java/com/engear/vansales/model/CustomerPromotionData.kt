package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class CustomerPromotionData(
    @SerializedName("data")
    val `data`: CustomerPromotion,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class CustomerPromotion(
        @SerializedName("itemPromotionInfo")
        var itemPromoInfo: List<ItemPromoInfo>
    ) {

        data class ItemPromoInfo(
            @SerializedName("id")
            var id: String,
            @SerializedName("name")
            var name: String,
            @SerializedName("order_item_type")
            var order_item_type: String,
            @SerializedName("offer_item_type")
            var offer_item_type: String,
            @SerializedName("discount_type")
            var discount_type: String,
            @SerializedName("discount_apply")
            var discount_apply: String,
            @SerializedName("FocQty")
            var FocQty: String,
            @SerializedName("type")
            var type: String,
            @SerializedName("is_key_combination")
            var is_key_combination: String,
            @SerializedName("promotion_items")
            var promotion_items: List<PromotionItems>
        ){
            data class PromotionItems(
                @SerializedName("id")
                var id: String,
                @SerializedName("item_code")
                var item_code: String,
                @SerializedName("item_name")
                var item_name: String,
                @SerializedName("item_uom_id")
                var item_uom_id: String
            )
        }

    }
}