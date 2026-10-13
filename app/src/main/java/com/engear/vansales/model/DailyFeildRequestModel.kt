package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DailyFeildRequestModel(
    @SerializedName("customers")
    var customers: ArrayList<Customer> = ArrayList(),
    @SerializedName("date")
    var date: String = "",
    @SerializedName("lob_id")
    var lobId: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("supervisor_id")
    var supervisorId: Int = 0,
    var isDailyFeildPosted: String = ""
):Serializable {
    data class Customer(
        @SerializedName("customer_id")
        var customerId: Int = 0,
        @SerializedName("daily_activity_details")
        var dailyActivityDetails: List<DailyActivityDetail> = listOf(),
        @SerializedName("off_shelf_display")
        var offShelfDisplay: String = "",
        @SerializedName("opportunity")
        var opportunity: String = "",
        @SerializedName("out_of_stock")
        var outOfStock: String = "",
        @SerializedName("remarks")
        var remarks: String = "",
        @SerializedName("shelf_display")
        var shelfDisplay: String = ""
    ) : Serializable {
        data class DailyActivityDetail(
            @SerializedName("supervisor_category_id")
            var supervisorCategoryId: Int = 0,
            @SerializedName("supervisor_category_status")
            var supervisorCategoryStatus: Int = 0
        ) : Serializable
    }
}