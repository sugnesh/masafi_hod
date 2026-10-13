package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChannelListResponse(
    @SerializedName("status") var status: Boolean = false,
    @SerializedName("data") var data: List<ChannelList> = listOf(),
    @SerializedName("message") var message: String = "",
    @SerializedName("errors") var errors: List<Any> = listOf(),
    @SerializedName("pagination") var pagination: List<Any> = listOf()
) : Serializable

data class ChannelList(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("uuid") var uuid: String = "",
    @SerializedName("organisation_id") var organisationId: Int = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("node_level") var nodeLevel: Int = 0,
    @SerializedName("status") var status: Int = 0,
    @SerializedName("children") var children: List<ChannelList> = listOf(),
    var displayLevel: Int = 0
) : Serializable

