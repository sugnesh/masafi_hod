package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class DeliveryData(
    @SerializedName("data")
    var `data`: List<DeliveryHeader>,
    @SerializedName("errors")
    var errors: List<Any>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)

data class DeliveryHeader(

    @SerializedName("uuid")
    var uuID: String,

    @SerializedName("customer_id")
    var customerId: Int,

    @SerializedName("delivery_date")
    var deliveryDate: String,
    @SerializedName("delivery_time")
    var deliveryTime: String,

    @SerializedName("delivery_details")
    var deliveryDetails: List<DeliveryDetail>,

    @SerializedName("delivery_number")
    var deliveryNumber: String,

    @SerializedName("delivery_weight")
    var deliveryWeight: String,

    @SerializedName("grand_total")
    var grandTotal: String,

    @SerializedName("id")
    var id: Int,

    @SerializedName("order_id")
    var orderId: Int,

    @SerializedName("lob_id")
    var lobId: String?,

    @SerializedName("payment_term_id")
    var paymentTermId: Int,

    @SerializedName("salesman_id")
    var salesmanId: Int,

    @SerializedName("status")
    var status: Int,

    @SerializedName("total_discount_amount")
    var totalDiscountAmount: String,

    @SerializedName("total_excise")
    var totalExcise: String,

    @SerializedName("total_gross")
    var totalGross: String,

    @SerializedName("total_net")
    var totalNet: String,

    @SerializedName("total_qty")
    var totalQty: String,

    @SerializedName("total_vat")
    var totalVat: String,
    @SerializedName("plan_seq")
    var plan_seq: String,
    @SerializedName("delivery_charge")
    var delivery_charge: String,
    @SerializedName("loyality_discount")
    var loyal_points: String,
    @SerializedName("offer_type")
    var offer_type: String,
    @SerializedName("offer_value")
    var offer_value: String,
    @SerializedName("offer_name")
    var offer_name: String,
    var isDeleted: String,
    @SerializedName("order")
    var mOrder: OrderDetail,

    )

data class OrderDetail(
    @SerializedName("order_number")
    var order_number: String,
    @SerializedName("order_date")
    var order_date: String,
    @SerializedName("customer_lop")
    var order_lpo: String,
    @SerializedName("partial_main_order_id")
    var partial_main_order_id: String,
    @SerializedName("source_order_number")
    var source_order_number: String,
    @SerializedName("transaction_type")
    var transaction_type: String,
    @SerializedName("source_ref_number")
    var payment_id: String,
    @SerializedName("salesman")
    var mPreseller: Preseller,
    @SerializedName("route")
    var mPreRoute: PRoute,
    @SerializedName("sources")
    var mSource: Source,
    @SerializedName("customer_address")
    var mAddress: CustomerAddress,
    @SerializedName("any_comment")
    var any_comment: String? = "",
    @SerializedName("card_type")
    var card_type: String,
    @SerializedName("card_charge")
    var card_charge: String,
    @SerializedName("is_icash_deduct")
    var is_icash_deduct: String,
    @SerializedName("icash_amount")
    var icash_amount: String,
) {
    data class Preseller(
        @SerializedName("firstname")
        var firstname: String,
        @SerializedName("lastname")
        var lastname: String,
        @SerializedName("salesman_info")
        var mInfo: PresellerInfo
    ) {
        data class PresellerInfo(
            @SerializedName("user_id")
            var user_id: String,
            @SerializedName("salesman_code")
            var salesman_code: String
        )
    }
    data class PRoute(
        @SerializedName("route_code")
        var route_code: String,
        @SerializedName("route_name")
        var route_name: String
    )

    data class Source(
        @SerializedName("id")
        var id: String,
        @SerializedName("name")
        var name: String
    )

    data class CustomerAddress(
        @SerializedName("address_1")
        var address_1: String,
        @SerializedName("address_2")
        var address_2: String,
        @SerializedName("phone_number")
        var phone_number: String,
        @SerializedName("latitude")
        var latitude: String,
        @SerializedName("longitude")
        var longitude: String
    )
}

data class DeliveryDetail(

    @SerializedName("id")
    var id: Int,

    @SerializedName("batch_number")
    var batchNumber: String,

    @SerializedName("delivery_id")
    var deliveryId: Int,

    @SerializedName("discount_id")
    var discountId: Int,

    @SerializedName("is_free")
    var isFree: Int,

    @SerializedName("is_item_poi")
    var isItemPoi: Int,

    @SerializedName("item")
    var item: Item,

    @SerializedName("item_discount_amount")
    var itemDiscountAmount: String,

    @SerializedName("item_excise")
    var itemExcise: String,

    @SerializedName("item_grand_total")
    var itemGrandTotal: String,

    @SerializedName("item_gross")
    var itemGross: String,
    @SerializedName("item_id")
    var itemId: Int,
    @SerializedName("item_net")
    var itemNet: String,
    @SerializedName("item_price")
    var itemPrice: String,
    @SerializedName("item_qty")
    var itemQty: String,
    @SerializedName("item_uom")
    var itemUom: ItemUom,
    @SerializedName("item_uom_id")
    var itemUomId: Int,
    @SerializedName("item_vat")
    var itemVat: String,
    @SerializedName("promotion_id")
    var promotionId: Int
) {
    data class Item(
        @SerializedName("id")
        var id: Int,
        @SerializedName("item_name")
        var itemName: String,
        @SerializedName("item_code")
        var itemCode: String
    )

    data class ItemUom(
        @SerializedName("code")
        var code: String,
        @SerializedName("id")
        var id: Int,
        @SerializedName("name")
        var name: String
    )
}


