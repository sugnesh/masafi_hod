package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PLoadResponse(
    @SerializedName("data")
    var `data`: List<Any> ,
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Boolean = false
)