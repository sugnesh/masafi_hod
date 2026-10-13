package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class PromoOfferData : Parcelable {

    var promotionId: String? = null
    var promotionName: String? = null
    var promotionSequace: String? = null
    var orderType: String? = null
    var offerType: String? = null
    var isApply :String? = null
    var offerQty :String? = null
    var offerUom:String? = null
    var orderItem: ArrayList<ItemData.Data> = ArrayList()
    var offerItem: ArrayList<ItemData.Data> = ArrayList()

    var orderPromo: ArrayList<PromoItemData> = ArrayList()
    var offerPromo: ArrayList<PromoItemData> = ArrayList()
    var isCombination: String? = null
    var type: String? = null
    var discType: String? = null


    constructor() {}
    public constructor(`in`: Parcel) {
        var promotionId = `in`.readString()
        var orderType = `in`.readString()
        var offerType = `in`.readString()
        var isCombination = `in`.readString()
        var isApply = `in`.readString()
        var offerQty = `in`.readString()
        var offerUom = `in`.readString()
        var type = `in`.readString()
        var discType = `in`.readString()
        orderItem = `in`.readList(null!!, null) as ArrayList<ItemData.Data>
        offerItem = `in`.readList(null!!, null) as ArrayList<ItemData.Data>
        orderPromo = `in`.readList(null!!, null) as ArrayList<PromoItemData>
        offerPromo = `in`.readList(null!!, null) as ArrayList<PromoItemData>
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(promotionId)
        dest.writeString(orderType)
        dest.writeString(offerType)
        dest.writeString(isCombination)
        dest.writeString(offerQty)
        dest.writeString(offerUom)
        dest.writeString(isApply)
        dest.writeString(type)
        dest.writeString(discType)
        dest.writeList(orderItem as List<*>?)
        dest.writeList(offerItem as List<*>?)
        dest.writeList(orderPromo as List<*>?)
        dest.writeList(offerPromo as List<*>?)
    }

    companion object {
        val CREATOR: Parcelable.Creator<PromoOfferData> =
            object : Parcelable.Creator<PromoOfferData> {
                override fun createFromParcel(`in`: Parcel): PromoOfferData {
                    return PromoOfferData(`in`)
                }

                override fun newArray(size: Int): Array<PromoOfferData?> {
                    return arrayOfNulls<PromoOfferData>(size)
                }
            }
    }
}