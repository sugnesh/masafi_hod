package com.engear.vansales.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
@Entity(tableName = "uomdata")
data class UomData(
    @SerializedName("item_id")
    var itemId: String = "",

    @SerializedName("item_code")
    var itemCode: String = "",

    @SerializedName("item_name")
    var itemName: String = "",

    @SerializedName("uom_id")
    var uomId: String = "",

    @SerializedName("uom_type")
    var uomType: String = "",

    @SerializedName("uom_upc")
    var uomUPC: String = "",

    @SerializedName("uom_qty")
    var uomQty: String = "",

    @SerializedName("uom_price")
    var uomPrice: String = "",

    @SerializedName("gross_price")
    var grossPrice: String = "",

    @SerializedName("discount_per")
    var discountPer: String = "",

    @SerializedName("discount_amt")
    var discountAmt: String = "",

    @SerializedName("net_amt")
    var netAmt: String = "",

    @SerializedName("vat_amt")
    var vatAmt: String = "",

    @SerializedName("total_amt")
    var totalAmt: String = "",

    @SerializedName("promotion_Id")
    var promotion_Id: String = "",

    @SerializedName("discount_Id")
    var discount_Id: String = "",

    var reason: String = "",

    var expiryDate: String = "",
    var enterQty: String = "",
    var actualQty: String="",
    var invAmount: String = "",
    var invNo: String = "",
    var isBad:String = "",
    @SerializedName("item_excise")
    var item_excise: String = "",
    var batchNo: String = "",
    var batchs: List<BatchdetailsList> = emptyList(),
    var varianceBatch: List<BatchdetailsList> = emptyList()
)
