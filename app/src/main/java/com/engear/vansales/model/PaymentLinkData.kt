package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class PaymentLinkData(
    val status: Boolean,
    val data: List<Any>?,
    val message: String?,
    val errors: List<Any>?,
    val pagination: List<Any>?
)