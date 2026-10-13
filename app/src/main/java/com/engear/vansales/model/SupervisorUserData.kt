package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

data class SupervisorUserData(
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
        @SerializedName("accessToken")
        var accessToken: String = "",
        @SerializedName("permissions-name")
        var permissionsName: List<PermissionsName> = listOf(),
        @SerializedName("user_info")
        var userInfo: UserInfo = UserInfo()
    ) {
        data class PermissionsName(
            @SerializedName("moduleName")
            var moduleName: String = "",
            @SerializedName("permissions")
            var permissions: List<Permission> = listOf()
        ) {
            data class Permission(
                @SerializedName("name")
                var name: String = ""
            )
        }

        data class UserInfo(
            @SerializedName("api_token")
            var apiToken: String = "",
            @SerializedName("country_id")
            var countryId: Int = 0,
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
            @SerializedName("organisation")
            var organisation: Organisation = Organisation(),
            @SerializedName("organisation_id")
            var organisationId: Int = 0,
            @SerializedName("parent_id")
            var parentId: Any? = Any(),
            @SerializedName("role")
            var role: Role = Role(),
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
        ) {
            data class Organisation(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("is_trial_period")
                var isTrialPeriod: Int = 0,
                @SerializedName("org_logo")
                var orgLogo: String = "",
                @SerializedName("org_name")
                var orgName: String = ""
            )

            data class Role(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("name")
                var name: String = ""
            )
        }
    }
}