package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class PriceMFData(
    @SerializedName("data")
    val `data`: List<PriceFData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class PriceFData(
        @SerializedName("price_disco_promo_plan_id")
        var price_disco_promo_plan_id: String,
        @SerializedName("customer_id")
        var customer_id: String,
        @SerializedName("channel_id")
        var channel_id: String,
        @SerializedName("sub_channel_id")
        var sub_channel_id: String,
        @SerializedName("item_id")
        var item_id: String,
        @SerializedName("item_uom_id")
        var item_uom_id: String,
        @SerializedName("price")
        var price: String,
        @SerializedName("customer_price")
        var customer_price: String,
        @SerializedName("customer_info")
        var customer_info: List<CustomerInfo>,
        @SerializedName("channel")
        var channel: Channel,
        @SerializedName("subchannel")
        var subchannel: SubChannel
    ) {
        data class CustomerInfo(
            @SerializedName("user_id")
            var user_id: String,
            @SerializedName("customer_code")
            var customer_code: String
        )

        data class Channel(
            @SerializedName("name")
            var name: String
        )

        data class SubChannel(
            @SerializedName("customer_category_name")
            var name: String
        )

    }
}