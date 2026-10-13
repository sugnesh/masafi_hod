package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class CreditData(
    @SerializedName("data")
    var `data`: List<CreditHeader>,
    @SerializedName("errors")
    var errors: List<Any>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)

data class CreditHeader(

    @SerializedName("uuid")
    var uuID: String,

    @SerializedName("customer_id")
    var customerId: Int,

    @SerializedName("credit_note_date")
    var creditDate: String,

    @SerializedName("credit_note_details")
    var creditDetails: List<CreditDetail>,

    @SerializedName("credit_note_number")
    var creditNumber: String,

    @SerializedName("return_type")
    var orderType: String,

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
    @SerializedName("reason")
    var reason: String,
    @SerializedName("invoice_id")
    var invoice_id: String,
    var isDeleted: String
)


data class CreditDetail(

    @SerializedName("id")
    var id: Int,

    @SerializedName("credit_note_id")
    var creditNoteId: Int,

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
    @SerializedName("reason")
    var reason: String,
    @SerializedName("item_expiry_date")
    var item_expiry_date: String,
    @SerializedName("invoice_number")
    var invoice_number: String
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


