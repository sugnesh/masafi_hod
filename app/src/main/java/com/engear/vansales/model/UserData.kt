package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("errors")
    var errors: List<Any>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
) {
    data class Data(
        @SerializedName("accessToken")
        var accessToken: String,
        @SerializedName("currency")
        var currency: Currency,
        @SerializedName("decimal_rate")
        var itemDecimal: String,
        @SerializedName("salesman_number_range")
        var salesmanNumberRange: SalesmanNumberRange,
        @SerializedName("user_info")
        var userInfo: UserInfo,
        @SerializedName("active_menu")
        var activeMenu: List<Menu>
    ) {
        data class SalesmanNumberRange(
            @SerializedName("collection_range_from")
            var collectionFrom: String,
            @SerializedName("collection_to")
            var collectionTo: String,
            @SerializedName("id")
            var id: Int,
            @SerializedName("invoice_range_from")
            var invoiceFrom: String,
            @SerializedName("invoice_to")
            var invoiceTo: String,
            @SerializedName("order_range_from")
            var orderFrom: String,
            @SerializedName("order_to")
            var orderTo: String,
            @SerializedName("return_range_from")
            var returnFrom: String,
            @SerializedName("credit_note_to")
            var returnTo: String,
            @SerializedName("salesman_id")
            var salesmanId: Int,
            @SerializedName("unload_range_from")
            var unloadFrom: String,
            @SerializedName("unload_to")
            var unloadTo: String
        )

        data class Currency(
            @SerializedName("code")
            var code: String,
            @SerializedName("symbol")
            var symbol: String,
            @SerializedName("decimal_digits")
            var decimal_digits: String,
            @SerializedName("rounding")
            var rounding: String,
            @SerializedName("format")
            var format: String
        )

        data class Menu(
            @SerializedName("id")
            var id: String,
            @SerializedName("menu_id")
            var menu_id: String,
            @SerializedName("is_active")
            var is_active: String,
            @SerializedName("role_menu")
            var roleMenu: RoleMenu
        ) {
            data class RoleMenu(
                @SerializedName("name")
                var name: String
            )
        }

        data class UserInfo(
            @SerializedName("api_token")
            var apiToken: String,
            @SerializedName("email")
            var email: String,
            @SerializedName("firstname")
            var firstname: String,
            @SerializedName("id")
            var id: Int,
            @SerializedName("lastname")
            var lastname: String,
            @SerializedName("mobile")
            var mobile: String,
            @SerializedName("organisation_id")
            var organisationId: Int,
            @SerializedName("salesman_info")
            var salesmanInfo: SalesmanInfo,
            @SerializedName("organisation")
            var organisation: OrganizationInfo,
            @SerializedName("uuid")
            var uuid: String
        ) {
            data class OrganizationInfo(
                @SerializedName("org_name")
                var name: String,
                @SerializedName("org_logo")
                var logo: String,
                @SerializedName("org_street1")
                var street1: String? = null,
                @SerializedName("org_street2")
                var street2: String? = null,
                @SerializedName("org_city")
                var city: String? = null,
                @SerializedName("org_state")
                var state: String? = null,
                @SerializedName("org_postal")
                var postal: String? = null,
                @SerializedName("country_info")
                var countryInfo: CountryInfo? = null
            ) {
                data class CountryInfo(
                    @SerializedName("name")
                    var name: String
                )
            }

            data class SalesmanInfo(
                @SerializedName("id")
                var id: Int,
                @SerializedName("salesman_type")
                var type: SalesmanType,
                @SerializedName("salesman_role")
                var role: SalesmanRole,
                @SerializedName("route")
                var route: Route,
                @SerializedName("route_id")
                var routeId: Int,
                @SerializedName("user_id")
                var userId: Int,
                @SerializedName("salesmanlobget")
                var lobList: List<SalesmanLob>,
                @SerializedName("profile_image")
                var profile_image: String? = null,
                @SerializedName("salesman_code")
                var salesman_code: String,
                @SerializedName("region_id")
                var regionId: String,
                @SerializedName("locus_password")
                var salesman_password: String,
                @SerializedName("salesman_supervisor")
                var supervisor: SalesmnSuperVisor
            ) {
                data class Route(
                    @SerializedName("id")
                    var id: Int,
                    @SerializedName("route_code")
                    var routeCode: String,
                    @SerializedName("route_name")
                    var routeName: String,
                    @SerializedName("depot_id")
                    var depotId: String,
                    @SerializedName("sales_enable")
                    var salesEnable: String,
                    @SerializedName("return_enable")
                    var returnEnable: String,
                    @SerializedName("collection")
                    var collection: String,
                    @SerializedName("print")
                    var print: String,
                    @SerializedName("warehouse")
                    var warehouse: Warehouse,
                    @SerializedName("van")
                    var van: Van,
                    @SerializedName("invoice_range_from")
                    var invoiceFrom: String,
                    @SerializedName("invoice_range_to")
                    var invoiceTo: String,
                    @SerializedName("invoice_range_next_number")
                    var invoiceNext: String,
                    @SerializedName("order_range_from")
                    var orderFrom: String,
                    @SerializedName("order_range_to")
                    var orderTo: String,
                    @SerializedName("order_range_next_number")
                    var orderNext: String,
                    @SerializedName("return_range_from")
                    var returnFrom: String,
                    @SerializedName("return_range_to")
                    var returnTo: String,
                    @SerializedName("return_range_next_number")
                    var returnNext: String,
                    @SerializedName("unload_range_from")
                    var unloadFrom: String,
                    @SerializedName("unload_range_to")
                    var unloadTo: String,
                    @SerializedName("unload_range_next_number")
                    var unloadNext: String,
                    @SerializedName("collection_range_from")
                    var collectionFrom: String,
                    @SerializedName("collection_range_to")
                    var collectionTo: String,
                    @SerializedName("collection_range_next_number")
                    var collectionNext: String,
                    @SerializedName("load_range_to")
                    var loadTo: String,
                    @SerializedName("load_range_next_number")
                    var loadNext: String,
                    @SerializedName("routesetting")
                    var routesetting: RouteSetting,
                ) {
                    data class Warehouse(
                        @SerializedName("id")
                        var id: Int,
                        @SerializedName("name")
                        var name: String,
                        @SerializedName("lat")
                        var lat: String,
                        @SerializedName("lang")
                        var lang: String,
                        @SerializedName("radius")
                        var radius: String
                    )

                    data class Van(
                        @SerializedName("id")
                        var id: Int,
                        @SerializedName("van_code")
                        var van_code: String,
                        @SerializedName("plate_number")
                        var plate_number: String,
                        @SerializedName("odometer_reading")
                        var odometer_reading: String
                    )

                    data class RouteSetting(
                        @SerializedName("route_id")
                        var id: Int,
                        @SerializedName("sales_enable")
                        var salesEnable: String,
                        @SerializedName("return_enable")
                        var returnEnable: String,
                        @SerializedName("collection")
                        var collection: String,
                        @SerializedName("print")
                        var print: String,
                        @SerializedName("customer_skip_enable")
                        var customer_skip_enable: Int,
                        @SerializedName("skipping_customers")
                        var skipping_customers: Int,
                        @SerializedName("force_check_in_enable")
                        var force_check_in_enable: Int,
                        @SerializedName("force_check_out_enable")
                        var force_check_out_enable: Int,
                        @SerializedName("invoice_cancellation_void_enable")
                        var invoice_cancellation_void_enable: Int,
                        @SerializedName("unscheduled_visits_enable")
                        var unscheduled_visits_enable: Int,
                        @SerializedName("no_transaction_enable")
                        var no_transaction_enable: Int,
                        @SerializedName("jcp_customers_enable")
                        var jcp_customers_enable: Int,
                        @SerializedName("van_stock_print_enable")
                        var van_stock_print_enable: Int
                    )
                }

                data class SalesmanType(
                    @SerializedName("id")
                    var id: Int,
                    @SerializedName("name")
                    var name: String
                )

                data class SalesmanRole(
                    @SerializedName("id")
                    var id: Int,
                    @SerializedName("name")
                    var name: String
                )

                data class SalesmnSuperVisor(
                    @SerializedName("id")
                    var id: Int,
                    @SerializedName("firstname")
                    var firstname: String,
                    @SerializedName("lastname")
                    var lastname: String
                )

                data class SalesmanLob(
                    @SerializedName("lob_id")
                    var lob_id: Int,
                    @SerializedName("lob")
                    var lobDetail: LOB
                ) {
                    data class LOB(
                        @SerializedName("id")
                        var id: Int,
                        @SerializedName("name")
                        var name: String
                    )
                }
            }
        }
    }
}