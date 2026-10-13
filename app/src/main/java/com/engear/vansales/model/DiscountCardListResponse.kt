package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DiscountCardListResponse(
    @SerializedName("status") var status: Boolean = false,
    @SerializedName("data") var data: List<DiscountCardList> = listOf(),
    @SerializedName("message") var message: String = "",
    @SerializedName("errors") var errors: List<Any> = listOf(),
) : Serializable

data class DiscountCardList(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("card_types") var customerCategoryName: String? = null

) : Serializable
