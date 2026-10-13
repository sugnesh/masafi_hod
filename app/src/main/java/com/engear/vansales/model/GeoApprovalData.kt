package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GeoApprovalData(
    @SerializedName("data")
    var `data`: DataGeoLocation,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {

    data class DataGeoLocation(
        @SerializedName("id")
        var id: Int,
        @SerializedName("status")
        var data_status: String?,
        @SerializedName("type")
        var type: String?,
        @SerializedName("uuid")
        var uuid: String?,
        @SerializedName("notification_id")
        var notification_id: String?
    )
}
