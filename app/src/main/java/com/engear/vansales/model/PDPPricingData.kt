package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class PDPPricingData(
    @SerializedName("data")
    val `data`: List<PricePPData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class PricePPData(
        @SerializedName("customer_code")
        var customer_code: String,
        @SerializedName("customer_name")
        var customer_fullname: String,
        @SerializedName("Priceinplan")
        var plandata: List<Priceinplan>
    ) {
        data class Priceinplan(
            @SerializedName("price_disco_promo_plans_id")
            var price_disco_promo_plans_id: String,
            @SerializedName("price_disco_promo_plans_name")
            var price_disco_promo_plans_name: String,
            @SerializedName("combination_plan_key_id")
            var combination_plan_key_id: String,
            @SerializedName("combination_plan_key_value")
            var combination_key_value: String,
            @SerializedName("item")
            var planItem: List<Item>
        ) {
            data class Item(
                @SerializedName("item_code")
                var item_code: String,
                @SerializedName("item_uom_id")
                var item_uom_id: String,
                @SerializedName("price")
                var price: String
            )
        }
    }
}