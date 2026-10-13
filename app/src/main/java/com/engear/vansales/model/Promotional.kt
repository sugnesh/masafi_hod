package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Promotional : Parcelable {

    var customerName: String? = null
    var customerPhone: String? = null
    var promotionId: String? = null
    var promotionAcountId: String? = null
    var freeItem: String? = null
    var amount: String? = null
    var invoiceNo: String? = null
    var invoiceImage: String? = null
    var category: String? = null
    var items: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        customerName = `in`.readString()
        customerPhone = `in`.readString()
        promotionId = `in`.readString()
        promotionAcountId = `in`.readString()
        freeItem = `in`.readString()
        amount = `in`.readString()
        invoiceNo = `in`.readString()
        invoiceImage = `in`.readString()
        category = `in`.readString()
        items = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(customerName)
        dest.writeString(customerPhone)
        dest.writeString(promotionId)
        dest.writeString(promotionAcountId)
        dest.writeString(freeItem)
        dest.writeString(amount)
        dest.writeString(invoiceNo)
        dest.writeString(invoiceImage)
        dest.writeString(category)
        dest.writeString(items)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Promotional> = object : Parcelable.Creator<Promotional> {
            override fun createFromParcel(`in`: Parcel): Promotional {
                return Promotional(`in`)
            }

            override fun newArray(size: Int): Array<Promotional?> {
                return arrayOfNulls<Promotional>(size)
            }
        }
    }
}