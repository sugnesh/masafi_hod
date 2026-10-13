package com.engear.vansales.model

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName


data class CustomerBalanceData(
    @SerializedName("data")
    val `data`: CustomerBalance?,
    @SerializedName("errors")
    val errors: List<Any?>? = listOf(),
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("status")
    val status: Boolean? = false
) {

    class CustomerBalance(
        @SerializedName("credit_limit")
        var credit_limit: String? = "",

        @SerializedName("total_exposure")
        val total_exposure: String? = "",
        var availableBalance:String=""

        )
}