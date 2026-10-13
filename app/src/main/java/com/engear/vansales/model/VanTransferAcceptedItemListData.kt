package com.engear.vansales.model

import com.google.gson.annotations.SerializedName


data class VanTransferAcceptedItemListData(
    @SerializedName("data")
    val `data`: List<DataItemVanTransfer>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class DataItemVanTransfer(
        @SerializedName("id")
        var id: Int,
        @SerializedName("uuid")
        var uuid: Int,
        @SerializedName("organisation_id")
        var organisation_id: Int,
        @SerializedName("source_route_id")
        var source_route_id: Int,
        @SerializedName("destination_route_id")
        var destination_route_id: Int,
        @SerializedName("code")
        var code: String?,
        @SerializedName("date")
        var date: String?,
        @SerializedName("status")
        var status: String?,
        @SerializedName("sourceroute")
        var sourceroute: SourceRoute? = null,
        @SerializedName("destinationroute")
        var destinationRoute: DestinationRoute? = null,
        @SerializedName("vantovantransferdetail")
        var vantovanTransferDetail: List<VantovanTransferDetail>
    ) {
        data class SourceRoute(
            @SerializedName("id")
            var id: Int,
            @SerializedName("route_name")
            var routeName: String
        )

        data class DestinationRoute(
            @SerializedName("id")
            var id: Int,
            @SerializedName("route_name")
            var destinationRouteName: String
        )

        data class VantovanTransferDetail(
            @SerializedName("id")
            var id: Int,
            @SerializedName("vantovantransfer_id")
            var vantovantransfer_id: Int,
            @SerializedName("item_id")
            var itemId: Int,
            @SerializedName("item_uom_id")
            var itemUomId: Int,
            @SerializedName("quantity")
            var quantity: Int,
            @SerializedName("item")
            var item: VanItemDetail? = null,
            @SerializedName("item_uom")
            var itemUom: VanItemDetail.VanItemUOM? = null
        ) {
            data class VanItemDetail(
                @SerializedName("id")
                var id: Int,
                @SerializedName("item_name")
                var item_name: String
            ) {
                data class VanItemUOM(
                    @SerializedName("id")
                    var id: Int,
                    @SerializedName("name")
                    var uomName: String,
                    @SerializedName("code")
                    var uomCode: String
                )
            }
        }

    }
}