package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class PricingData(
    @SerializedName("data")
    val `data`: List<PriceData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class PriceData(
        @SerializedName("customer")
        var customer: Customer,
        @SerializedName("pdp")
        var pdp: PDP
    ) {
        data class Customer(
            @SerializedName("id")
            var id: String,
            @SerializedName("firstname")
            var firstname: String,
            @SerializedName("lastname")
            var lastname: String
        )

        data class PDP(
            @SerializedName("id")
            var id: String,
            @SerializedName("name")
            var name: String,
            @SerializedName("p_d_p_items")
            var items: List<PDPItems>
        ) {
            data class PDPItems(
                @SerializedName("id")
                var id: String,
                @SerializedName("item_id")
                var item_id: String,
                @SerializedName("item_uom_id")
                var item_uom_id: String,
                @SerializedName("price")
                var price: String
            )
        }
    }
}