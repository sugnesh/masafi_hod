package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaymentTermListResponse(
    @SerializedName("status") var status: Boolean = false,
    @SerializedName("data") var data: List<PaymentTermList> = listOf(),
    @SerializedName("message") var message: String = "",
    @SerializedName("errors") var errors: List<Any> = listOf(),
    @SerializedName("pagination") var pagination: List<Any> = listOf()
) : Serializable

data class PaymentTermList(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("uuid") var uuid: String = "",
    @SerializedName("organisation_id") var organisationId: Int = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("number_of_days") var numberOfDays: Int = 0,
    @SerializedName("status") var status: Int = 0
) : Serializable
