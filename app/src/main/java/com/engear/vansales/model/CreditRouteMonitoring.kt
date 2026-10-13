package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreditRouteMonitoring(
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
):Serializable {
    data class Data(
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("credit_note_comment")
        var creditNoteComment: Any? = Any(),
        @SerializedName("credit_note_date")
        var creditNoteDate: String = "",
        @SerializedName("credit_note_details")
        var creditNoteDetails: List<CreditNoteDetail> = listOf(),
        @SerializedName("credit_note_number")
        var creditNoteNumber: String = "",
        @SerializedName("current_stage")
        var currentStage: String = "",
        @SerializedName("current_stage_comment")
        var currentStageComment: Any? = Any(),
        @SerializedName("customer")
        var customer: Customer = Customer(),
        @SerializedName("customer_id")
        var customerId: Int = 0,
        @SerializedName("deleted_at")
        var deletedAt: Any? = Any(),
        @SerializedName("grand_total")
        var grandTotal: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("invoice")
        var invoice: Invoice = Invoice(),
        @SerializedName("invoice_id")
        var invoiceId: Int = 0,
        @SerializedName("lob")
        var lob: Lob = Lob(),
        @SerializedName("lob_id")
        var lobId: Int = 0,
        @SerializedName("need_to_approve")
        var needToApprove: String = "",
        @SerializedName("objectid")
        var objectid: String = "",
        @SerializedName("organisation_id")
        var organisationId: Int = 0,
        @SerializedName("payment_term_id")
        var paymentTermId: Int = 0,
        @SerializedName("pending_credit")
        var pendingCredit: String = "",
        @SerializedName("reason")
        var reason: String = "",
        @SerializedName("salesman")
        var salesman: Salesman = Salesman(),
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
        @SerializedName("uuid")
        var uuid: String = "",
        @SerializedName("user")
        var user: User = User()
    ) :Serializable {
        data class CreditNoteDetail(
            @SerializedName("batch_number")
            var batchNumber: Any? = Any(),
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("credit_note_id")
            var creditNoteId: Int = 0,
            @SerializedName("deleted_at")
            var deletedAt: Any? = Any(),
            @SerializedName("discount_id")
            var discountId: Int = 0,
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("is_free")
            var isFree: Int = 0,
            @SerializedName("is_item_poi")
            var isItemPoi: Int = 0,
            @SerializedName("item")
            var item: Item = Item(),
            @SerializedName("item_condition")
            var itemCondition: String = "",
            @SerializedName("item_discount_amount")
            var itemDiscountAmount: String = "",
            @SerializedName("item_excise")
            var itemExcise: String = "",
            @SerializedName("item_expiry_date")
            var itemExpiryDate: Any? = Any(),
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
            @SerializedName("reason")
            var reason: String = "",
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("uuid")
            var uuid: String = ""
        ) :Serializable {
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

        data class Customer(
            @SerializedName("customerinfo")
            var customerinfo: Customerinfo = Customerinfo(),
            @SerializedName("firstname")
            var firstname: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("lastname")
            var lastname: String = ""
        ) :Serializable{
            data class Customerinfo(
                @SerializedName("customer_code")
                var customerCode: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("user_id")
                var userId: Int = 0
            ) : Serializable
        }

        data class Invoice(
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("current_stage")
            var currentStage: String = "",
            @SerializedName("current_stage_comment")
            var currentStageComment: Any? = Any(),
            @SerializedName("customer_id")
            var customerId: Int = 0,
            @SerializedName("deleted_at")
            var deletedAt: Any? = Any(),
            @SerializedName("delivery_id")
            var deliveryId: Any? = Any(),
            @SerializedName("depot_id")
            var depotId: Int = 0,
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
            @SerializedName("invoice_type")
            var invoiceType: String = "",
            @SerializedName("is_premium_invoice")
            var isPremiumInvoice: Int = 0,
            @SerializedName("lob_id")
            var lobId: Int = 0,
            @SerializedName("order_id")
            var orderId: Any? = Any(),
            @SerializedName("order_type_id")
            var orderTypeId: Int = 0,
            @SerializedName("organisation_id")
            var organisationId: Int = 0,
            @SerializedName("payment_received")
            var paymentReceived: Int = 0,
            @SerializedName("payment_term_id")
            var paymentTermId: Int = 0,
            @SerializedName("salesman_id")
            var salesmanId: Any? = Any(),
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
            var tripId: Any? = Any(),
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("uuid")
            var uuid: String = ""
        ): Serializable

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
        ) : Serializable

        data class Salesman(
            @SerializedName("firstname")
            var firstname: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("lastname")
            var lastname: String = "",
            @SerializedName("salesmaninfo")
            var salesmaninfo: Salesmaninfo = Salesmaninfo()
        ) :Serializable {
            data class Salesmaninfo(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("salesman_code")
                var salesmanCode: String = "",
                @SerializedName("user_id")
                var userId: Int = 0
            ):Serializable
        }

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
        ) :Serializable
    }
}