package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class ReasonData(
    @SerializedName("data")
    val `data`: List<Reason>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Reason(
        @SerializedName("id")
        var id: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("type")
        var type: String
    )
}