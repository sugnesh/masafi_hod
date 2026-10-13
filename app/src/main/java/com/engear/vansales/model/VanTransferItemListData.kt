package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class VanTransferItemListData(
    @SerializedName("data")
    val `data`: List<DataItemVan>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class DataItemVan(
        @SerializedName("id")
        var id: Int,
        @SerializedName("item_id")
        var item_id: Int,
        @SerializedName("qty")
        var qty: Int,
        @SerializedName("item_uom_id")
        var item_uom_id: String?,
        @SerializedName("item")
        var item: ItemDetail? = null,
        @SerializedName("item_uom")
        var itemUom: ItemDetail.ItemUOM? = null
    ) {
        data class ItemDetail(
            @SerializedName("id")
            var id: Int,
            @SerializedName("item_code")
            var itemCode: String,
            @SerializedName("item_name")
            var item_name: String,
            var enterQty: String,
            var displayUOMName: String
        ) {
                data class ItemUOM(
                    @SerializedName("id")
                    var id: Int,
                    @SerializedName("name")
                    var uomName: String,
                    @SerializedName("code")
                    var uomCode: String
                )

        }

    }
}