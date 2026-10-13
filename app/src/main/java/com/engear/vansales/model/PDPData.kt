package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class PDPData(
    @SerializedName("data")
    val `data`: List<PriceData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class PriceData(
        @SerializedName("name")
        var name: String,
        @SerializedName("id")
        var id: String,
        @SerializedName("combination_key_value")
        var combination_key_value: String,
        @SerializedName("is_key_combination")
        var is_key_combination: String,
        @SerializedName("type")
        var type: String,
        @SerializedName("order_item_type")
        var order_item_type: String,
        @SerializedName("offer_item_type")
        var offer_item_type: String,
        @SerializedName("discount_main_type")
        var discount_main_type: String,
        @SerializedName("discount_type")
        var discount_type: String,
        @SerializedName("discount_apply_on")
        var discount_apply_on: String,
        @SerializedName("discount_value")
        var discount_value: String,
        @SerializedName("discount_percentage")
        var discount_percentage: String,
        @SerializedName("qty_to")
        var qty_to: String,
        @SerializedName("priority_sequence")
        var priority_sequence: String,
        @SerializedName("p_d_p_customers")
        var pdpCustomer: List<Customer>,
        @SerializedName("p_d_p_countries")
        var pdpCountry: List<PDPCountry>,
        @SerializedName("p_d_p_regions")
        var pdpRegion: List<PDPRegion>,
        @SerializedName("p_d_p_areas")
        var pdpuArea: List<PDPArea>,
        @SerializedName("p_d_p_routes")
        var pdpRoute: List<PDPRoute>,
        @SerializedName("p_d_p_sales_organisations")
        var pdpSalesOrg: List<PDPSalesORG>,
        @SerializedName("p_d_p_channels")
        var pdpChannel: List<PDPChannel>,
        @SerializedName("p_d_p_subsecondary")
        var pdpSubChannel: List<PDPSubChannel>,
        @SerializedName("p_d_p_parent_customers")
        var pdpParent: List<PDPParent>,
        @SerializedName("p_d_p_customer_categories")
        var pdpCustomerCategory: List<PDPCustomerCategory>,
        @SerializedName("p_d_p_item_major_categories")
        var pdpItemCategory: List<PDPItemCategory>,
        @SerializedName("p_d_p_item_groups")
        var pdpItemGroup: List<PDPItemGroup>,
        @SerializedName("p_d_p_items")
        var pdpItem: List<PDPItemsMain>,
        @SerializedName("p_d_p_promotion_items")
        var pdpPromotionItem: List<PDPItemsMain>,
        @SerializedName("p_d_p_promotion_offer_items")
        var pdpOfferItem: List<PDPItemsMain>,
        @SerializedName("p_d_p_combination_slabs")
        var pdpSlab: List<PDPItemSlab>,
        @SerializedName("p_d_p_exclude_customer")
        var pdpExcludeCustomer: List<Customer>,
    ) {
        data class Customer(
            @SerializedName("id")
            var id: String,
            @SerializedName("customer_id")
            var customer_id: String,
            @SerializedName("lastname")
            var lastname: String,
            @SerializedName("customer_info")
            var customer_info: CustomerInfo
        ) {
            data class CustomerInfo(
                @SerializedName("user_id")
                var user_id: String
            )
        }

        data class PDPCountry(
            @SerializedName("id")
            var id: String,
            @SerializedName("country_id")
            var country_id: String
        )

        data class PDPRoute(
            @SerializedName("id")
            var id: String,
            @SerializedName("route_id")
            var route_id: String
        )

        data class PDPSalesORG(
            @SerializedName("id")
            var id: String,
            @SerializedName("sales_organisation_id")
            var sales_org_id: String
        )

        data class PDPChannel(
            @SerializedName("id")
            var id: String,
            @SerializedName("channel_id")
            var channel_id: String
        )

        data class PDPSubChannel(
            @SerializedName("id")
            var id: String,
            @SerializedName("sub_secondary_channel_id")
            var subChannel_id: String
        )

        data class PDPParent(
            @SerializedName("id")
            var id: String,
            @SerializedName("parent_customer_id")
            var parent_cust_id: String,
            @SerializedName("customer_infos")
            var customer_info: CustomerInfo
        ) {
            data class CustomerInfo(
                @SerializedName("user_id")
                var user_id: String
            )
        }

        data class PDPRegion(
            @SerializedName("id")
            var id: String,
            @SerializedName("region_id")
            var region_id: String
        )

        data class PDPArea(
            @SerializedName("id")
            var id: String,
            @SerializedName("area_id")
            var area_id: String
        )

        data class PDPCustomerCategory(
            @SerializedName("id")
            var id: String,
            @SerializedName("channel_id")
            var category_id: String
        )

        data class PDPItemCategory(
            @SerializedName("id")
            var id: String,
            @SerializedName("major_category_id")
            var major_category_id: String
        )

        data class PDPItemGroup(
            @SerializedName("id")
            var id: String,
            @SerializedName("item_group_id")
            var item_group_id: String
        )

        data class PDPItemsMain(
            @SerializedName("id")
            var id: String,
            @SerializedName("item_id")
            var item_id: String,
            @SerializedName("item_uom_id")
            var item_uom_id: String,
            @SerializedName("price")
            var price: String,
            @SerializedName("item_qty")
            var item_qty: String,
            @SerializedName("offered_qty")
            var offered_qty: String,
            @SerializedName("to_qty")
            var to_qty: String,
            @SerializedName("type")
            var type: String,
            @SerializedName("promotion_group_id")
            var promotion_group_id: String,
            @SerializedName("sequence")
            var sequence: String
        )

        data class PDPItemSlab(
            @SerializedName("price_disco_promo_plan_id")
            var id: String,
            @SerializedName("item_uom_id")
            var item_uom_id: String,
            @SerializedName("from_qty")
            var from_qty: String,
            @SerializedName("to_qty")
            var to_qty: String,
            @SerializedName("offer_qty")
            var offer_qty: String,
            @SerializedName("type")
            var type: String,
            @SerializedName("promotion_group_id")
            var promotion_group_id: String,
            @SerializedName("sequence")
            var sequence: String
        )
    }
}