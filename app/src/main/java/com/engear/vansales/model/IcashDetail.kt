package com.engear.vansales.model

data class IcashDetail(
    val status: Boolean,
    val data: Data,
    val message: String,
    val errors: List<Any>,  // Assuming errors is a list of something; adjust accordingly.
    val pagination: Pagination
)

data class Data(
    val customer_data: CustomerData1,
    val transaction_history: List<Transaction1>
)

data class CustomerData1(
    val customer_name: String,
    val customer_code: String?,
    val mobile: String?,
    val email: String?,
    val site_code: String?,
    val account_number: String,
    val activated_on: String,
    val balance: String,
    val bonus: String,
    val status: String,
    val id: Int
)

data class Transaction1(
    val id: Int,
    val date: String,
    val time: String,
    val customer_id: Int,
    val transaction_number: String,
    val type: String,
    val opening_balance: String,
    val credit: String,
    val debit: String,
    val closing_balance: String,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String?
)

data class Pagination(
    val total_pages: Int,
    val record_per_page: Int,
    val total_records: Int
)
