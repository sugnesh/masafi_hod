package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class DistributionData(
    @SerializedName("data")
    val `data`: List<DistData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class DistData(
        @SerializedName("cusotmer_id")
        var cusotmer_id: String,
        @SerializedName("distribution")
        var distribution: List<Distribution>
    ) {
        data class Distribution(
            @SerializedName("id")
            var id: String,
            @SerializedName("name")
            var name: String,
            @SerializedName("distributionModelStocks")
            var modelStock: List<ModelStock>
        ) {
            data class ModelStock(
                @SerializedName("id")
                var id: String,
                @SerializedName("customer_id")
                var customer_id: String,
                @SerializedName("distribution_model_stock_details")
                var modelStockDetail: List<ModelStockDetail>
            ) {
                data class ModelStockDetail(
                    @SerializedName("id")
                    var id: String,
                    @SerializedName("item_id")
                    var item_id: String,
                    @SerializedName("item_uom_id")
                    var item_uom_id: String,
                    @SerializedName("capacity")
                    var capacity: String,
                    @SerializedName("item")
                    var item: ItemData.Data
                )
            }
        }
    }
}