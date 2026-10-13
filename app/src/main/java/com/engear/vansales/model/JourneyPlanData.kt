package com.engear.vansales.model

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName


data class JourneyPlanData(
    @SerializedName("data")
    val `data`: JourneyPlan?,
    @SerializedName("errors")
    val errors: List<Any?>? = listOf(),
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("status")
    val status: Boolean? = false
) {
    class JourneyPlan(
        @SerializedName("created_at")
        val createdAt: String? = "",

        @SerializedName("current_stage")
        val currentStage: String? = "",

        @SerializedName("current_stage_comment")
        val currentStageComment: Any? = Any(),

        @SerializedName("description")
        val description: String? = "",

        @SerializedName("end_date")
        val endDate: String? = "",

        @SerializedName("end_time")
        val endTime: String? = "",

        @SerializedName("id")
        val id: Int? = 0,

        @SerializedName("journey_plan_weeks")
        val journeyPlanWeeks: MutableList<JourneyPlanWeek?>? = ArrayList(),

        @SerializedName("journey_plan_days")
        val journeyPlanDays: MutableList<JourneyPlanWeek.JourneyPlanDay?>? = ArrayList(),

        @SerializedName("name")
        val name: String? = "",

        @SerializedName("no_end_date")
        val noEndDate: Int? = 0,

        @SerializedName("organisation_id")
        val organisationId: Int? = 0,

        @SerializedName("plan_type")
        val planType: String? = "",

        @SerializedName("is_enforce")
        val is_enforce: String? = "",

        @SerializedName("route")
        val route: Any? = Any(),

        @SerializedName("route_id")
        val routeId: Int? = 0,

        @SerializedName("start_date")
        val startDate: String? = "",

        @SerializedName("start_day_of_the_week")
        val startDayOfTheWeek: Int? = 0,

        @SerializedName("start_time")
        val startTime: String? = "",

        @SerializedName("status")
        val status: Int? = 0,

        @SerializedName("updated_at")
        val updatedAt: String? = "",

        @SerializedName("uuid")
        val uuid: String? = "",

        @SerializedName("week_1")
        val week1: Int? = 0,

        @SerializedName("week_2")
        val week2: Int? = 0,

        @SerializedName("week_3")
        val week3: Int? = 0,

        @SerializedName("week_4")
        val week4: Int? = 0,

        @SerializedName("week_5")
        val week5: Int? = 0
    ) {
        data class JourneyPlanWeek(
            @SerializedName("id")
            val id: Int? = 0,

            @SerializedName("journey_plan_days")
            val journeyPlanDays: MutableList<JourneyPlanDay?>? = ArrayList(),

            @SerializedName("journey_plan_id")
            val journeyPlanId: Int? = 0,

            @SerializedName("uuid")
            val uuid: String? = "",

            @SerializedName("week_number")
            val weekNumber: String? = ""
        ) {
            data class JourneyPlanDay(
                @SerializedName("day_name")
                val dayName: String? = "",

                @SerializedName("day_number")
                val dayNumber: Int? = 0,

                @SerializedName("id")
                val id: Int? = 0,

                @SerializedName("journey_plan_customers")
                val journeyPlanCustomers: MutableList<JourneyPlanCustomer?>? = ArrayList(),

                @SerializedName("journey_plan_id")
                val journeyPlanId: Int? = 0,

                @SerializedName("journey_plan_week_id")
                val journeyPlanWeekId: Int? = 0,

                @SerializedName("uuid")
                val uuid: String? = ""
            ) {
                data class JourneyPlanCustomer(
                    @SerializedName("customer_id")
                    val customerId: Int? = 0,

                    @Embedded(prefix = "customer_info_")
                    @SerializedName("customer_info")
                    val customerInfo: CustomerInfo? = CustomerInfo(),

                    @SerializedName("day_customer_sequence")
                    val dayCustomerSequence: Int? = 0,

                    @SerializedName("day_end_time")
                    val dayEndTime: Any? = Any(),

                    @SerializedName("day_start_time")
                    val dayStartTime: Any? = Any(),

                    @SerializedName("id")
                    val id: Int? = 0,

                    @SerializedName("journey_plan_day_id")
                    val journeyPlanDayId: Int? = 0
                ) {
                    data class CustomerInfo(
                        @SerializedName("id")
                        val id: Int? = 0,

                        @SerializedName("user")
                        val user: Any? = Any(),

                        @SerializedName("user_id")
                        val userId: Int? = 0
                    )
                }
            }
        }
    }
}