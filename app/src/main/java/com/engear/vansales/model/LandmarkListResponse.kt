package com.engear.vansales.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LandmarkListResponse(
    @SerializedName("status") var status: Boolean = false,
    @SerializedName("data") var data: List<LandmarkList> = listOf(),
) : Serializable

data class LandmarkList(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("landmark_id") var landmarkId: Int = 0,
    @SerializedName("name") var landmarkName: String? = null,
) : Serializable
