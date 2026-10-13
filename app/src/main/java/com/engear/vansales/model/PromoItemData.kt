package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class PromoItemData : Parcelable {

    var itemId: String? = null
    var promoGroupId: String? = null
    var promoseq: String? = null
    var fromqty: String? = null
    var toqty: String? = null
    var type :String? = null
    var value :String? = null

    constructor() {}
    public constructor(`in`: Parcel) {
        var promotionId = `in`.readString()
        var orderType = `in`.readString()
        var offerType = `in`.readString()
        var isCombination = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(promoGroupId)
        dest.writeString(itemId)
        dest.writeString(promoseq)
        dest.writeString(fromqty)
        dest.writeString(toqty)
        dest.writeString(type)
        dest.writeString(value)
    }

    companion object {
        val CREATOR: Parcelable.Creator<PromoItemData> =
            object : Parcelable.Creator<PromoItemData> {
                override fun createFromParcel(`in`: Parcel): PromoItemData {
                    return PromoItemData(`in`)
                }

                override fun newArray(size: Int): Array<PromoItemData?> {
                    return arrayOfNulls<PromoItemData>(size)
                }
            }
    }
}