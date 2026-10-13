package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

data class LoadQuantity(
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
) {
    data class Data(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("loadquantity")
        var loadquantity: List<Loadquantity> = listOf(),
        @SerializedName("route_id")
        var routeId: Int = 0,
        @SerializedName("transaction_date")
        var transactionDate: String = ""
    ) {
        data class Loadquantity(
            @SerializedName("item")
            var item: Item = Item(),
            @SerializedName("item_id")
            var itemId: Int = 0,
            @SerializedName("load_qty")
            var loadQty: String = "",
            @SerializedName("return_qty")
            var returnQty: String = "",
            @SerializedName("sales_qty")
            var salesQty: String = "",
            @SerializedName("transaction_id")
            var transactionId: Int = 0,
            @SerializedName("unload_qty")
            var unloadQty: String = ""
        ) {
            data class Item(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("item_code")
                var itemCode: String = "",
                @SerializedName("item_name")
                var itemName: String = "",
                @SerializedName("item_uom_lower_unit")
                var itemUomLowerUnit: ItemUomLowerUnit = ItemUomLowerUnit(),
                @SerializedName("lower_unit_uom_id")
                var lowerUnitUomId: Int = 0
            ) {
                data class ItemUomLowerUnit(
                    @SerializedName("code")
                    var code: String = "",
                    @SerializedName("id")
                    var id: Int = 0,
                    @SerializedName("name")
                    var name: String = ""
                )
            }
        }
    }
}