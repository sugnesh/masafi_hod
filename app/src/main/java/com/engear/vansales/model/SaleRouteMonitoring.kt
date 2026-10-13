package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SaleRouteMonitoring(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("errors")
    var errors: List<Any> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("pagination")
    var pagination: List<Any> = listOf(),
    @SerializedName("status")
    var status: Boolean = false
) :Serializable{
    data class Data(
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("current_stage")
        var currentStage: String = "",
        @SerializedName("current_stage_comment")
        var currentStageComment: Any? = Any(),
        @SerializedName("customer_id")
        var customerId: Int = 0,
        @SerializedName("customer_lpo")
        var customerLpo: Any? = Any(),
        @SerializedName("deleted_at")
        var deletedAt: Any? = Any(),
        @SerializedName("delivery_id")
        var deliveryId: Any? = Any(),
        @SerializedName("depot")
        var depot: Any? = Any(),
        @SerializedName("depot_id")
        var depotId: Any? = Any(),
        @SerializedName("grand_total")
        var grandTotal: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("invoice_date")
        var invoiceDate: String = "",
        @SerializedName("invoice_due_date")
        var invoiceDueDate: String = "",
        @SerializedName("invoice_number")
        var invoiceNumber: String = "",
        @SerializedName("invoice_reminder")
        var invoiceReminder: Any? = Any(),
        @SerializedName("invoice_type")
        var invoiceType: String = "",
        @SerializedName("invoices")
        var invoices: ArrayList<Invoice> =ArrayList(),
        @SerializedName("is_premium_invoice")
        var isPremiumInvoice: Any? = Any(),
        @SerializedName("lob")
        var lob: Lob = Lob(),
        @SerializedName("lob_id")
        var lobId: Int = 0,
        @SerializedName("need_to_approve")
        var needToApprove: String = "",
        @SerializedName("objectid")
        var objectid: String = "",
        @SerializedName("order")
        var order: Any? = Any(),
        @SerializedName("order_id")
        var orderId: Any? = Any(),
        @SerializedName("order_type")
        var orderType: OrderType = OrderType(),
        @SerializedName("order_type_id")
        var orderTypeId: Int = 0,
        @SerializedName("organisation_id")
        var organisationId: Int = 0,
        @SerializedName("payment_received")
        var paymentReceived: Int = 0,
        @SerializedName("payment_term_id")
        var paymentTermId: Int = 0,
        @SerializedName("pending_credit")
        var pendingCredit: String = "",
        @SerializedName("salesman_id")
        var salesmanId: Int = 0,
        @SerializedName("source")
        var source: Int = 0,
        @SerializedName("status")
        var status: Int = 0,
        @SerializedName("total_discount_amount")
        var totalDiscountAmount: String = "",
        @SerializedName("total_excise")
        var totalExcise: String = "",
        @SerializedName("total_gross")
        var totalGross: String = "",
        @SerializedName("total_net")
        var totalNet: String = "",
        @SerializedName("total_qty")
        var totalQty: String = "",
        @SerializedName("total_vat")
        var totalVat: String = "",
        @SerializedName("trip_id")
        var tripId: Int = 0,
        @SerializedName("updated_at")
        var updatedAt: String = "",
        @SerializedName("user")
        var user: User = User(),
        @SerializedName("uuid")
        var uuid: String = ""
    ) :Serializable{
        data class Invoice(
            @SerializedName("batch_number")
            var batchNumber: Any? = Any(),
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("deleted_at")
            var deletedAt: Any? = Any(),
            @SerializedName("discount_id")
            var discountId: Any? = Any(),
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("invoice_id")
            var invoiceId: Int = 0,
            @SerializedName("is_free")
            var isFree: Int = 0,
            @SerializedName("is_item_poi")
            var isItemPoi: Int = 0,
            @SerializedName("item")
            var item: Item = Item(),
            @SerializedName("item_discount_amount")
            var itemDiscountAmount: String = "",
            @SerializedName("item_excise")
            var itemExcise: String = "",
            @SerializedName("item_grand_total")
            var itemGrandTotal: String = "",
            @SerializedName("item_gross")
            var itemGross: String = "",
            @SerializedName("item_id")
            var itemId: Int = 0,
            @SerializedName("item_net")
            var itemNet: String = "",
            @SerializedName("item_price")
            var itemPrice: String = "",
            @SerializedName("item_qty")
            var itemQty: String = "",
            @SerializedName("item_uom")
            var itemUom: ItemUom = ItemUom(),
            @SerializedName("item_uom_id")
            var itemUomId: Int = 0,
            @SerializedName("item_vat")
            var itemVat: String = "",
            @SerializedName("promotion_id")
            var promotionId: Any? = Any(),
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("uuid")
            var uuid: String = ""
        ):Serializable {
            data class Item(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("item_name")
                var itemName: String = ""
            ):Serializable

            data class ItemUom(
                @SerializedName("code")
                var code: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("name")
                var name: String = ""
            ):Serializable
        }

        data class Lob(
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("deleted_at")
            var deletedAt: Any? = Any(),
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("name")
            var name: String = "",
            @SerializedName("organisation_id")
            var organisationId: Int = 0,
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("user_id")
            var userId: Int = 0,
            @SerializedName("uuid")
            var uuid: String = ""
        ):Serializable

        data class OrderType(
            @SerializedName("description")
            var description: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("name")
            var name: String = ""
        ):Serializable

        data class User(
            @SerializedName("email")
            var email: String = "",
            @SerializedName("firstname")
            var firstname: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("lastname")
            var lastname: String = "",
            @SerializedName("parent_id")
            var parentId: String = ""
        ):Serializable
    }
}