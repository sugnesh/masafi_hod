package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class CouponData(
    @SerializedName("data")
    val `data`: List<Coupon>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Coupon(
        @SerializedName("id")
        var id: String,
        @SerializedName("item_id")
        var item_id: String,
        @SerializedName("serial_no_from")
        var serialFrom: String,
        @SerializedName("serial_no_to")
        var serialTo: String,
        @SerializedName("total_serial")
        var total_qty: String,
        @SerializedName("customer_coupon")
        val customerCoupon: List<CustomerCoupon>,
    ){
        data class CustomerCoupon(
            @SerializedName("customer_id")
            var customer_id: String?,
            @SerializedName("coupon_id")
            var coupon_id: String,
            @SerializedName("serial_number")
            var serial_number: String,
            @SerializedName("reedem_date")
            var reedem_date: String?,
            @SerializedName("coupon_price")
            var price: String?

        )
    }
}