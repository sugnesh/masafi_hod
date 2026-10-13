package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class ProductCatelogData(
    @SerializedName("data")
    val `data`: List<ItemPData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class ItemPData(
        @SerializedName("id")
        var id: String,
        @SerializedName("item_code")
        var item_code: String,
        @SerializedName("item_name")
        var item_name: String,
        @SerializedName("item_description")
        var item_description: String,
        @SerializedName("lower_unit_uom_id")
        var lower_unit_uom_id: String,
        @SerializedName("lower_unit_item_price")
        var lower_unit_item_price: String,
        @SerializedName("product_catalog")
        var productCatalog: List<ProductCatelog>
    ) {
        data class ProductCatelog(
            @SerializedName("net_weight")
            var net_weight: String,
            @SerializedName("flawer")
            var flawer: String,
            @SerializedName("shelf_file")
            var shelf_file: String,
            @SerializedName("ingredients")
            var ingredients: String,
            @SerializedName("energy")
            var energy: String,
            @SerializedName("fat")
            var fat: String,
            @SerializedName("protein")
            var protein: String,
            @SerializedName("carbohydrate")
            var carbohydrate: String,
            @SerializedName("calcium")
            var calcium: String,
            @SerializedName("sodium")
            var sodium: String,
            @SerializedName("potassium")
            var potassium: String,
            @SerializedName("crude_fibre")
            var crude_fibre: String,
            @SerializedName("vitamin")
            var vitamin: String,
            @SerializedName("image_string")
            var image_string: String
        )
    }
}