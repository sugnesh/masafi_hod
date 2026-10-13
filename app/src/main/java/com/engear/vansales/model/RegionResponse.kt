package com.engear.vansales.model

data class RegionResponse(
    val status: Boolean,
    val data: RegionData,
    val message: String,
    val errors: List<Any>,
    val pagination: List<Any>
)

data class RegionData(
    val region: List<RegionList>
)

data class RegionList(
    val id: Int,
    val region_name: String,
    val region_status: Int,
    val country_id: Int
)
