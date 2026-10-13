package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class NodeBrand(
    @SerializedName("data")
    val `data`: List<NodeData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class NodeData(
        @SerializedName("id")
        var id: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("brand_name")
        var brand_name: String,
        @SerializedName("children")
        var subNode: List<ChildNodeData>
    ) {
        data class ChildNodeData(
            @SerializedName("id")
            var id: String,
            @SerializedName("name")
            var name: String,
            @SerializedName("brand_name")
            var brand_name: String,
            @SerializedName("parent_id")
            var parent_id: String,
            @SerializedName("node_level")
            var node_level: String
        )
    }
}