package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class EndoRequestData(
    @SerializedName("errors")
    var errors: List<Any>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: Boolean
)