package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class CustomerHODAddress(
    @SerializedName("data")
    val `data`: CustomerInfoAddress,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class CustomerInfoAddress(
        @SerializedName("id")
        var itemId: Int,
        @SerializedName("customer_address")
        var customer_address: List<CustomerHIAddress>
    ) {

        data class CustomerHIAddress(
            @SerializedName("id")
            var id: String,
            @SerializedName("title")
            var title: String,
            @SerializedName("address_1")
            var address_1: String,
            @SerializedName("address_2")
            var address_2: String,
            @SerializedName("city")
            var city: String,
            @SerializedName("landmark_id")
            var landmark_id: String,
            @SerializedName("landmarks")
            var landmarks: Landmarks,

            ) {
            data class Landmarks(
                @SerializedName("id")
                var id: String,
                @SerializedName("name")
                var name: String,
                @SerializedName("region")
                var region: RegionLandMark
            ) {
                data class RegionLandMark(
                    @SerializedName("region_code")
                    var region_code: String
                )
            }
        }

    }
}