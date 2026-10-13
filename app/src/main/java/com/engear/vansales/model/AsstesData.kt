package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class AsstesData(
    @SerializedName("data")
    val `data`: List<AsstData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class AsstData(
        @SerializedName("user_id")
        var cusotmer_id: String,
        @SerializedName("asset_tracking")
        var assets: List<AssetsDetail>
    ) {
        data class AssetsDetail(
            @SerializedName("id")
            var id: String,
            @SerializedName("title")
            var name: String,
            @SerializedName("image")
            var image: String,
            @SerializedName("category")
            var assetCode: String,
            @SerializedName("model_name")
            var type: String
        )
    }
}