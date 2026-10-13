package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SubSecTradeChannelResponse(
    @SerializedName("status") var status: Boolean = false,
    @SerializedName("data") var data: List<SubChannelList> = listOf(),
    @SerializedName("message") var message: String = "",
    @SerializedName("errors") var errors: List<Any> = listOf(),
    @SerializedName("pagination") var pagination: String = ""
) : Serializable

data class SubChannelList(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("organisation_id") var organisationId: Int = 0,
    @SerializedName("sub_secondary_channele_name") var subSecondaryChannelName: String = "",
    @SerializedName("node_level") var nodeLevel: Int = 0,
    @SerializedName("status") var status: Int = 0
) : Serializable
