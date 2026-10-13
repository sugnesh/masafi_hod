package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AssignData(
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
)
