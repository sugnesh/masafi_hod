package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class MyErrorMessage(
    @SerializedName("errors")
    val errors: Error?,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Error(
        @SerializedName("error")
        var error: String
    )
}