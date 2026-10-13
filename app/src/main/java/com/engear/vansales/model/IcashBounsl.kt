package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class IcashBounsl(
    @SerializedName("data")
    val `data`: List<BonusData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class BonusData(
        @SerializedName("topup_value")
        var topup_value: String,
        @SerializedName("bonus_value")
        var bonus_value: String
    )
}