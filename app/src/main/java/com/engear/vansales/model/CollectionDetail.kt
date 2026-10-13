package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class CollectionDetail : Parcelable {

    var collectionNo: String? = null
    var invId: String? = null
    var invNo: String? = null
    var paidAmt: String? = null
    var customerId: String? = null
    var type: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        collectionNo = `in`.readString()
        paidAmt = `in`.readString()
        customerId = `in`.readString()
        invNo = `in`.readString()
        type = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(collectionNo)
        dest.writeString(paidAmt)
        dest.writeString(customerId)
        dest.writeString(invNo)
        dest.writeString(type)

    }

    companion object {
        val CREATOR: Parcelable.Creator<CollectionDetail> = object : Parcelable.Creator<CollectionDetail> {
            override fun createFromParcel(`in`: Parcel): CollectionDetail {
                return CollectionDetail(`in`)
            }

            override fun newArray(size: Int): Array<CollectionDetail?> {
                return arrayOfNulls<CollectionDetail>(size)
            }
        }
    }
}