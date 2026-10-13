package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class PlanogramData(
    @SerializedName("data")
    val `data`: List<PlanoData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class PlanoData(
        @SerializedName("cusotmer_id")
        var cusotmer_id: String,
        @SerializedName("planogram")
        var planogram: List<PlanogramMain>
    ) {
        data class PlanogramMain(
            @SerializedName("id")
            var id: String,
            @SerializedName("name")
            var name: String,
            @SerializedName("distribution")
            var distribution: List<DistributionPlano>
        ) {
            data class DistributionPlano(
                @SerializedName("id")
                var id: String,
                @SerializedName("name")
                var name: String,
                @SerializedName("images")
                var image: List<ImageDistribution>
            ) {
                data class ImageDistribution(
                    @SerializedName("id")
                    var id: String,
                    @SerializedName("planogram_id")
                    var planogram_id: String,
                    @SerializedName("image_string")
                    var image_string: String
                )
            }
        }
    }
}