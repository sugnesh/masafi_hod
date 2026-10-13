package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SubChannelListResponse(
    @SerializedName("status") var status: Boolean = false,
    @SerializedName("data") var data: List<ChannelSubList> = listOf(),
    @SerializedName("message") var message: String = "",
    @SerializedName("errors") var errors: List<Any> = listOf(),
    @SerializedName("pagination") var pagination: Int = 0
) : Serializable

data class ChannelSubList(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("uuid") var uuid: String = "",
    @SerializedName("organisation_id") var organisationId: Int = 0,
    @SerializedName("customer_category_code") var customerCategoryCode: String? = null,
    @SerializedName("parent_id") var parentId: Int? = null,
    @SerializedName("node_level") var nodeLevel: Int = 0,
    @SerializedName("customer_category_name") var customerCategoryName: String? = null,
    @SerializedName("status") var status: Int = 0,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("children") var children: List<ChannelSubList> = listOf(),
    var displayLevel: Int = 0
) : Serializable
