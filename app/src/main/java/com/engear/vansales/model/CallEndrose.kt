package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

 data class CallEndrose(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("errors")
    var errors: List<Any> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("pagination")
    var pagination: List<Any> = listOf(),
    @SerializedName("status")
    var status: Boolean = false
) {
    data class Data(
        @SerializedName("salesman_id")
        var salesmanId: Int = 0,
        @SerializedName("schedule_call")
        var scheduleCall: Int = 0,
        @SerializedName("schedule_sale")
        var scheduleSale: Int = 0,
        @SerializedName("unschedule_call")
        var unscheduleCall: Int = 0,
        @SerializedName("unschedule_sale")
        var unscheduleSale: Any? = Any(),
        @SerializedName("Visited_count")
        var visitedCount: Int = 0
    )
}