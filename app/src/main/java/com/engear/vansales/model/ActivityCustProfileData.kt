package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class ActivityCustProfileData(
    @SerializedName("data")
    val `data`: List<Activity>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Activity(
        @SerializedName("user_id")
        var user_id: String,
        @SerializedName("salesman_activity_profiles")
        var custDetail: List<CustData>
    ) {
        data class CustData(
            @SerializedName("id")
            var id: String,
            @SerializedName("activity_name")
            var activity_name: String,
            @SerializedName("salesman_activity_profile_detail")
            var profileDetail: List<ProfileData>
        ) {
            data class ProfileData(
                @SerializedName("id")
                var id: String,
                @SerializedName("module_name")
                var name: String,
                @SerializedName("status")
                var status: String,
                @SerializedName("priority")
                var priority: String
            )
        }

    }
}