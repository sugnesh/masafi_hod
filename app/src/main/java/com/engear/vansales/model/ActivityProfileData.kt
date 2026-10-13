package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class ActivityProfileData(
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
        @SerializedName("merchandiser_id")
        var cusotmer_id: String,
        @SerializedName("salesman_activity_profile_detail")
        var profileData: List<ProfileData>
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