package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

data class CarryOverEndrose(
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
        @SerializedName("code")
        var code: String = "",
        @SerializedName("route_id")
        var routeId: Int = 0,
        @SerializedName("salesman")
        var salesman: Salesman = Salesman(),
        @SerializedName("salesman_id")
        var salesmanId: Int = 0,
        @SerializedName("salesman_unload_detail")
        var transactiondetail: List<Transactiondetail> = listOf()
    ) {
        data class Salesman(
            @SerializedName("firstname")
            var firstname: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("lastname")
            var lastname: String = "",
            @SerializedName("salesmaninfo")
            var salesmaninfo: Salesmaninfo = Salesmaninfo()
        ) {
            data class Salesmaninfo(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("salesman_code")
                var salesmanCode: String = "",
                @SerializedName("user_id")
                var userId: Int = 0
            )
        }

        data class Transactiondetail(
            @SerializedName("unload_qty")
            var closingQty: String = "",
            @SerializedName("item")
            var item: Item = Item(),
            @SerializedName("item_uom")
            var item_uom: ItemUOM = ItemUOM(),
            @SerializedName("item_id")
            var itemId: Int = 0,
            @SerializedName("unload_type")
            var unload_type: String = "",
            var code: String = ""
        ) {
            data class Item(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("item_code")
                var itemCode: String = "",
                @SerializedName("item_name")
                var itemName: String = ""
            )

            data class ItemUOM(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("name")
                var name: String = ""
            )
        }
    }
}