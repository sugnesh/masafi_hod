package com.engear.vansales.model


import com.google.gson.annotations.SerializedName

data class SupervisorRoute(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("errors")
    var errors: List<Any> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("pagination")
    var pagination: List<Any> = listOf(),
    @SerializedName("status")
    var status: Boolean? = false
) {
    data class Data(
        @SerializedName("route")
        var route: Route = Route(),
        @SerializedName("route_id")
        var routeId: Int = 0,
        @SerializedName("user")
        var salesman: Salesman = Salesman()
    ) {

        data class Route(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("route_code")
            var routeCode: String = "",
            @SerializedName("route_name")
            var routeName: String = ""
        )

        data class Salesman(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("firstname")
            var firstname: String = "",
            @SerializedName("lastname")
            var lastname: String = ""
        )
    }
}