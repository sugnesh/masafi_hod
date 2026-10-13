package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class TaskData(
    @SerializedName("data")
    val `data`: List<Task>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Task(
        @SerializedName("title")
        var title: String,
        @SerializedName("description")
        var description: String,
        @SerializedName("date")
        var date: String,
        @SerializedName("time")
        var time: String,
        @SerializedName("isAllDay")
        var isAllDay: String
    )
}