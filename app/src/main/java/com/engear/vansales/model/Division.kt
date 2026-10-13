package com.engear.vansales.model

class Division : ArrayList<DivisionItem>()

data class DivisionItem(
    val amount: Int,
    val balance: String,
    val channel: Channel,
    val channel_id: Int,
    val created_at: String,
    val credit_days: Int,
    val credit_limit: String,
    val customer_category_id: Int,
    val customer_group: Any,
    val customer_group_id: Int,
    val customer_info_id: Int,
    val customer_type_id: Int,
    val deleted_at: Any,
    val id: Int,
    val lob: Lob,
    val lob_id: Int,
    val merchandiser_id: Any,
    val organisation_id: Int,
    val payment_term: PaymentTerm,
    val payment_term_id: Int,
    val region: Region,
    val region_id: Int,
    val route: SupervisorRoute,
    val route_id: Int,
    val sales_organisation: SalesOrganisation,
    val sales_organisation_id: Int,
    val updated_at: String,
    val uuid: String
)



data class Channel(
    val created_at: String,
    val deleted_at: Any,
    val id: Int,
    val name: String,
    val node_level: Int,
    val organisation_id: Int,
    val parent_id: Any,
    val status: Int,
    val updated_at: String,
    val uuid: String
)

data class Lob(
    val id: Int,
    val name: String
)

data class PaymentTerm(
    val id: Int,
    val name: String,
    val number_of_days: Int
)

data class Region(
    val id: Int,
    val region_name: String
)

data class Route(
    val area_id: Int,
    val depot_id: Int,
    val id: Int,
    val route_name: String
)

data class SalesOrganisation(
    val id: Int,
    val name: String,
    val node_level: Int,
    val parent_id: Int
)
