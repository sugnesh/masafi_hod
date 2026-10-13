package com.engear.vansales.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "item")
data class ItemData(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    @Entity(tableName = "data")
    data class Data(
        @SerializedName("item_major_category_id")
        var majorCategory: Int,
        @SerializedName("item_group_id")
        var groupId: Int,
        @SerializedName("brand_id")
        var brandId: Int,
        @SerializedName("item_code")
        var itemCode: String,
        @SerializedName("id")
        var id: Int,
        @SerializedName("item_name")
        var itemName: String,
        @SerializedName("item_barcode")
        var barcode: String,
        @SerializedName("item_weight")
        var itemWeight: String,
        @SerializedName("item_shelf_life")
        var itemShellife: String,
        @SerializedName("lower_unit_item_upc")
        var lowerUnitUPC: String,
        @SerializedName("lower_unit_uom_id")
        var lowerUnitId: String,
        @SerializedName("lower_unit_item_price")
        var lowerUnitPrice: String,
        @SerializedName("is_tax_apply")
        var isTaxApply: String,
        @SerializedName("item_vat_percentage")
        var vatPercentage: String,
        @SerializedName("item_uom_lower_unit")
        var lowerUOM: ItemLowerUOM? = null,
        @SerializedName("item_main_price")
        var itemUOM: List<ItemMainUOM>,
        var itemSelectUOM: List<UomData>,
        var itemExistUOM: List<UomData>,
        @SerializedName("item_major_category")
        var category: CategoryData? = null,
        @SerializedName("brand")
        var brand: BrandData? = null,
        @SerializedName("item_group")
        var group: GroupData? = null,
        var vanUOM: String,
        var isFree: String,
        var displayUnitId: String,
        @SerializedName("lob_id")
        var lobId: String? = null,
        @SerializedName("lob")
        var lob: Lob? = null,
        @SerializedName("supervisor_category")
        var supervisor_category: SUPCategory? = null,
        @SerializedName("item_excise")
        var item_excise: String = "",
        @SerializedName("is_item_excise")
        var is_item_excise: Int = 0,
        @SerializedName("item_excise_uom_id")
        var item_excise_uom_id: Int = 0,
        var isStockAvailable: String = "0",
        var batchNo: String = "0",
        var expiry: String = "0"
    ) {
        @Entity(tableName = "lob")
        data class Lob(
            @SerializedName("name")
            var name: String,
            @SerializedName("user_id")
            var user_id: String,
            @SerializedName("id")
            var id: Int
        )

        @Entity(tableName = "subcategory")
        data class SUPCategory(
            @SerializedName("name")
            var name: String,
            @SerializedName("id")
            var id: Int
        )

        @Entity(tableName = "loweruom")
        data class ItemLowerUOM(
            @SerializedName("name")
            var name: String,
            @SerializedName("id")
            var id: Int
        )

        @Entity(tableName = "mainuom")
        data class ItemMainUOM(
            @SerializedName("item_uom_id")
            var uomId: String,
            @SerializedName("item_upc")
            var uomUPC: String,
            @SerializedName("item_price")
            var uomPrice: String,
            @SerializedName("item_id")
            var itemId: String,
            @SerializedName("id")
            var id: Int,
            var qty: String,
            var exciseAmt: String,
            @SerializedName("uom_type")
            var uomType: String,
            @SerializedName("sell_enable")
            var saleEnable: String,
            @SerializedName("return_enable")
            var returnEnable: String,
            @SerializedName("item_uom")
            var uom: ItemUOM
        ) {
            @Entity(tableName = "itemuom")
            data class ItemUOM(
                @SerializedName("name")
                var name: String,
                @SerializedName("id")
                var id: Int
            )
        }

        @Entity(tableName = "categorydata")
        data class CategoryData(
            @SerializedName("id")
            var id: Int,
            @SerializedName("name")
            var name: String,
            @SerializedName("uuid")
            var uuid: String
        )

        @Entity(tableName = "branddata")
        data class BrandData(
            @SerializedName("brand_name")
            var name: String,
            @SerializedName("id")
            var id: Int,
            @SerializedName("uuid")
            var uuid: String
        )

        @Entity(tableName = "groupdata")
        data class GroupData(
            @SerializedName("name")
            var name: String,
            @SerializedName("id")
            var id: Int,
            @SerializedName("uuid")
            var uuid: String
        )
    }
}