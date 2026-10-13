package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class AllUOMData(
    @SerializedName("data")
    var `data`: List<DataUOM>?,
    @SerializedName("errors")
    var errors: List<Any>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)

data class DataUOM(
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String?
)
