package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class DiscountData(
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
            @SerializedName("qty_from")
            var qty_from: String,
            @SerializedName("qty_to")
            var qty_to: String,
            @SerializedName("discount_type")
            var discount_type: String,
            @SerializedName("discount_value")
            var discount_value: String,
            @SerializedName("discount_percentage")
            var discount_percentage: String,
            @SerializedName("discount_apply_on")
            var discount_apply_on: String,
            @SerializedName("p_d_p_discount_slabs")
            var slabs: List<PDPSlab>,
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
            data class PDPSlab(
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