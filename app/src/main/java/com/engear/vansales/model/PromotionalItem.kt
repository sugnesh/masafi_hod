package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class PromotionalItem : Parcelable {

    var promotionId: String? = null
    var itemName: String? = null
    var amount: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        promotionId = `in`.readString()
        itemName = `in`.readString()
        amount = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(promotionId)
        dest.writeString(itemName)
        dest.writeString(amount)
    }

    companion object {
        val CREATOR: Parcelable.Creator<PromotionalItem> = object : Parcelable.Creator<PromotionalItem> {
            override fun createFromParcel(`in`: Parcel): PromotionalItem {
                return PromotionalItem(`in`)
            }

            override fun newArray(size: Int): Array<PromotionalItem?> {
                return arrayOfNulls<PromotionalItem>(size)
            }
        }
    }
}