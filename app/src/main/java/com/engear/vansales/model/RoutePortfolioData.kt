package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class RoutePortfolioData(
    @SerializedName("data")
    val `data`: List<PortfolioActivity>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class PortfolioActivity(
        @SerializedName("user_id")
        var user_id: String,
        @SerializedName("portfolio_management_customer")
        var customer: List<CustData>,
        @SerializedName("portfolio_management_item")
        var item: List<PortfolioItem>
    ) {
        data class CustData(
            @SerializedName("user_id")
            var user_id: String
        )

        data class PortfolioItem(
            @SerializedName("item_id")
            var item_id: String,
            @SerializedName("plu")
            var plu: String? = null,
            @SerializedName("sell_enable")
            var sale_enable: String,
            @SerializedName("return_enable")
            var return_enable: String
        )

    }
}