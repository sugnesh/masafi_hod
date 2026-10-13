package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class CustomerResponseData(
    @SerializedName("data")
    val `data`: CustomerResponse?,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class CustomerResponse(
        @SerializedName("user_id")
        var user_id: Int,
        @SerializedName("customer_code")
        var customer_code: String?
    )

}