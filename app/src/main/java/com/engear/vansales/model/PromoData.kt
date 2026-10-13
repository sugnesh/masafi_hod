package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class PromoData(
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
            @SerializedName("order_item_type")
            var orderType: String,
            @SerializedName("offer_item_type")
            var offerType: String,
            @SerializedName("p_d_p_promotion_items")
            var orderItems: List<PDPItems>,
            @SerializedName("p_d_p_promotion_offer_items")
            var offerItems: List<PDPOfferItems>
        ) {
            data class PDPItems(
                @SerializedName("id")
                var id: String,
                @SerializedName("item_id")
                var item_id: String,
                @SerializedName("item_uom_id")
                var item_uom_id: String,
                @SerializedName("price")
                var price: String,
                @SerializedName("item_qty")
                var qty: String
            )
            data class PDPOfferItems(
                @SerializedName("id")
                var id: String,
                @SerializedName("item_id")
                var item_id: String,
                @SerializedName("item_uom_id")
                var item_uom_id: String,
                @SerializedName("offered_qty")
                var qty: String
            )
        }
    }
}