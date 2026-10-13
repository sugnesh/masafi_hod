package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

 data class DataCollection(
    @SerializedName("data")
    var `data`: Data = Data(),
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
        @SerializedName("account")
        var account: List<Any> = listOf(),
        @SerializedName("area")
        var area: List<Any> = listOf(),
        @SerializedName("area_list")
        var areaList: List<Any> = listOf(),
        @SerializedName("brand")
        var brand: List<Any> = listOf(),
        @SerializedName("brand_list")
        var brandList: List<Any> = listOf(),
        @SerializedName("channel")
        var channel: List<Channel> = listOf(),
        @SerializedName("channel_list")
        var channelList: List<Any> = listOf(),
        @SerializedName("code")
        var code: Int = 0,
        @SerializedName("country")
        var country: List<Any> = listOf(),
        @SerializedName("country_master")
        var countryMaster: List<Any> = listOf(),
        @SerializedName("currency")
        var currency: List<Any> = listOf(),
        @SerializedName("customer_category")
        var customerCategory: List<CustomerCategory> = listOf(),
        @SerializedName("customer_type")
        var customerType: List<Any> = listOf(),
        @SerializedName("customers")
        var customers: List<Any> = listOf(),
        @SerializedName("depot")
        var depot: List<Any> = listOf(),
        @SerializedName("item_group")
        var itemGroup: List<Any> = listOf(),
        @SerializedName("item_major_category")
        var itemMajorCategory: List<Any> = listOf(),
        @SerializedName("item_major_category_list")
        var itemMajorCategoryList: List<Any> = listOf(),
        @SerializedName("item_uom")
        var itemUom: List<Any> = listOf(),
        @SerializedName("items")
        var items: List<Any> = listOf(),
        @SerializedName("merchandiser")
        var merchandiser: List<Any> = listOf(),
        @SerializedName("order_type")
        var orderType: List<Any> = listOf(),
        @SerializedName("org_country")
        var orgCountry: List<Any> = listOf(),
        @SerializedName("payment_term")
        var paymentTerm: List<Any> = listOf(),
        @SerializedName("promotional_item")
        var promotionalItem: List<Any> = listOf(),
        @SerializedName("reason")
        var reason: List<Any> = listOf(),
        @SerializedName("reason_list")
        var reasonList: List<Any> = listOf(),
        @SerializedName("region")
        var region: List<Region> = listOf(),
        @SerializedName("route")
        var route: List<Any> = listOf(),
        @SerializedName("sales_organisation")
        var salesOrganisation: List<SalesOrganisation> = listOf(),
        @SerializedName("sales_organisation_list")
        var salesOrganisationList: List<Any> = listOf(),
        @SerializedName("salesman_role")
        var salesmanRole: List<Any> = listOf(),
        @SerializedName("salesman_supervisor")
        var salesmanSupervisor: List<Any> = listOf(),
        @SerializedName("salesman_type")
        var salesmanType: List<Any> = listOf(),
        @SerializedName("salesmans")
        var salesmans: List<Any> = listOf(),
        @SerializedName("vendor")
        var vendor: List<Any> = listOf()
    ) {
        data class Channel(
            @SerializedName("children")
            var children: List<Any> = listOf(),
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("name")
            var name: String = "",
            @SerializedName("node_level")
            var nodeLevel: Int = 0,
            @SerializedName("parent_id")
            var parentId: Any? = Any(),
            @SerializedName("status")
            var status: Int = 0
        )

        data class CustomerCategory(
            @SerializedName("children")
            var children: List<Any> = listOf(),
            @SerializedName("code")
            var code: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("name")
            var name: String = "",
            @SerializedName("node_level")
            var nodeLevel: Int = 0,
            @SerializedName("parent_id")
            var parentId: Any? = Any(),
            @SerializedName("status")
            var status: Int = 0
        )

        data class Region(
            @SerializedName("country_id")
            var countryId: Int = 0,
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("region_name")
            var regionName: String = "",
            @SerializedName("region_status")
            var regionStatus: Int = 0
        )

        data class SalesOrganisation(
            @SerializedName("children")
            var children: List<Any> = listOf(),
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("name")
            var name: String = "",
            @SerializedName("node_level")
            var nodeLevel: Int = 0,
            @SerializedName("parent_id")
            var parentId: Any? = Any(),
            @SerializedName("status")
            var status: Int = 0
        )
    }
}