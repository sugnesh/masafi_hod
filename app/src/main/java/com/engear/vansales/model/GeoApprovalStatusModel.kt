package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GeoApprovalStatusModel(
    @SerializedName("status")
    val status: Boolean?,
    @SerializedName("data")
    val data: List<GeoApprovalStatusModelDetails>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("errors")
    val errors: List<Any>?,
    @SerializedName("pagination")
    val pagination: Pagination?
) : Serializable {

    data class GeoApprovalStatusModelDetails(
        @SerializedName("id")
        val id: Int,
        @SerializedName("uuid")
        val uuid: String?,
        @SerializedName("organisation_id")
        val organisationId: Int?,
        @SerializedName("user_id")
        val userId: Int?,
        @SerializedName("other")
        val other: String?,
        @SerializedName("url")
        val url: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("loadrequest_id")
        val loadRequestId: String?,
        @SerializedName("is_read")
        val isRead: Int?,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("approval_status")
        val approvalStatus: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("salesman_lat")
        val salesmanLat: String?,
        @SerializedName("salesman_long")
        val salesmanLong: String?,
        @SerializedName("customer_lat")
        val customerLat: String?,
        @SerializedName("customer_long")
        val customerLong: String?,
        @SerializedName("radius")
        val radius: String?,
        @SerializedName("salesman_id")
        val salesmanId: Int?,
        @SerializedName("supervisor_id")
        val supervisorId: Int?,
        @SerializedName("reason")
        val reason: String?,
        @SerializedName("customer_id")
        val customerId: Int?,
        @SerializedName("lastvisitcustomer_id")
        val lastVisitCustomerId: Int?,
        @SerializedName("salesman")
        val salesman: Salesman?,
        @SerializedName("supervisor")
        val supervisor: Supervisor?,
        @SerializedName("invoice_no")
        val invoiceNo: String?,
        @SerializedName("route")
        val route: Route?,
        @SerializedName("customer")
        val customer: Customer?,
        @SerializedName("lastvisitcustomer")
        val lastVisitCustomer: Customer?,
        @SerializedName("skip_customers")
        val skipCustomers: List<SkipCustomer>?,
        @SerializedName("credit_limit_approval")
        val creditLimitApproval: List<Any>?,
        @SerializedName("creditnote")
        val creditNote: String?
    ) : Serializable {

        data class Salesman(
            @SerializedName("id")
            val id: Int,
            @SerializedName("firstname")
            val firstName: String?,
            @SerializedName("lastname")
            val lastName: String?,
            @SerializedName("salesmanInfo")
            val salesmanInfo: SalesmanInfo?
        ) : Serializable {

            data class SalesmanInfo(
                @SerializedName("id")
                val id: Int,
                @SerializedName("user_id")
                val userId: Int?,
                @SerializedName("salesman_code")
                val salesmanCode: String?
            ) : Serializable
        }

        data class Supervisor(
            @SerializedName("id")
            val id: Int,
            @SerializedName("firstname")
            val firstName: String?,
            @SerializedName("lastname")
            val lastName: String?
        ) : Serializable

        data class Route(
            @SerializedName("id")
            val id: Int,
            @SerializedName("route_code")
            val routeCode: String?,
            @SerializedName("route_name")
            val routeName: String?
        ) : Serializable

        data class Customer(
            @SerializedName("id")
            val id: Int,
            @SerializedName("firstname")
            val firstName: String?,
            @SerializedName("lastname")
            val lastName: String?,
            @SerializedName("customer_info")
            val customerInfo: CustomerInfo?
        ) : Serializable {

            data class CustomerInfo(
                @SerializedName("id")
                val id: Int,
                @SerializedName("user_id")
                val userId: Int?,
                @SerializedName("customer_code")
                val customerCode: String?
            ) : Serializable
        }

        data class SkipCustomer(
            @SerializedName("id")
            val id: Int,
            @SerializedName("uuid")
            val uuid: String?,
            @SerializedName("geoapproval_id")
            val geoApprovalId: Int?,
            @SerializedName("customer_id")
            val customerId: Int?,
            @SerializedName("sequence")
            val sequence: String?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("updated_at")
            val updatedAt: String?,
            @SerializedName("deleted_at")
            val deletedAt: String?,
            @SerializedName("customer")
            val customer: Customer?
        ) : Serializable
    }

    data class Pagination(
        @SerializedName("total_pages")
        val totalPages: Int?,
        @SerializedName("current_page")
        val currentPage: Int?,
        @SerializedName("total_records")
        val totalRecords: Int?
    ) : Serializable
}
