package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class DiscountOfferData : Parcelable {
    var planId: String? = null
    var discountMainType: String? = null
    var discountType: String? = null
    var discountValue: String? = null
    var discountPercentage: String? = null
    var discountApply: String? = null
    var discountTo: String? = null


    constructor() {}
    public constructor(`in`: Parcel) {
        var planId = `in`.readString()
        var discountMainType = `in`.readString()
        var discountType = `in`.readString()
        var discountValue = `in`.readString()
        var discountPercentage = `in`.readString()
        var discountApply = `in`.readString()
        var discountTo = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(planId)
        dest.writeString(discountMainType)
        dest.writeString(discountType)
        dest.writeString(discountValue)
        dest.writeString(discountPercentage)
        dest.writeString(discountApply)
        dest.writeString(discountTo)

    }

    companion object {
        val CREATOR: Parcelable.Creator<DiscountOfferData> =
            object : Parcelable.Creator<DiscountOfferData> {
                override fun createFromParcel(`in`: Parcel): DiscountOfferData {
                    return DiscountOfferData(`in`)
                }

                override fun newArray(size: Int): Array<DiscountOfferData?> {
                    return arrayOfNulls<DiscountOfferData>(size)
                }
            }
    }
}