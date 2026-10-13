package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NewCustomer(
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
):Serializable {
    data class Data(
        @SerializedName("balance")
        var balance: String? = "",
        @SerializedName("bill_to_payer")
        var billToPayer: BillToPayer? = BillToPayer(),
        @SerializedName("channel")
        var channel: Channel? = Channel(),
        @SerializedName("channel_id")
        var channelId: Int? = 0,
        @SerializedName("credit_days")
        var creditDays: Int? = 0,
        @SerializedName("credit_limit")
        var creditLimit: String? = "",
        @SerializedName("current_stage")
        var currentStage: String = "",
        @SerializedName("current_stage_comment")
        var currentStageComment: Any? = Any(),
        @SerializedName("custom_field_value_save")
        var customFieldValueSave: List<Any> = listOf(),
        @SerializedName("customer_address_1")
        var customerAddress1: String = "",
        @SerializedName("customer_address_1_lang")
        var customerAddress1Lang: String = "",
        @SerializedName("customer_address_1_lat")
        var customerAddress1Lat: String = "",
        @SerializedName("customer_address_2")
        var customerAddress2: Any? = Any(),
        @SerializedName("customer_address_2_lang")
        var customerAddress2Lang: String = "",
        @SerializedName("customer_address_2_lat")
        var customerAddress2Lat: String = "",
        @SerializedName("customer_category")
        var customerCategory: CustomerCategory? = CustomerCategory(),
        @SerializedName("customer_category_id")
        var customerCategoryId: Int? = 0,
        @SerializedName("customer_city")
        var customerCity: Any? = Any(),
        @SerializedName("customer_code")
        var customerCode: String = "",
        @SerializedName("customer_document")
        var customerDocument: List<CustomerDocument> = listOf(),
        @SerializedName("customer_group")
        var customerGroup: Any? = Any(),
        @SerializedName("customer_group_id")
        var customerGroupId: Int = 0,
        @SerializedName("customer_merchandiser")
        var customerMerchandiser: List<Any> = listOf(),
        @SerializedName("customer_phone")
        var customerPhone: Any? = Any(),
        @SerializedName("customer_state")
        var customerState: Any? = Any(),
        @SerializedName("customer_type")
        var customerType: CustomerType? = CustomerType(),
        @SerializedName("customer_type_id")
        var customerTypeId: Int? = 0,
        @SerializedName("customer_zipcode")
        var customerZipcode: Any? = Any(),
        @SerializedName("customerlob")
        var customerlob: List<Customerlob> = listOf(),
        @SerializedName("erp_code")
        var erpCode: Any? = Any(),
        @SerializedName("expired_date")
        var expiredDate: String? = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("is_lob")
        var isLob: Int = 0,
        @SerializedName("need_to_approve")
        var needToApprove: String = "",
        @SerializedName("objectid")
        var objectid: String = "",
        @SerializedName("organisation_id")
        var organisationId: Int = 0,
        @SerializedName("payer")
        var payer: Payer? = Payer(),
        @SerializedName("payment_term_id")
        var paymentTermId: Int? = 0,
        @SerializedName("region")
        var region: Region? = Region(),
        @SerializedName("region_id")
        var regionId: Int? = 0,
        @SerializedName("route")
        var route: Route? = Route(),
        @SerializedName("route_id")
        var routeId: Int? = 0,
        @SerializedName("sales_organisation")
        var salesOrganisation: SalesOrganisation? = SalesOrganisation(),
        @SerializedName("sales_organisation_id")
        var salesOrganisationId: Int? = 0,
        @SerializedName("ship_to_party")
        var shipToParty: ShipToParty? = ShipToParty(),
        @SerializedName("sold_to_party")
        var soldToParty: SoldToParty? = SoldToParty(),
        @SerializedName("status")
        var status: Int = 0,
        @SerializedName("user")
        var user: User = User(),
        @SerializedName("user_country")
        var userCountry: UserCountry = UserCountry(),
        @SerializedName("user_id")
        var userId: Int = 0,
        @SerializedName("uuid")
        var uuid: String = ""
    ) :Serializable{
        data class BillToPayer(
            @SerializedName("customer_code")
            var customerCode: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("user")
            var user: User = User(),
            @SerializedName("user_id")
            var userId: Int = 0
        ) :Serializable{
            data class User(
                @SerializedName("firstname")
                var firstname: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("lastname")
                var lastname: String = ""
            ):Serializable
        }

        data class Channel(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("name")
            var name: String = "",
            @SerializedName("status")
            var status: Int = 0
        ):Serializable

        data class CustomerCategory(
            @SerializedName("customer_category_code")
            var customerCategoryCode: String = "",
            @SerializedName("customer_category_name")
            var customerCategoryName: String = "",
            @SerializedName("id")
            var id: Int = 0
        ):Serializable

        data class CustomerDocument(
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("customer_id")
            var customerId: Int = 0,
            @SerializedName("doc_string")
            var docString: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("uuid")
            var uuid: String = ""
        ):Serializable

        data class CustomerType(
            @SerializedName("customer_type_name")
            var customerTypeName: String = "",
            @SerializedName("id")
            var id: Int = 0
        ):Serializable

        data class Customerlob(
            @SerializedName("amount")
            var amount: Int = 0,
            @SerializedName("balance")
            var balance: String = "",
            @SerializedName("bill_to_payer")
            var billToPayer: BillToPayer = BillToPayer(),
            @SerializedName("channel")
            var channel: Channel = Channel(),
            @SerializedName("channel_id")
            var channelId: Int = 0,
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("credit_days")
            var creditDays: Int = 0,
            @SerializedName("credit_limit")
            var creditLimit: String = "",
            @SerializedName("customer_category")
            var customerCategory: CustomerCategory = CustomerCategory(),
            @SerializedName("customer_category_id")
            var customerCategoryId: Int = 0,
            @SerializedName("customer_group")
            var customerGroup: Any? = Any(),
            @SerializedName("customer_group_id")
            var customerGroupId: Any? = Any(),
            @SerializedName("customer_info_id")
            var customerInfoId: Int = 0,
            @SerializedName("customer_type")
            var customerType: CustomerType = CustomerType(),
            @SerializedName("customer_type_id")
            var customerTypeId: Int = 0,
            @SerializedName("deleted_at")
            var deletedAt: Any? = Any(),
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("lob")
            var lob: Lob = Lob(),
            @SerializedName("lob_id")
            var lobId: Int = 0,
            @SerializedName("merchandiser_id")
            var merchandiserId: Any? = Any(),
            @SerializedName("organisation_id")
            var organisationId: Int = 0,
            @SerializedName("payer")
            var payer: Payer = Payer(),
            @SerializedName("payment_term_id")
            var paymentTermId: Int = 0,
            @SerializedName("region")
            var region: Region = Region(),
            @SerializedName("region_id")
            var regionId: Int = 0,
            @SerializedName("route")
            var route: Route = Route(),
            @SerializedName("route_id")
            var routeId: Int = 0,
            @SerializedName("sales_organisation")
            var salesOrganisation: SalesOrganisation = SalesOrganisation(),
            @SerializedName("sales_organisation_id")
            var salesOrganisationId: Int = 0,
            @SerializedName("ship_to_party")
            var shipToParty: ShipToParty = ShipToParty(),
            @SerializedName("sold_to_party")
            var soldToParty: SoldToParty = SoldToParty(),
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("uuid")
            var uuid: String = ""
        ):Serializable {
            data class BillToPayer(
                @SerializedName("customer_code")
                var customerCode: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("user")
                var user: User = User(),
                @SerializedName("user_id")
                var userId: Int = 0
            ):Serializable {
                data class User(
                    @SerializedName("api_token")
                    var apiToken: String = "",
                    @SerializedName("country_id")
                    var countryId: Any? = Any(),
                    @SerializedName("created_at")
                    var createdAt: String = "",
                    @SerializedName("deleted_at")
                    var deletedAt: Any? = Any(),
                    @SerializedName("email")
                    var email: String = "",
                    @SerializedName("email_verified_at")
                    var emailVerifiedAt: Any? = Any(),
                    @SerializedName("firstname")
                    var firstname: String = "",
                    @SerializedName("id")
                    var id: Int = 0,
                    @SerializedName("id_stripe")
                    var idStripe: Any? = Any(),
                    @SerializedName("is_approved_by_admin")
                    var isApprovedByAdmin: Int = 0,
                    @SerializedName("lastname")
                    var lastname: String = "",
                    @SerializedName("login_type")
                    var loginType: String = "",
                    @SerializedName("mobile")
                    var mobile: String = "",
                    @SerializedName("organisation_id")
                    var organisationId: Int = 0,
                    @SerializedName("parent_id")
                    var parentId: String = "",
                    @SerializedName("role_id")
                    var roleId: Int = 0,
                    @SerializedName("status")
                    var status: Int = 0,
                    @SerializedName("updated_at")
                    var updatedAt: String = "",
                    @SerializedName("usertype")
                    var usertype: Int = 0,
                    @SerializedName("uuid")
                    var uuid: String = ""
                ):Serializable
            }

            data class Channel(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("name")
                var name: String = "",
                @SerializedName("status")
                var status: Int = 0
            ):Serializable

            data class CustomerCategory(
                @SerializedName("customer_category_code")
                var customerCategoryCode: String = "",
                @SerializedName("customer_category_name")
                var customerCategoryName: String = "",
                @SerializedName("id")
                var id: Int = 0
            ):Serializable

            data class CustomerType(
                @SerializedName("customer_type_name")
                var customerTypeName: String = "",
                @SerializedName("id")
                var id: Int = 0
            ):Serializable

            data class Lob(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("name")
                var name: String = ""
            ):Serializable

            data class Payer(
                @SerializedName("customer_code")
                var customerCode: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("user")
                var user: User = User(),
                @SerializedName("user_id")
                var userId: Int = 0
            ):Serializable {
                data class User(
                    @SerializedName("api_token")
                    var apiToken: String = "",
                    @SerializedName("country_id")
                    var countryId: Any? = Any(),
                    @SerializedName("created_at")
                    var createdAt: String = "",
                    @SerializedName("deleted_at")
                    var deletedAt: Any? = Any(),
                    @SerializedName("email")
                    var email: String = "",
                    @SerializedName("email_verified_at")
                    var emailVerifiedAt: Any? = Any(),
                    @SerializedName("firstname")
                    var firstname: String = "",
                    @SerializedName("id")
                    var id: Int = 0,
                    @SerializedName("id_stripe")
                    var idStripe: Any? = Any(),
                    @SerializedName("is_approved_by_admin")
                    var isApprovedByAdmin: Int = 0,
                    @SerializedName("lastname")
                    var lastname: String = "",
                    @SerializedName("login_type")
                    var loginType: String = "",
                    @SerializedName("mobile")
                    var mobile: String = "",
                    @SerializedName("organisation_id")
                    var organisationId: Int = 0,
                    @SerializedName("parent_id")
                    var parentId: String = "",
                    @SerializedName("role_id")
                    var roleId: Int = 0,
                    @SerializedName("status")
                    var status: Int = 0,
                    @SerializedName("updated_at")
                    var updatedAt: String = "",
                    @SerializedName("usertype")
                    var usertype: Int = 0,
                    @SerializedName("uuid")
                    var uuid: String = ""
                ):Serializable
            }

            data class Region(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("region_code")
                var regionCode: String = "",
                @SerializedName("region_name")
                var regionName: String = "",
                @SerializedName("region_status")
                var regionStatus: Int = 0
            ):Serializable

            data class Route(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("route_code")
                var routeCode: String = "",
                @SerializedName("route_name")
                var routeName: String = "",
                @SerializedName("status")
                var status: Int = 0
            ):Serializable

            data class SalesOrganisation(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("name")
                var name: String = ""
            ):Serializable

            data class ShipToParty(
                @SerializedName("customer_code")
                var customerCode: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("user")
                var user: User = User(),
                @SerializedName("user_id")
                var userId: Int = 0
            ):Serializable {
                data class User(
                    @SerializedName("api_token")
                    var apiToken: String = "",
                    @SerializedName("country_id")
                    var countryId: Any? = Any(),
                    @SerializedName("created_at")
                    var createdAt: String = "",
                    @SerializedName("deleted_at")
                    var deletedAt: Any? = Any(),
                    @SerializedName("email")
                    var email: String = "",
                    @SerializedName("email_verified_at")
                    var emailVerifiedAt: Any? = Any(),
                    @SerializedName("firstname")
                    var firstname: String = "",
                    @SerializedName("id")
                    var id: Int = 0,
                    @SerializedName("id_stripe")
                    var idStripe: Any? = Any(),
                    @SerializedName("is_approved_by_admin")
                    var isApprovedByAdmin: Int = 0,
                    @SerializedName("lastname")
                    var lastname: String = "",
                    @SerializedName("login_type")
                    var loginType: String = "",
                    @SerializedName("mobile")
                    var mobile: String = "",
                    @SerializedName("organisation_id")
                    var organisationId: Int = 0,
                    @SerializedName("parent_id")
                    var parentId: String = "",
                    @SerializedName("role_id")
                    var roleId: Int = 0,
                    @SerializedName("status")
                    var status: Int = 0,
                    @SerializedName("updated_at")
                    var updatedAt: String = "",
                    @SerializedName("usertype")
                    var usertype: Int = 0,
                    @SerializedName("uuid")
                    var uuid: String = ""
                ):Serializable
            }

            data class SoldToParty(
                @SerializedName("customer_code")
                var customerCode: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("user")
                var user: User = User(),
                @SerializedName("user_id")
                var userId: Int = 0
            ):Serializable {
                data class User(
                    @SerializedName("api_token")
                    var apiToken: String = "",
                    @SerializedName("country_id")
                    var countryId: Any? = Any(),
                    @SerializedName("created_at")
                    var createdAt: String = "",
                    @SerializedName("deleted_at")
                    var deletedAt: Any? = Any(),
                    @SerializedName("email")
                    var email: String = "",
                    @SerializedName("email_verified_at")
                    var emailVerifiedAt: Any? = Any(),
                    @SerializedName("firstname")
                    var firstname: String = "",
                    @SerializedName("id")
                    var id: Int = 0,
                    @SerializedName("id_stripe")
                    var idStripe: Any? = Any(),
                    @SerializedName("is_approved_by_admin")
                    var isApprovedByAdmin: Int = 0,
                    @SerializedName("lastname")
                    var lastname: String = "",
                    @SerializedName("login_type")
                    var loginType: String = "",
                    @SerializedName("mobile")
                    var mobile: String = "",
                    @SerializedName("organisation_id")
                    var organisationId: Int = 0,
                    @SerializedName("parent_id")
                    var parentId: String = "",
                    @SerializedName("role_id")
                    var roleId: Int = 0,
                    @SerializedName("status")
                    var status: Int = 0,
                    @SerializedName("updated_at")
                    var updatedAt: String = "",
                    @SerializedName("usertype")
                    var usertype: Int = 0,
                    @SerializedName("uuid")
                    var uuid: String = ""
                ):Serializable
            }
        }

        data class Payer(
            @SerializedName("customer_code")
            var customerCode: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("user")
            var user: User = User(),
            @SerializedName("user_id")
            var userId: Int = 0
        ):Serializable {
            data class User(
                @SerializedName("firstname")
                var firstname: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("lastname")
                var lastname: String = ""
            ):Serializable
        }

        data class Region(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("region_name")
            var regionName: String = "",
            @SerializedName("region_status")
            var regionStatus: Int = 0
        ):Serializable

        data class Route(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("route_code")
            var routeCode: String = "",
            @SerializedName("route_name")
            var routeName: String = "",
            @SerializedName("status")
            var status: Int = 0
        ):Serializable

        data class SalesOrganisation(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("name")
            var name: String = ""
        ):Serializable

        data class ShipToParty(
            @SerializedName("customer_code")
            var customerCode: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("user")
            var user: User = User(),
            @SerializedName("user_id")
            var userId: Int = 0
        ) :Serializable{
            data class User(
                @SerializedName("firstname")
                var firstname: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("lastname")
                var lastname: String = ""
            ):Serializable
        }

        data class SoldToParty(
            @SerializedName("customer_code")
            var customerCode: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("user")
            var user: User = User(),
            @SerializedName("user_id")
            var userId: Int = 0
        ) :Serializable{
            data class User(
                @SerializedName("firstname")
                var firstname: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("lastname")
                var lastname: String = ""
            ):Serializable
        }

        data class User(
            @SerializedName("country_id")
            var countryId: Int? = 0,
            @SerializedName("email")
            var email: String = "",
            @SerializedName("firstname")
            var firstname: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("lastname")
            var lastname: String = "",
            @SerializedName("mobile")
            var mobile: Any? = Any(),
            @SerializedName("organisation_id")
            var organisationId: Int = 0,
            @SerializedName("parent_id")
            var parentId: String = "",
            @SerializedName("role_id")
            var roleId: Int = 0,
            @SerializedName("status")
            var status: Int = 0,
            @SerializedName("usertype")
            var usertype: Int = 0
        ):Serializable

        data class UserCountry(
            @SerializedName("country")
            var country: Any? = Any(),
            @SerializedName("country_id")
            var countryId: Int? = 0,
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("organisation_id")
            var organisationId: Int = 0
        ):Serializable
    }
}