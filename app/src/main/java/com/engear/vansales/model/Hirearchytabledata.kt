package com.engear.vansales.model

data class Hirearchytabledata(
    val status: Boolean,
    val data: List<ApprovalData>,
    val message: String
)

data class ApprovalData(
    val id: Int,
    val uuid: String,
    val creditlimit_approval_id: Int,
    val user_id: Int,
    val user_level: Int,
    val approval_status: String,
    val no_of_days_approved: Int,
    val end_date: String?,
    val approved_amount: String,
    val comment: String?,
    val total_outstanding: String?,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String?,
    val user: User
)

data class User(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val role_id: Int,
    val role: UserRole
)

data class UserRole(
    val id: Int,
    val name: String
)
