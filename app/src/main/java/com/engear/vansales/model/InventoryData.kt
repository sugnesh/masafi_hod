package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class InventoryData(
    @SerializedName("data")
    val `data`: List<AssignData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class AssignData(
        @SerializedName("id")
        var id: String,
        @SerializedName("assign_inventory_id")
        var assign_inventory_id: String,
        @SerializedName("customer_id")
        var customer_id: String,
        @SerializedName("assign_inventory")
        var assign_inventory: AssignInventory
    ) {
        data class AssignInventory(
            @SerializedName("id")
            var id: String,
            @SerializedName("activity_name")
            var activity_name: String,
            @SerializedName("assign_inventory_details")
            var inventoryDetail: List<InventoryDetail>
        ) {
            data class InventoryDetail(
                @SerializedName("id")
                var id: String,
                @SerializedName("assign_inventory_id")
                var assign_inventory_id: String,
                @SerializedName("item_id")
                var item_id: String,
                @SerializedName("item_uom_id")
                var item_uom_id: String,
                @SerializedName("capacity")
                var capacity: String
            )
        }
    }
}