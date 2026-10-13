package com.engear.vansales.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ItemLoadSPMonitoring(
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
) : Serializable {
    data class Data(
        @SerializedName("item_id")
        var item_id: Int = 0,
        @SerializedName("load_qty")
        var load_qty: String = "",
        @SerializedName("unload_qty")
        var unload_qty: String = "",
        @SerializedName("item_uom")
        var item_uom: String = "",
        @SerializedName("item")
        var item: ITEMDATA = ITEMDATA(),
        @SerializedName("item_u_o_m")
        var itemuom: ITEMUOM = ITEMUOM()
    ) : Serializable {
        data class ITEMDATA(
            @SerializedName("item_name")
            var item_name: String = "",
            @SerializedName("item_code")
            var itemCode: String = ""
        )

        data class ITEMUOM(
            @SerializedName("name")
            var name: String = ""
        )
    }
}