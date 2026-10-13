package com.engear.vansales.model


data class ApprovalRes(
    var `data`: List<Data?>?,
    var errors: List<Any?>?,
    var message: String?,
    var pagination: List<Any?>?,
    var status: Boolean?
) {
    data class Data(
        var approval_stage: Int?,
        var approval_status: String?,
        var approval_type: String?,
        var approved_amount: String?,
        var comment: String?,
        var created_at: String?,
        var credit_limit_approval_details: List<CreditLimitApprovalDetail?>?,
        var creditlimit_doa_id: Int?,
        var customer: Customer?,
        var customer_id: Int?,
        var deleted_at: Any?,
        var end_date: Any?,
        var geoapproval_id: Int?,
        var id: Int?,
        var invoice_id: Int?,
        var level1_approve: Any?,
        var level1_user: Int?,
        var level2_approve: Any?,
        var level2_user: Int?,
        var level3_approve: Any?,
        var level3_user: Int?,
        var level4_approve: Any?,
        var level4_user: Int?,
        var level5_approve: Any?,
        var level5_user: Any?,
        var level6_approve: Any?,
        var level6_user: Any?,
        var no_of_days_approved: Any?,
        var no_of_days_requested: Int?,
        var overdue_amount: String?,
        var overdue_days: String?,
        var requested_amount: String?,
        var salesman: Salesman?,
        var salesman_id: Int?,
        var updated_at: String?,
        var uuid: String?
    ) {
        data class CreditLimitApprovalDetail(
            var approval_status: String?,
            var approved_amount: String?,
            var comment: String?,
            var creditlimit_approval_id: Int?,
            var created_at: String?,
            var deleted_at: Any?,
            var end_date: Any?,
            var id: Int?,
            var no_of_days_approved: Int?,
            var total_outstanding: Any?,
            var updated_at: String?,
            var user: User?,
            var user_id: Int?,
            var user_level: Int?,
            var uuid: String?
        ) {
            data class User(
                var firstname: String?,
                var id: Int?,
                var lastname: String?
            )
        }

        data class Customer(
            var customer_code: String?,
            var id: Int?,
            var user: User?,
            var user_id: Int?
        ) {
            data class User(
                var firstname: String?,
                var id: Int?,
                var lastname: String?
            )
        }

        data class Salesman(
            var id: Int?,
            var salesman_code: String?,
            var user: User?,
            var user_id: Int?
        ) {
            data class User(
                var firstname: String?,
                var id: Int?,
                var lastname: String?
            )
        }
    }
}