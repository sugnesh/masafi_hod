package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class StockData(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Data(
        @SerializedName("id")
        var id: Int,
        @SerializedName("code")
        var code: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("depot_id")
        var depot_id: String,
        @SerializedName("item_main_price")
        var itemList: List<StockList>
    ) {
        data class StockList(
            @SerializedName("item_uom_id")
            var uomId: String,
            @SerializedName("item_id")
            var item_id: String,
            @SerializedName("qty")
            var qty: String,
            @SerializedName("batch")
            var batch: String,
            @SerializedName("item")
            var itemDetail: ItemUOM,
            @SerializedName("item_uom")
            var uom: ItemUOM
        ) {
            data class ItemUOM(
                @SerializedName("name")
                var name: String,
                @SerializedName("item_code")
                var itemCode: String,
                @SerializedName("id")
                var id: Int
            )

            data class ItemDetail(
                @SerializedName("item_name")
                var name: String,
                @SerializedName("id")
                var id: Int
            )
        }
    }
}