package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class CustomerData(
    @SerializedName("data")
    val `data`: List<DataCustomer>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class DataCustomer(
        @SerializedName("id")
        var id: Int,
        @SerializedName("user_id")
        var user_id: Int,
        @SerializedName("region_id")
        var region_id: Int,
        @SerializedName("route_id")
        var route_id: String?,
        @SerializedName("payment_term_id")
        var payment_term_id: Int,
        @SerializedName("customer_type_id")
        var customer_type_id: String?,
        @SerializedName("customer_category_id")
        var customer_category_id: String?,
        @SerializedName("customer_group_id")
        var customer_group_id: String?,
        @SerializedName("sales_organisation_id")
        var sales_organisation_id: String? = null,
        @SerializedName("sub_secondary_channel_id")
        var sub_channel: String? = null,
        @SerializedName("channel_id")
        var channel_id: String,
        @SerializedName("customer_code")
        var customer_code: String,
        @SerializedName("customer_address_1")
        var customer_address_1: String,
        @SerializedName("customer_address_2")
        var customer_address_2: String? = null,
        @SerializedName("customer_city")
        var customer_city: String? = null,
        @SerializedName("customer_state")
        var customer_state: String? = null,
        @SerializedName("customer_zipcode")
        var customer_zipcode: String? = null,
        @SerializedName("customer_phone")
        var customer_phone: String? = null,
        @SerializedName("credit_limit")
        var credit_limit: String,
        @SerializedName("credit_days")
        var credit_days: String? = null,
        @SerializedName("user")
        var userDetail: UserDetail,
        var cust_sale: String,
        var cust_delviery: String,
        var cust_delviery_time: String,
        var cust_order: String,
        var cust_return: String,
        var cust_coll: String,
        var cust_merchan: String,
        @SerializedName("customer_address_1_lat")
        var latitude: String? = null,
        @SerializedName("customer_address_1_lang")
        var longitude: String? = null,
        @SerializedName("is_lob")
        var isLob: Int = 0,
        @SerializedName("sales_enable")
        var salesEnable: String,
        @SerializedName("return_enable")
        var returnEnable: String,
        @SerializedName("collection_enable")
        var collectionEnable: String,
        @SerializedName("print_enable")
        var printEnable: String,
        var isOverDue: String,
        var planSeq: String,
        @SerializedName("sales_organisation")
        var salesOrg: SalesORG? = null,
        @SerializedName("channel")
        var channel: Channel? = null,
        @SerializedName("region")
        var region: Region? = null,
        @SerializedName("payment_term")
        var payment_term: PaymentTerm? = null,
        @SerializedName("ship_to_party")
        var ship_to_party: PayerDetail? = null,
        @SerializedName("sold_to_party")
        var sold_to_party: PayerDetail? = null,
        @SerializedName("payer")
        var payer: PayerDetail? = null,
        @SerializedName("bill_to_payer")
        var bill_to_payer: PayerDetail? = null,
        @SerializedName("customerlob")
        val customerlob: List<Customerlob>? = null,
        @SerializedName("customer_phone_1")
        val customer_phone_1: String? = null,
        @SerializedName("customer_phone_2")
        val customer_phone_2: String? = null,
        @SerializedName("customer_phone_3")
        val customer_phone_3: String? = null,
        var isCheck: Boolean = false,
        var isOutstanding: Boolean = false,
        @SerializedName("due_on")
        var due_on: Int = 0,
        @SerializedName("trn_no")
        var trn_no: String = "",
        @SerializedName("radius")
        var radius: String = "",
        @SerializedName("no_of_invoice")
        var no_of_invoice: Int = 0,
        @SerializedName("sales_geo_checkin")
        var sales_geo_checkin: String = "0",
        @SerializedName("return_geo_checkin")
        var return_geo_checkin: String = "0",
        @SerializedName("collection_geo_checkin")
        var collection_geo_checkin: String = "0",
        @SerializedName("capture_location_enable")
        var capture_location_enable: String = "0",
        @SerializedName("geo_checking")
        var geo_checking: String = "0",
        @SerializedName("customer_block_types")
        var customer_block_types: List<BlockType>? = null,
        @SerializedName("parent_customer")
        var parent_customer: ParentCustomer? = null,
        @SerializedName("approved_account_release")
        var accountRelease: ApproveDays? = null,
        @SerializedName("approved_credit_limit")
        var approveCreditLimit: ApproveLimit? = null,
        @SerializedName("customer_category")
        var customerCategory: CustomerCategory? = null,
        @SerializedName("sub_secondary_channel")
        var subSecChannel: SubSecChannel? = null
    ) {

        data class ParentCustomer(
            @SerializedName("firstname")
            var firstname: String,
            @SerializedName("lastname")
            var lastname: String,
            @SerializedName("customer_code")
            var customer_code: String
        )

        data class ApproveLimit(
            @SerializedName("approved_amount")
            var approved_amount: String,
            @SerializedName("no_of_days_approved")
            var no_of_days_approved: String,
            @SerializedName("end_date")
            var end_date: String,
            @SerializedName("total_outstanding")
            var total_outstanding: String
        )

        data class CustomerCategory(
            @SerializedName("customer_category_name")
            var name: String,
            @SerializedName("id")
            var id: Int
        )

        data class SubSecChannel(
            @SerializedName("sub_secondary_channele_name")
            var name: String,
            @SerializedName("id")
            var id: Int
        )

        data class ApproveDays(
            @SerializedName("approved_amount")
            var approved_amount: String,
            @SerializedName("no_of_days_approved")
            var no_of_days_approved: String,
            @SerializedName("end_date")
            var end_date: String
        )

        data class Customerlob(
            val amount: Int,
            val balance: String?,
            val channel_id: Int,
            val created_at: String,
            val credit_days: Int,
            val credit_limit: String,
            val customer_category_id: Int?,
            val customer_group_id: Int?,
            val customer_info_id: Int,
            val customer_type_id: Int,
            val deleted_at: String,
            val id: Int,
            val lob_id: Int,
            val merchandiser_id: String,
            val organisation_id: Int,
            val payment_term_id: Int?,
            val region_id: Int,
            val route_id: Int,
            val sales_organisation_id: Int?,
            val updated_at: String,
            val uuid: String,
            val lob: Lob,
            var due_on: Int,
            //val route: SupervisorRoute? = null,
            val region: Region? = null,
            val channel: Channel? = null,
            val sales_organisation: SalesOrganisation? = null,
            val payment_term: PaymentTerm? = null,
            var customer_block_types: List<BlockType>? = null
        )

        data class UserDetail(
            @SerializedName("firstname")
            var firstname: String,
            @SerializedName("lastname")
            var lastname: String,
            @SerializedName("id")
            var id: Int,
            @SerializedName("email")
            var email: String
        )

        data class SalesORG(
            @SerializedName("name")
            var name: String,
            @SerializedName("id")
            var id: Int
        )

        data class Channel(
            @SerializedName("name")
            var name: String,
            @SerializedName("id")
            var id: Int
        )

        data class Region(
            @SerializedName("region_name")
            var name: String,
            @SerializedName("id")
            var id: Int
        )

        data class PaymentTerm(
            @SerializedName("name")
            var name: String,
            @SerializedName("id")
            var id: Int
        )

        data class PayerDetail(
            @SerializedName("user_id")
            var user_id: String,
            @SerializedName("customer_code")
            var customer_code: String,
            @SerializedName("customer_address_1")
            var customer_address_1: String,
            @SerializedName("customer_address_2")
            var customer_address_2: String,
            @SerializedName("customer_city")
            var city: String,
            @SerializedName("customer_state")
            var state: String,
            @SerializedName("customer_zipcode")
            var zipCode: String
        )

        data class Lob(
            val id: String,
            val name: String
        )

        data class BlockType(
            @SerializedName("customer_id")
            var customer_id: String,
            @SerializedName("type")
            var type: String,
            @SerializedName("customer_lob_id")
            var customer_lob_id: String,
            @SerializedName("is_block")
            var is_block: String
        )
    }
}