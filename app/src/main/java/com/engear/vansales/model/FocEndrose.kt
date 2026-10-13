package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

data class FocEndrose(
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
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("deleted_at")
        var deletedAt: Any? = Any(),
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("item")
        var item: Item = Item(),
        @SerializedName("item_id")
        var itemId: Int = 0,
        @SerializedName("item_uom")
        var itemUom: ItemUom = ItemUom(),
        @SerializedName("item_uom_id")
        var itemUomId: Int = 0,
        @SerializedName("price")
        var price: Any? = Any(),
        @SerializedName("price_disco_promo_plan_id")
        var priceDiscoPromoPlanId: Int = 0,
        @SerializedName("updated_at")
        var updatedAt: String = "",
        @SerializedName("uuid")
        var uuid: String = ""
    ) {
        data class Item(
            @SerializedName("brand_id")
            var brandId: Int = 0,
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("current_stage")
            var currentStage: String = "",
            @SerializedName("current_stage_comment")
            var currentStageComment: String = "",
            @SerializedName("deleted_at")
            var deletedAt: Any? = Any(),
            @SerializedName("end_date")
            var endDate: Any? = Any(),
            @SerializedName("erp_code")
            var erpCode: Any? = Any(),
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("is_product_catalog")
            var isProductCatalog: Int = 0,
            @SerializedName("is_promotional")
            var isPromotional: Int = 0,
            @SerializedName("is_tax_apply")
            var isTaxApply: Int = 0,
            @SerializedName("item_barcode")
            var itemBarcode: Any? = Any(),
            @SerializedName("item_code")
            var itemCode: String = "",
            @SerializedName("item_description")
            var itemDescription: String = "",
            @SerializedName("item_excise")
            var itemExcise: String = "",
            @SerializedName("item_group_id")
            var itemGroupId: Int = 0,
            @SerializedName("item_image")
            var itemImage: Any? = Any(),
            @SerializedName("item_major_category_id")
            var itemMajorCategoryId: Int = 0,
            @SerializedName("item_name")
            var itemName: String = "",
            @SerializedName("item_shelf_life")
            var itemShelfLife: Any? = Any(),
            @SerializedName("item_vat_percentage")
            var itemVatPercentage: String = "",
            @SerializedName("item_weight")
            var itemWeight: Any? = Any(),
            @SerializedName("lob_id")
            var lobId: Int = 0,
            @SerializedName("lower_unit_item_price")
            var lowerUnitItemPrice: Int = 0,
            @SerializedName("lower_unit_item_upc")
            var lowerUnitItemUpc: Int = 0,
            @SerializedName("lower_unit_purchase_order_price")
            var lowerUnitPurchaseOrderPrice: Int = 0,
            @SerializedName("lower_unit_uom_id")
            var lowerUnitUomId: Int = 0,
            @SerializedName("new_lunch")
            var newLunch: Int = 0,
            @SerializedName("organisation_id")
            var organisationId: Int = 0,
            @SerializedName("start_date")
            var startDate: Any? = Any(),
            @SerializedName("status")
            var status: Int = 0,
            @SerializedName("stock_keeping_unit")
            var stockKeepingUnit: Int = 0,
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("uuid")
            var uuid: String = "",
            @SerializedName("volume")
            var volume: Any? = Any()
        )

        data class ItemUom(
            @SerializedName("code")
            var code: String = "",
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
            @SerializedName("status")
            var status: Int = 0,
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("uuid")
            var uuid: String = ""
        )
    }
}