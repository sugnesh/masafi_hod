package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class VisitData(
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
        @SerializedName("added_on")
        var addedOn: String = "",
        @SerializedName("comment")
        var comment: Any? = Any(),
        @SerializedName("completed_task")
        var completedTask: Int = 0,
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("customer")
        var customer: Customer = Customer(),
        @SerializedName("customer_id")
        var customerId: Int = 0,
        @SerializedName("date")
        var date: String = "",
        @SerializedName("deleted_at")
        var deletedAt: Any? = Any(),
        @SerializedName("end_time")
        var endTime: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("is_sequnece")
        var isSequnece: String = "",
        @SerializedName("journey_plan_id")
        var journeyPlanId: Int = 0,
        @SerializedName("latitude")
        var latitude: String = "",
        @SerializedName("longitude")
        var longitude: String = "",
        @SerializedName("organisation_id")
        var organisationId: Int = 0,
        @SerializedName("reason")
        var reason: Any? = Any(),
        @SerializedName("route")
        var route: Route = Route(),
        @SerializedName("route_id")
        var routeId: Int = 0,
        @SerializedName("salesman")
        var salesman: Salesman = Salesman(),
        @SerializedName("salesman_id")
        var salesmanId: Int = 0,
        @SerializedName("shop_status")
        var shopStatus: String = "",
        @SerializedName("start_time")
        var startTime: String = "",
        @SerializedName("status")
        var status: Int = 0,
        @SerializedName("total_task")
        var totalTask: Int = 0,
        @SerializedName("trip")
        var trip: Trip = Trip(),
        @SerializedName("trip_id")
        var tripId: Int = 0,
        @SerializedName("updated_at")
        var updatedAt: String = "",
        @SerializedName("uuid")
        var uuid: String = "",
        @SerializedName("visit_total_time")
        var visitTotalTime: String = ""
    ) {
        data class Customer(
            @SerializedName("customerinfo")
            var customerinfo: Customerinfo = Customerinfo(),
            @SerializedName("firstname")
            var firstname: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("lastname")
            var lastname: String = ""
        ) {
            data class Customerinfo(
                @SerializedName("customer_code")
                var customerCode: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("user_id")
                var userId: Int = 0
            )
        }

        data class Route(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("route_code")
            var routeCode: String = "",
            @SerializedName("route_name")
            var routeName: String = ""
        )

        data class Salesman(
            @SerializedName("firstname")
            var firstname: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("lastname")
            var lastname: String = "",
            @SerializedName("salesmaninfo")
            var salesmaninfo: Salesmaninfo = Salesmaninfo()
        ) {
            data class Salesmaninfo(
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("salesman_code")
                var salesmanCode: String = "",
                @SerializedName("user_id")
                var userId: Int = 0
            )
        }

        data class Trip(
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("deleted_at")
            var deletedAt: Any? = Any(),
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("organisation_id")
            var organisationId: Int = 0,
            @SerializedName("route_id")
            var routeId: Int = 0,
            @SerializedName("salesman_id")
            var salesmanId: Int = 0,
            @SerializedName("trip_end")
            var tripEnd: String = "",
            @SerializedName("trip_end_date")
            var tripEndDate: String = "",
            @SerializedName("trip_end_time")
            var tripEndTime: String = "",
            @SerializedName("trip_from")
            var tripFrom: Int = 0,
            @SerializedName("trip_start")
            var tripStart: String = "",
            @SerializedName("trip_start_date")
            var tripStartDate: String = "",
            @SerializedName("trip_start_time")
            var tripStartTime: String = "",
            @SerializedName("trip_status")
            var tripStatus: Int = 0,
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("uuid")
            var uuid: String = ""
        )
    }
}