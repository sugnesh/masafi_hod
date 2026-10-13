package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class SalesmanRequestData(
    @SerializedName("data")
    val `data`: SalesmanData,
    @SerializedName("errors")
    var errors: List<Any>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
) {
    data class SalesmanData(
        @SerializedName("total_sales")
        var totalSales: String,
        @SerializedName("total_return")
        var totalReturn: String,
        @SerializedName("total_target")
        var totalTarget: String?,
        @SerializedName("load_item")
        var loadedItem: String,
        @SerializedName("unload_item")
        var unloadedItem: String,
        @SerializedName("payment_summary")
        var totalCollection: String,
        @SerializedName("brand_wise_sales")
        var brandSale: String,
        @SerializedName("category_wise_sales")
        var categoryWiseSale: String,
        @SerializedName("item_wise_sales")
        var itemWiseSales: String,
        @SerializedName("slow_item_moving")
        var slowMobingItem: SlowItemMoving,
        @SerializedName("fast_item_moving")
        var fastMobingItem: FastItemMoving
    ) {
        data class SlowItemMoving(
            @SerializedName("item_name")
            var itemName: String
        )

        data class FastItemMoving(
            @SerializedName("item_name")
            var itemName: String
        )
    }
}